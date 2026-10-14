package com.aegis.bridge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple10;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public final class BridgeServer {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final BridgeConfig config;
    private final FiscoContext fisco;
    private final HttpServer server;

    private BridgeServer(BridgeConfig config, FiscoContext fisco) throws Exception {
        this.config = config;
        this.fisco = fisco;
        this.server = HttpServer.create(
            new InetSocketAddress(config.host, config.port), 0
        );
        this.server.setExecutor(Executors.newFixedThreadPool(8));
        registerRoutes();
    }

    public static void main(String[] args) throws Exception {
        BridgeConfig config = BridgeConfig.load();
        FiscoContext fisco = new FiscoContext(config);
        BridgeServer bridge = new BridgeServer(config, fisco);
        bridge.server.start();

        System.out.println("AEGIS_FISCO_BRIDGE=READY");
        System.out.println("LISTEN=" + config.host + ":" + config.port);
        System.out.println("GROUP=group0");
        System.out.println("CHAIN_ID=" + fisco.client.getChainId());
        System.out.println("BLOCK_NUMBER=" + fisco.client.getBlockNumber().getBlockNumber());
        System.out.println("SIGNER=" + fisco.signer.getAddress());
        System.out.println("AGENT_WALLET=" + config.agentWalletAddress);
    }

    private void registerRoutes() {
        server.createContext("/health", this::health);
        server.createContext("/wallet/balance", this::walletBalance);
        server.createContext("/wallet/state", this::walletState);
        server.createContext("/wallet/transfer", this::walletTransfer);
        server.createContext("/risk/spent", this::riskSpent);
        server.createContext("/poi/", this::poiExists);
        server.createContext("/transaction/", this::transactionReceipt);
    }

    private void health(HttpExchange exchange) throws IOException {
        if (!method(exchange, "GET")) {
            return;
        }
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("status", "ok");
            result.put("chainId", fisco.client.getChainId());
            result.put("groupId", "group0");
            result.put("blockNumber", fisco.client.getBlockNumber().getBlockNumber());
            result.put("signer", fisco.signer.getAddress());
            result.put("agentWallet", config.agentWalletAddress);
            send(exchange, 200, result);
        } catch (Exception e) {
            error(exchange, 500, "HEALTH_CHECK_FAILED", e);
        }
    }

    private void walletBalance(HttpExchange exchange) throws IOException {
        if (!method(exchange, "GET")) {
            return;
        }
        if (!authorized(exchange)) {
            return;
        }
        try {
            BigInteger balance = fisco.walletBalance();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("wallet", config.agentWalletAddress);
            result.put("token", config.tokenAddress);
            result.put("symbol", "ACNY");
            result.put("decimals", 2);
            result.put("balanceMinor", balance.toString());
            send(exchange, 200, result);
        } catch (Exception e) {
            error(exchange, 500, "BALANCE_QUERY_FAILED", e);
        }
    }

    private void walletState(HttpExchange exchange) throws IOException {
        if (!method(exchange, "GET")) {
            return;
        }
        if (!authorized(exchange)) {
            return;
        }
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("wallet", config.agentWalletAddress);
            result.put("owner", fisco.agentWallet.owner());
            result.put("agentExecutor", fisco.agentWallet.agentExecutor());
            result.put("localSigner", fisco.signer.getAddress());
            result.put("paused", fisco.riskEngine.paused());
            result.put("balanceMinor", fisco.walletBalance().toString());
            result.put("spentTodayMinor", fisco.spentToday().toString());
            send(exchange, 200, result);
        } catch (Exception e) {
            error(exchange, 500, "WALLET_STATE_FAILED", e);
        }
    }

    private void riskSpent(HttpExchange exchange) throws IOException {
        if (!method(exchange, "GET")) {
            return;
        }
        if (!authorized(exchange)) {
            return;
        }
        try {
            Map<String, Object> result = Map.of(
                "wallet", config.agentWalletAddress,
                "spentTodayMinor", fisco.spentToday().toString()
            );
            send(exchange, 200, result);
        } catch (Exception e) {
            error(exchange, 500, "RISK_QUERY_FAILED", e);
        }
    }

    private void poiExists(HttpExchange exchange) throws IOException {
        if (!method(exchange, "GET")) {
            return;
        }
        if (!authorized(exchange)) {
            return;
        }
        try {
            String path = exchange.getRequestURI().getPath();
            String hash = path.substring("/poi/".length());
            if (!HexUtil.isBytes32(hash)) {
                send(exchange, 400, Map.of("error", "INVALID_POI_HASH"));
                return;
            }
            boolean exists = fisco.poiRegistry.exists(HexUtil.bytes32(hash));
            if (!exists) {
                send(exchange, 200, Map.of("poiHash", hash, "exists", false));
                return;
            }

            Tuple10<byte[], byte[], String, String, String, BigInteger, BigInteger, BigInteger, BigInteger, Boolean> record =
                fisco.poiRegistry.getPoI(HexUtil.bytes32(hash));

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("poiHash", hash);
            result.put("exists", record.getValue10());
            result.put("intentId", HexUtil.hex(record.getValue1()));
            result.put("policyHash", HexUtil.hex(record.getValue2()));
            result.put("wallet", record.getValue3());
            result.put("token", record.getValue4());
            result.put("recipient", record.getValue5());
            result.put("amountMinor", record.getValue6().toString());
            result.put("riskScoreBps", record.getValue7().toString());
            result.put("timestamp", record.getValue8().toString());
            result.put("blockNumber", record.getValue9().toString());

            send(exchange, 200, result);
        } catch (Exception e) {
            error(exchange, 500, "POI_QUERY_FAILED", e);
        }
    }

    private void walletTransfer(HttpExchange exchange) throws IOException {
        if (!method(exchange, "POST")) {
            return;
        }
        if (!authorized(exchange)) {
            return;
        }
        try {
            JsonNode body = JSON.readTree(exchange.getRequestBody());
            String recipient = text(body, "recipient");
            String amountRaw = text(body, "amountMinor");
            String poiHash = text(body, "poiHash");
            String intentId = text(body, "intentId");
            String policyHash = text(body, "policyHash");
            String riskRaw = text(body, "riskScoreBps");

            if (!HexUtil.isAddress(recipient)) {
                throw new IllegalArgumentException("Invalid recipient address");
            }
            if (!HexUtil.isBytes32(poiHash) || !HexUtil.isBytes32(intentId) || !HexUtil.isBytes32(policyHash)) {
                throw new IllegalArgumentException("poiHash, intentId and policyHash must be bytes32");
            }

            BigInteger amount = new BigInteger(amountRaw);
            BigInteger risk = new BigInteger(riskRaw);

            if (amount.compareTo(BigInteger.ZERO) <= 0) {
                throw new IllegalArgumentException("amountMinor must be > 0");
            }
            if (risk.compareTo(BigInteger.ZERO) < 0 || risk.compareTo(BigInteger.valueOf(10000)) > 0) {
                throw new IllegalArgumentException("riskScoreBps must be between 0 and 10000");
            }

            TransactionReceipt receipt = fisco.transfer(
                recipient, amount, poiHash, intentId, policyHash, risk
            );
            Map<String, Object> result = receiptJson(receipt);
            result.put("success", receipt.isStatusOK());
            result.put("poiHash", poiHash);

            if (!receipt.isStatusOK()) {
                send(exchange, 422, result);
                return;
            }
            send(exchange, 200, result);
        } catch (IllegalArgumentException e) {
            error(exchange, 400, "INVALID_REQUEST", e);
        } catch (Exception e) {
            error(exchange, 422, "TRANSACTION_REJECTED", e);
        }
    }

    private void transactionReceipt(HttpExchange exchange) throws IOException {
        if (!method(exchange, "GET")) {
            return;
        }
        if (!authorized(exchange)) {
            return;
        }
        try {
            String path = exchange.getRequestURI().getPath();
            String txHash = path.substring("/transaction/".length());
            if (!HexUtil.isTxHash(txHash)) {
                send(exchange, 400, Map.of("error", "INVALID_TRANSACTION_HASH"));
                return;
            }
            BcosTransactionReceipt response = fisco.client.getTransactionReceipt(txHash, false);
            TransactionReceipt receipt = response.getResult();
            if (receipt == null) {
                send(exchange, 404, Map.of("error", "TRANSACTION_NOT_FOUND"));
                return;
            }
            send(exchange, 200, receiptJson(receipt));
        } catch (Exception e) {
            error(exchange, 500, "TRANSACTION_QUERY_FAILED", e);
        }
    }

    private boolean authorized(HttpExchange exchange) throws IOException {
        String provided = exchange.getRequestHeaders().getFirst("X-Aegis-Bridge-Key");
        if (provided == null || !constantTimeEquals(provided, config.bridgeKey)) {
            send(exchange, 401, Map.of("error", "UNAUTHORIZED"));
            return false;
        }
        return true;
    }

    private static boolean constantTimeEquals(String a, String b) {
        byte[] x = a.getBytes(StandardCharsets.UTF_8);
        byte[] y = b.getBytes(StandardCharsets.UTF_8);
        if (x.length != y.length) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < x.length; i++) {
            diff |= x[i] ^ y[i];
        }
        return diff == 0;
    }

    private static boolean method(HttpExchange exchange, String expected) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase(expected)) {
            send(exchange, 405, Map.of("error", "METHOD_NOT_ALLOWED"));
            return false;
        }
        return true;
    }

    private static String text(JsonNode body, String key) {
        JsonNode value = body.get(key);
        if (value == null || value.asText().isBlank()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value.asText();
    }

    private static Map<String, Object> receiptJson(TransactionReceipt receipt) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("transactionHash", receipt.getTransactionHash());
        result.put("blockNumber", receipt.getBlockNumber());
        result.put("status", receipt.getStatus());
        result.put("from", receipt.getFrom());
        result.put("to", receipt.getTo());
        result.put("contractAddress", receipt.getContractAddress());
        result.put("message", receipt.getMessage());
        result.put("logCount", receipt.getLogEntries() == null ? 0 : receipt.getLogEntries().size());
        return result;
    }

    private static void error(HttpExchange exchange, int status, String code, Exception exception) throws IOException {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", code);
        response.put("message", exception.getMessage());
        send(exchange, status, response);
    }

    private static void send(HttpExchange exchange, int status, Object payload) throws IOException {
        byte[] bytes = JSON.writeValueAsBytes(payload);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}

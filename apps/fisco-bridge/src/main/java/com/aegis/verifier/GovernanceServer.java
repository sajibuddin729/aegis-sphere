package com.aegis.verifier;

import com.aegis.bridge.HexUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple8;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public final class GovernanceServer {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final String bridgeKey;
    private final String host;
    private final int port;
    private final GovernanceContext context;
    private final HttpServer server;

    private GovernanceServer(String bridgeKey, String host, int port, GovernanceContext context) throws Exception {
        this.bridgeKey = bridgeKey;
        this.host = host;
        this.port = port;
        this.context = context;
        this.server = HttpServer.create(new InetSocketAddress(host, port), 0);
        this.server.setExecutor(Executors.newFixedThreadPool(4));
        registerRoutes();
    }

    public static void main(String[] args) throws Exception {
        String bridgeKey = System.getenv("AEGIS_GOVERNANCE_BRIDGE_KEY");
        if (bridgeKey == null || bridgeKey.trim().length() < 32) {
            throw new IllegalStateException("AEGIS_GOVERNANCE_BRIDGE_KEY is required and must be >= 32 chars");
        }

        String host = System.getenv().getOrDefault("AEGIS_GOVERNANCE_BRIDGE_HOST", "127.0.0.1");
        int port = Integer.parseInt(System.getenv().getOrDefault("AEGIS_GOVERNANCE_BRIDGE_PORT", "9090"));

        if (!host.equals("127.0.0.1")) {
            throw new IllegalStateException("Governance bridge must bind to 127.0.0.1");
        }

        GovernanceContext context = new GovernanceContext();
        GovernanceServer server = new GovernanceServer(bridgeKey, host, port, context);
        server.server.start();

        System.out.println("SERVICE=aegis-governance-verifier");
        System.out.println("HOST=" + host);
        System.out.println("PORT=" + port);
        System.out.println("SIGNER=" + context.verifierSigner.getAddress());
        System.out.println("UPDATE_GOVERNANCE=" + context.governanceAddress);
    }

    private void registerRoutes() {
        server.createContext("/health", this::health);
        server.createContext("/governance/decision", this::governanceDecision);
    }

    private void health(HttpExchange exchange) throws IOException {
        if (!method(exchange, "GET")) {
            return;
        }
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("status", "ok");
            result.put("service", "aegis-governance-verifier");
            result.put("chainId", context.client.getChainId());
            result.put("groupId", "group0");
            result.put("blockNumber", context.client.getBlockNumber().getBlockNumber());
            result.put("verifier", context.verifierSigner.getAddress());
            result.put("UpdateGovernance", context.governanceAddress);
            send(exchange, 200, result);
        } catch (Exception e) {
            error(exchange, 500, "HEALTH_CHECK_FAILED", e);
        }
    }

    private void governanceDecision(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (!authorized(exchange)) {
            return;
        }

        try {
            if (method.equalsIgnoreCase("POST") && path.equals("/governance/decision")) {
                JsonNode body = JSON.readTree(exchange.getRequestBody());
                if (body == null) {
                    send(exchange, 400, Map.of("error", "INVALID_REQUEST_BODY"));
                    return;
                }

                String candidateHash = text(body, "candidateHash");
                String reportHash = text(body, "reportHash");
                String attestationHash = text(body, "attestationHash");
                String verifierVersionHash = text(body, "verifierVersionHash");

                String[] hashes = {candidateHash, reportHash, attestationHash, verifierVersionHash};
                for (String h : hashes) {
                    if (!HexUtil.isBytes32(h)) {
                        throw new IllegalArgumentException("Hash must be bytes32: " + h);
                    }
                }

                JsonNode decisionNode = body.get("decisionValue");
                if (decisionNode == null || !decisionNode.isInt()) {
                    throw new IllegalArgumentException("decisionValue must be an integer (1 or 2)");
                }
                int decisionValue = decisionNode.asInt();
                if (decisionValue != 1 && decisionValue != 2) {
                    throw new IllegalArgumentException("decisionValue must be 1 (APPROVED) or 2 (REJECTED)");
                }

                TransactionReceipt receipt = context.updateGovernance.recordDecision(
                    HexUtil.bytes32(candidateHash),
                    HexUtil.bytes32(reportHash),
                    HexUtil.bytes32(attestationHash),
                    HexUtil.bytes32(verifierVersionHash),
                    BigInteger.valueOf(decisionValue)
                );

                Map<String, Object> result = receiptJson(receipt);
                result.put("success", receipt.isStatusOK());
                result.put("candidateHash", candidateHash);
                result.put("decisionValue", decisionValue);

                if (!receipt.isStatusOK()) {
                    send(exchange, 422, result);
                    return;
                }

                send(exchange, 200, result);
                return;
            }

            if (method.equalsIgnoreCase("GET") && path.startsWith("/governance/decision/")) {
                String candidateHash = path.substring("/governance/decision/".length());
                if (!HexUtil.isBytes32(candidateHash)) {
                    send(exchange, 400, Map.of("error", "INVALID_CANDIDATE_HASH"));
                    return;
                }

                Tuple8<byte[], byte[], byte[], String, BigInteger, BigInteger, BigInteger, Boolean> tuple =
                    context.updateGovernance.getDecision(HexUtil.bytes32(candidateHash));

                Map<String, Object> result = new LinkedHashMap<>();
                result.put("candidateHash", candidateHash);
                result.put("exists", tuple.getValue8());
                result.put("reportHash", HexUtil.hex(tuple.getValue1()));
                result.put("attestationHash", HexUtil.hex(tuple.getValue2()));
                result.put("verifierVersionHash", HexUtil.hex(tuple.getValue3()));
                result.put("verifier", tuple.getValue4());
                result.put("decision", tuple.getValue5().intValue());
                result.put("timestamp", tuple.getValue6().toString());
                result.put("blockNumber", tuple.getValue7().toString());

                send(exchange, 200, result);
                return;
            }

            send(exchange, 405, Map.of("error", "METHOD_NOT_ALLOWED"));

        } catch (IllegalArgumentException e) {
            error(exchange, 400, "INVALID_GOVERNANCE_REQUEST", e);
        } catch (Exception e) {
            error(exchange, 422, "GOVERNANCE_EXECUTION_REJECTED", e);
        }
    }

    private boolean authorized(HttpExchange exchange) throws IOException {
        String provided = exchange.getRequestHeaders().getFirst("X-Aegis-Governance-Key");
        if (provided == null || !constantTimeEquals(provided, this.bridgeKey)) {
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
        int result = 0;
        for (int i = 0; i < x.length; i++) {
            result |= x[i] ^ y[i];
        }
        return result == 0;
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
        response.put("message", exception != null ? exception.getMessage() : "Unknown error");
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

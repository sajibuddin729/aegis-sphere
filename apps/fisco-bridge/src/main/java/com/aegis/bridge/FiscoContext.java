package com.aegis.bridge;

import com.aegis.bridge.contracts.AegisToken;
import com.aegis.bridge.contracts.AgentWallet;
import com.aegis.bridge.contracts.AuditEvidenceRegistry;
import com.aegis.bridge.contracts.PoIRegistry;
import com.aegis.bridge.contracts.PortfolioManager;
import com.aegis.bridge.contracts.RiskCircuitBreaker;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FiscoContext {

    public final BridgeConfig config;
    public final BcosSDK sdk;
    public final Client client;
    public final CryptoKeyPair signer;

    public final AegisToken token;
    public final PoIRegistry poiRegistry;
    public final RiskCircuitBreaker riskEngine;
    public final AgentWallet agentWallet;
    public final PortfolioManager portfolioManager;
    public final AuditEvidenceRegistry auditEvidenceRegistry;
    public final EmergencyOracleClient oracleClient;

    public FiscoContext(BridgeConfig config) throws Exception {
        this.config = config;
        this.oracleClient = new EmergencyOracleClient(
            config.oracleUrl,
            config.oracleKey,
            config.oracleRequired
        );
        this.sdk = BcosSDK.build(config.fiscoConfigPath);
        this.client = sdk.getClient("group0");

        client.getCryptoSuite()
            .loadAccount("pem", config.accountPemPath, null);
        this.signer = client.getCryptoSuite().getCryptoKeyPair();

        this.token = AegisToken.load(config.tokenAddress, client, signer);
        this.poiRegistry = PoIRegistry.load(config.poiRegistryAddress, client, signer);
        this.riskEngine = RiskCircuitBreaker.load(config.riskAddress, client, signer);
        this.agentWallet = AgentWallet.load(config.agentWalletAddress, client, signer);
        this.portfolioManager = PortfolioManager.load(
            config.portfolioManagerAddress,
            client,
            signer
        );
        this.auditEvidenceRegistry = AuditEvidenceRegistry.load(
            config.auditEvidenceRegistryAddress,
            client,
            signer
        );

        verifyBinding();
    }

    private void verifyBinding() throws Exception {
        String onChainExecutor = agentWallet.agentExecutor();
        String localSigner = signer.getAddress();

        if (!onChainExecutor.equalsIgnoreCase(localSigner)) {
            throw new IllegalStateException(
                "Signer does not match AgentWallet.agentExecutor. "
                + "Local=" + localSigner + ", OnChain=" + onChainExecutor
            );
        }
    }

    public BigInteger walletBalance() throws Exception {
        return token.balanceOf(config.agentWalletAddress);
    }

    public BigInteger recipientBalance(String recipient) throws Exception {
        return token.balanceOf(recipient);
    }

    public BigInteger spentToday() throws Exception {
        // Despite the generated Java parameter name,
        // the deployed Solidity function expects
        // the wallet address.
        return riskEngine.getSpentToday(config.agentWalletAddress);
    }

    public TransactionReceipt transfer(
        String recipient,
        BigInteger amount,
        String poiHash,
        String intentId,
        String policyHash,
        BigInteger riskScoreBps
    ) throws Exception {
        oracleClient.assertFinancialExecutionAllowed();
        return agentWallet.executeTokenTransfer(
            config.tokenAddress,
            recipient,
            amount,
            HexUtil.bytes32(poiHash),
            HexUtil.bytes32(intentId),
            HexUtil.bytes32(policyHash),
            riskScoreBps
        );
    }

    public Map<String, Object> portfolioState() throws Exception {
        var state = portfolioManager.getPortfolioState();

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("manager", config.portfolioManagerAddress);
        result.put("acnyToken", config.tokenAddress);
        result.put("agixToken", config.growthAssetAddress);
        result.put("balanceAcnyMinor", state.getValue1().toString());
        result.put("balanceAgixMinor", state.getValue2().toString());
        result.put("totalValueAcnyMinor", state.getValue3().toString());
        result.put("acnyAllocationBps", state.getValue4().toString());
        result.put("agixAllocationBps", state.getValue5().toString());
        result.put("priceAgixInAcny", config.portfolioPriceBInA.toString());

        return result;
    }

    public TransactionReceipt rebalancePortfolio(
        String direction,
        BigInteger amountIn,
        BigInteger minAmountOut,
        String poiHash,
        String intentId,
        String policyHash,
        BigInteger riskScoreBps
    ) throws Exception {
        oracleClient.assertFinancialExecutionAllowed();

        final String tokenIn;

        if ("ACNY_TO_AGIX".equals(direction)) {
            tokenIn = config.tokenAddress;
        } else if ("AGIX_TO_ACNY".equals(direction)) {
            tokenIn = config.growthAssetAddress;
        } else {
            throw new IllegalArgumentException("Unsupported portfolio direction");
        }

        if (amountIn.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("amountIn must be > 0");
        }

        if (minAmountOut.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("minAmountOut must be > 0");
        }

        return portfolioManager.executeRebalance(
            tokenIn,
            amountIn,
            minAmountOut,
            HexUtil.bytes32(poiHash),
            HexUtil.bytes32(intentId),
            HexUtil.bytes32(policyHash),
            riskScoreBps
        );
    }

    public TransactionReceipt recordAuditEvidence(
        String evidenceHash,
        String policyVersionHash,
        String retrievalSetHash,
        String decisionHash,
        String relatedPoIHash
    ) throws Exception {
        oracleClient.assertFinancialExecutionAllowed();
        return auditEvidenceRegistry.recordEvidence(
            HexUtil.bytes32(evidenceHash),
            HexUtil.bytes32(policyVersionHash),
            HexUtil.bytes32(retrievalSetHash),
            HexUtil.bytes32(decisionHash),
            HexUtil.bytes32(relatedPoIHash)
        );
    }

    public Map<String, Object> getAuditEvidence(
        String evidenceHash
    ) throws Exception {
        byte[] hash = HexUtil.bytes32(evidenceHash);
        boolean exists = auditEvidenceRegistry.exists(hash);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("evidenceHash", evidenceHash);
        result.put("exists", exists);

        if (!exists) {
            return result;
        }

        var record = auditEvidenceRegistry.getEvidence(hash);

        result.put("policyVersionHash", HexUtil.hex(record.getValue1()));
        result.put("retrievalSetHash", HexUtil.hex(record.getValue2()));
        result.put("decisionHash", HexUtil.hex(record.getValue3()));
        result.put("relatedPoIHash", HexUtil.hex(record.getValue4()));
        result.put("recorder", record.getValue5());
        result.put("timestamp", record.getValue6().toString());
        result.put("blockNumber", record.getValue7().toString());
        result.put("recordExists", record.getValue8());

        return result;
    }
}


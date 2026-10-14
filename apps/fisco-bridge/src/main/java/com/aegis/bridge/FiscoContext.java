package com.aegis.bridge;

import com.aegis.bridge.contracts.AegisToken;
import com.aegis.bridge.contracts.AgentWallet;
import com.aegis.bridge.contracts.PoIRegistry;
import com.aegis.bridge.contracts.RiskCircuitBreaker;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;

import java.math.BigInteger;

public final class FiscoContext {

    public final BridgeConfig config;
    public final BcosSDK sdk;
    public final Client client;
    public final CryptoKeyPair signer;

    public final AegisToken token;
    public final PoIRegistry poiRegistry;
    public final RiskCircuitBreaker riskEngine;
    public final AgentWallet agentWallet;

    public FiscoContext(BridgeConfig config) throws Exception {
        this.config = config;
        this.sdk = BcosSDK.build(config.fiscoConfigPath);
        this.client = sdk.getClient("group0");

        client.getCryptoSuite()
            .loadAccount("pem", config.accountPemPath, null);
        this.signer = client.getCryptoSuite().getCryptoKeyPair();

        this.token = AegisToken.load(config.tokenAddress, client, signer);
        this.poiRegistry = PoIRegistry.load(config.poiRegistryAddress, client, signer);
        this.riskEngine = RiskCircuitBreaker.load(config.riskAddress, client, signer);
        this.agentWallet = AgentWallet.load(config.agentWalletAddress, client, signer);

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
}

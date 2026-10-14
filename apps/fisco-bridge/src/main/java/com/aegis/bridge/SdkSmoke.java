package com.aegis.bridge;

import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;

public final class SdkSmoke {
    public static void main(String[] args) throws Exception {
        String configPath = System.getenv("FISCO_CONFIG_PATH");
        String accountPem = System.getenv("FISCO_ACCOUNT_PEM");

        if (configPath == null || configPath.isBlank()) {
            throw new IllegalStateException(
                "FISCO_CONFIG_PATH is required"
            );
        }

        if (accountPem == null || accountPem.isBlank()) {
            throw new IllegalStateException(
                "FISCO_ACCOUNT_PEM is required"
            );
        }

        BcosSDK sdk = BcosSDK.build(configPath);
        Client client = sdk.getClient("group0");

        client.getCryptoSuite().loadAccount(
            "pem",
            accountPem,
            null
        );

        CryptoKeyPair keyPair = client.getCryptoSuite().getCryptoKeyPair();

        System.out.println(
            "AEGIS_FISCO_CONNECTION=OK"
        );
        System.out.println(
            "GROUP=group0"
        );
        System.out.println(
            "CHAIN_ID=" + client.getChainId()
        );
        System.out.println(
            "BLOCK_NUMBER=" + client.getBlockNumber().getBlockNumber()
        );
        System.out.println(
            "SIGNER_ADDRESS=" + keyPair.getAddress()
        );

        System.exit(0);
    }
}

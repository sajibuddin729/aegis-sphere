package com.aegis.verifier;

import com.aegis.verifier.contracts.UpdateGovernance;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;

import java.io.File;

public final class GovernanceContext {

    public final Client client;
    public final CryptoKeyPair verifierSigner;
    public final UpdateGovernance updateGovernance;
    public final String governanceAddress;

    public GovernanceContext() throws Exception {
        String fiscoConfigPath = System.getenv("FISCO_CONFIG_PATH");
        if (fiscoConfigPath == null || fiscoConfigPath.trim().isEmpty()) {
            if (new File("runtime/config.toml").exists()) {
                fiscoConfigPath = "runtime/config.toml";
            } else if (new File("apps/fisco-bridge/runtime/config.toml").exists()) {
                fiscoConfigPath = "apps/fisco-bridge/runtime/config.toml";
            } else {
                throw new IllegalStateException("FISCO_CONFIG_PATH environment variable is required");
            }
        }

        BcosSDK sdk = BcosSDK.build(fiscoConfigPath);
        this.client = sdk.getClient("group0");

        String pemPath = requireEnv("AEGIS_VERIFIER_ACCOUNT_PEM");

        client.getCryptoSuite().loadAccount("pem", pemPath, null);
        this.verifierSigner = client.getCryptoSuite().getCryptoKeyPair();

        String address = verifierSigner.getAddress();

        if (!address.equalsIgnoreCase("0x108a9272627edc87425c0b0fa673395548b68263")) {
            throw new IllegalStateException("VERIFIER_IDENTITY_MISMATCH");
        }

        this.governanceAddress = requireEnv("AEGIS_UPDATE_GOVERNANCE_ADDRESS");

        this.updateGovernance = UpdateGovernance.load(
            governanceAddress,
            client,
            verifierSigner
        );
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value.trim();
    }
}

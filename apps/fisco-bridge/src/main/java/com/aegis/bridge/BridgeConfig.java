package com.aegis.bridge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BridgeConfig {

    public final String fiscoConfigPath;
    public final String accountPemPath;
    public final String deploymentManifestPath;
    public final String bridgeKey;
    public final String host;
    public final int port;

    public final String tokenAddress;
    public final String poiRegistryAddress;
    public final String riskAddress;
    public final String agentWalletAddress;

    private BridgeConfig(
        String fiscoConfigPath,
        String accountPemPath,
        String deploymentManifestPath,
        String bridgeKey,
        String host,
        int port,
        String tokenAddress,
        String poiRegistryAddress,
        String riskAddress,
        String agentWalletAddress
    ) {
        this.fiscoConfigPath = fiscoConfigPath;
        this.accountPemPath = accountPemPath;
        this.deploymentManifestPath = deploymentManifestPath;
        this.bridgeKey = bridgeKey;
        this.host = host;
        this.port = port;
        this.tokenAddress = tokenAddress;
        this.poiRegistryAddress = poiRegistryAddress;
        this.riskAddress = riskAddress;
        this.agentWalletAddress = agentWalletAddress;
    }

    public static BridgeConfig load() throws Exception {
        String fiscoConfig = requireEnv("FISCO_CONFIG_PATH");
        String accountPem = requireEnv("FISCO_ACCOUNT_PEM");
        String manifest = requireEnv("AEGIS_DEPLOYMENT_MANIFEST");
        String bridgeKey = requireEnv("AEGIS_BRIDGE_KEY");

        String host = System.getenv()
            .getOrDefault("AEGIS_BRIDGE_HOST", "127.0.0.1");
        int port = Integer.parseInt(
            System.getenv().getOrDefault("AEGIS_BRIDGE_PORT", "9080")
        );

        if (!host.equals("127.0.0.1")) {
            throw new IllegalStateException(
                "Signing bridge must bind to 127.0.0.1"
            );
        }
        if (bridgeKey.length() < 32) {
            throw new IllegalStateException(
                "AEGIS_BRIDGE_KEY is too short"
            );
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(
            Files.readString(Path.of(manifest))
        );
        JsonNode contracts = root.path("contracts");

        String tokenAddress = contracts.path("AegisToken")
            .path("address")
            .asText();
        String poiAddress = contracts.path("PoIRegistry")
            .path("address")
            .asText();
        String riskAddress = contracts.path("RiskCircuitBreaker")
            .path("address")
            .asText();
        String walletAddress = contracts.path("AgentWallet")
            .path("address")
            .asText();

        validateAddress("AegisToken", tokenAddress);
        validateAddress("PoIRegistry", poiAddress);
        validateAddress("RiskCircuitBreaker", riskAddress);
        validateAddress("AgentWallet", walletAddress);

        return new BridgeConfig(
            fiscoConfig,
            accountPem,
            manifest,
            bridgeKey,
            host,
            port,
            tokenAddress,
            poiAddress,
            riskAddress,
            walletAddress
        );
    }

    private static String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(key + " is required");
        }
        return value;
    }

    private static void validateAddress(String name, String address) {
        if (!HexUtil.isAddress(address)) {
            throw new IllegalStateException(
                "Invalid address for " + name + ": " + address
            );
        }
    }
}

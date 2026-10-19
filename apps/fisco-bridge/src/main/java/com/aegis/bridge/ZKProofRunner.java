package com.aegis.bridge;

import com.aegis.bridge.contracts.ZKEligibilityGate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ZKProofRunner {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        String gateAddress = args.length > 0 ? args[0] : "0x717570d925f97a1bc4a852b61fe89b46a1294b2b";
        String calldataPath = args.length > 1 ? args[1] : "zk/runtime/solidity-calldata.txt";
        String mode = args.length > 2 ? args[2] : "verify"; // verify, duplicate, check

        String configPath = System.getenv("FISCO_CONFIG_PATH");
        String accountPem = System.getenv("FISCO_ACCOUNT_PEM");

        BcosSDK sdk = BcosSDK.build(configPath);
        Client client = sdk.getClient("group0");
        client.getCryptoSuite().loadAccount("pem", accountPem, null);
        CryptoKeyPair keyPair = client.getCryptoSuite().getCryptoKeyPair();

        ZKEligibilityGate gate = ZKEligibilityGate.load(gateAddress, client, keyPair);

        String rawCalldata = Files.readString(Path.of(calldataPath)).trim();
        // Wrap in brackets to parse as JSON array of 4 items: [pA, pB, pC, pub]
        JsonNode root = MAPPER.readTree("[" + rawCalldata + "]");

        List<BigInteger> pA = parseBigIntList(root.get(0));
        List<List<BigInteger>> pB = new ArrayList<>();
        for (JsonNode sub : root.get(1)) {
            pB.add(parseBigIntList(sub));
        }
        List<BigInteger> pC = parseBigIntList(root.get(2));
        List<BigInteger> pubSignals = parseBigIntList(root.get(3));

        if ("verify".equalsIgnoreCase(mode)) {
            System.out.println("TESTING_DIRECT_VERIFIER...");
            String verifierAddr = gate.verifier();
            System.out.println("GATE_VERIFIER_ADDRESS=" + verifierAddr);
            com.aegis.bridge.contracts.AegisEligibilityVerifier verifier =
                com.aegis.bridge.contracts.AegisEligibilityVerifier.load(verifierAddr, client, keyPair);

            org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder encoder =
                new org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder(client.getCryptoSuite());
            org.fisco.bcos.sdk.v3.codec.datatypes.Function fn = verifier.getMethodVerifyProofRawFunction(pA, pB, pC, pubSignals);
            System.out.println("METHOD_SIGNATURE=" + org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder.buildMethodSignature(fn.getName(), fn.getInputParameters()));
            byte[] encodedBytes = encoder.encode(fn);
            String encoded = HexUtil.hex(encodedBytes);
            System.out.println("ENCODED_HEX_LEN=" + encoded.length() + " (bytes=" + (encoded.length() - 2) / 2 + ")");
            System.out.println("ENCODED_HEX=" + encoded);

            org.fisco.bcos.sdk.v3.codec.datatypes.Function fnGate = gate.getMethodVerifyAndRecordRawFunction(pA, pB, pC, pubSignals);
            System.out.println("GATE_METHOD_SIGNATURE=" + org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder.buildMethodSignature(fnGate.getName(), fnGate.getInputParameters()));

            System.out.println("SUBMITTING_ZK_PROOF...");
            TransactionReceipt receipt = gate.verifyAndRecord(pA, pB, pC, pubSignals);

            System.out.println("STATUS=" + receipt.getStatus());
            System.out.println("STATUS_OK=" + receipt.isStatusOK());
            System.out.println("TX_HASH=" + receipt.getTransactionHash());
            System.out.println("BLOCK_NUMBER=" + receipt.getBlockNumber());
            System.out.println("OUTPUT=" + receipt.getOutput());
            System.out.println("MESSAGE=" + receipt.getMessage());
            if (receipt.getLogEntries() != null) {
                System.out.println("LOG_COUNT=" + receipt.getLogEntries().size());
            }

            if (!receipt.isStatusOK()) {
                System.exit(1);
            }
        } else if ("duplicate".equalsIgnoreCase(mode)) {
            System.out.println("SUBMITTING_DUPLICATE_PROOF...");
            TransactionReceipt receipt = gate.verifyAndRecord(pA, pB, pC, pubSignals);
            System.out.println("STATUS=" + receipt.getStatus());
            System.out.println("STATUS_OK=" + receipt.isStatusOK());
            System.out.println("MESSAGE=" + receipt.getMessage());
        } else if ("check".equalsIgnoreCase(mode)) {
            String proofHashHex = args.length > 3 ? args[3] : "";
            if (!proofHashHex.startsWith("0x")) {
                proofHashHex = "0x" + proofHashHex;
            }
            byte[] proofHash = HexUtil.bytes32(proofHashHex);
            boolean exists = gate.verifiedProofs(proofHash);
            System.out.println("PROOF_EXISTS=" + exists);
        }
    }

    private static List<BigInteger> parseBigIntList(JsonNode arrayNode) {
        List<BigInteger> list = new ArrayList<>();
        for (JsonNode node : arrayNode) {
            String text = node.asText();
            if (text.startsWith("0x") || text.startsWith("0X")) {
                list.add(new BigInteger(text.substring(2), 16));
            } else {
                list.add(new BigInteger(text));
            }
        }
        return list;
    }
}

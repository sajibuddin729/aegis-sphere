package com.aegis.verifier;

import com.aegis.bridge.HexUtil;
import com.aegis.verifier.contracts.UpdateGovernance;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;

import java.math.BigInteger;

public final class FinancialSignerGovernanceTest {

    public static void main(String[] args) throws Exception {
        String fiscoConfigPath = System.getenv("FISCO_CONFIG_PATH");
        if (fiscoConfigPath == null || fiscoConfigPath.isEmpty()) {
            fiscoConfigPath = new java.io.File("apps/fisco-bridge/runtime/config.toml").getAbsolutePath();
        }
        BcosSDK sdk = BcosSDK.build(fiscoConfigPath);
        Client client = sdk.getClient("group0");

        String pemPath = System.getenv("FISCO_ACCOUNT_PEM");
        if (pemPath == null || pemPath.isEmpty()) {
            pemPath = new java.io.File("apps/fisco-bridge/runtime/account/agent-executor.pem").getAbsolutePath();
        }
        client.getCryptoSuite().loadAccount("pem", pemPath, null);
        CryptoKeyPair financialSigner = client.getCryptoSuite().getCryptoKeyPair();

        String financialAddress = financialSigner.getAddress();
        System.out.println("FINANCIAL_SIGNER=" + financialAddress);

        String governanceAddress = "0x40fce2f6cbef3daa8e671549102d386f75a058ba";
        UpdateGovernance governance = UpdateGovernance.load(governanceAddress, client, financialSigner);

        Boolean isAuth = governance.authorizedVerifiers(financialAddress);
        System.out.println("AUTHORIZED=" + isAuth);

        byte[] dummyCandidateHash = HexUtil.bytes32("0x7777777777777777777777777777777777777777777777777777777777777777");
        byte[] dummyHash = HexUtil.bytes32("0x1111111111111111111111111111111111111111111111111111111111111111");

        TransactionReceipt receipt = governance.recordDecision(
            dummyCandidateHash,
            dummyHash,
            dummyHash,
            dummyHash,
            BigInteger.valueOf(1)
        );

        System.out.println("RECEIPT_STATUS=" + receipt.getStatus());
        System.out.println("RECEIPT_MESSAGE=" + receipt.getMessage());

        Boolean existsAfter = governance.exists(dummyCandidateHash);
        System.out.println("EXISTS_AFTER=" + existsAfter);

        if (receipt.getStatus() == 16 && !existsAfter) {
            System.out.println("FINANCIAL_SIGNER_GOVERNANCE_REJECTION=PASS");
        } else {
            System.out.println("FINANCIAL_SIGNER_GOVERNANCE_REJECTION=FAIL");
        }

        System.exit(0);
    }
}

package com.aegis.bridge.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class ZKEligibilityGate extends Contract {
    public static final String[] BINARY_ARRAY = {"60a060405234801561001057600080fd5b5060405161073d38038061073d83398101604081905261002f916100ac565b6001600160a01b0381166100895760405162461bcd60e51b815260206004820152601460248201527f41454749533a205a45524f5f5645524946494552000000000000000000000000604482015260640160405180910390fd5b600080546001600160a01b031916331790556001600160a01b03166080526100dc565b6000602082840312156100be57600080fd5b81516001600160a01b03811681146100d557600080fd5b9392505050565b6080516106406100fd6000396000818160a401526101f701526106406000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c8063176d80c4146100675780632b7ac3f31461009f5780633778359c146100de5780638da5cb5b146100ff5780638e23ce9d1461011257806391b1b09a14610135575b600080fd5b61008a6100753660046104b3565b60026020526000908152604090205460ff1681565b60405190151581526020015b60405180910390f35b6100c67f000000000000000000000000000000000000000000000000000000000000000081565b6040516001600160a01b039091168152602001610096565b6100f16100ec3660046104f4565b610148565b604051908152602001610096565b6000546100c6906001600160a01b031681565b61008a6101203660046104b3565b60016020526000908152604090205460ff1681565b6100f161014336600461054e565b6103e2565b604080518235602080830191909152808401358284015282518083038401815260609092018352815191810191909120600081815260019092529181205490919060ff166101dd5760405162461bcd60e51b815260206004820152601e60248201527f41454749533a20534e415053484f545f4e4f545f52454749535445524544000060448201526064015b60405180910390fd5b604051635fe8c13b60e01b81526000906001600160a01b037f00000000000000000000000000000000000000000000000000000000000000001690635fe8c13b90610232908a908a908a908a90600401610570565b602060405180830381865afa15801561024f573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061027391906105ce565b9050806102c25760405162461bcd60e51b815260206004820152601760248201527f41454749533a20494e56414c49445f5a4b5f50524f4f4600000000000000000060448201526064016101d4565b868686866040516020016102d99493929190610570565b60408051601f1981840301815291815281516020928301206000818152600290935291205490935060ff16156103515760405162461bcd60e51b815260206004820152601d60248201527f41454749533a2050524f4f465f414c52454144595f5245434f5244454400000060448201526064016101d4565b6000838152600260209081526040808320805460ff191660011790555161037a918791016105f7565b60408051808303601f19018152828252805160209182012088830135845260608901359184019190915292508291859187917f93e76c65f743faa492f0ac3d233b79d4aa9efa8b1be0ce4692c9b27b39ad466b910160405180910390a4505050949350505050565b600080546001600160a01b031633146104305760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064016101d4565b604080516020810185905290810183905260600160408051808303601f1901815282825280516020918201206000818152600180845290849020805460ff19169091179055868452908301859052925082917ff970fe2492be9d122d81266868a1522c0e87044f2688a3c00a830e9d8abc4b3b910160405180910390a292915050565b6000602082840312156104c557600080fd5b5035919050565b80604081018310156104dd57600080fd5b92915050565b80608081018310156104dd57600080fd5b600080600080610180858703121561050b57600080fd5b61051586866104cc565b935061052486604087016104e3565b92506105338660c087016104cc565b91506105438661010087016104e3565b905092959194509250565b6000806040838503121561056157600080fd5b50508035926020909101359150565b61018081016040808784376000838201818152879190815b60028110156105a857848483379084018281529284019290600101610588565b5050828760c0870137610100850192508083526080868437835250909695505050505050565b6000602082840312156105e057600080fd5b815180151581146105f057600080fd5b9392505050565b608081810190838337600081529291505056fea2646970667358221220712c87a2e83c04ec13842ed91118bd4375c34d7a164ccb69a62ab6203776949e64736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"60a060405234801561001057600080fd5b5060405161074238038061074283398101604081905261002f916100ad565b6001600160a01b03811661008a57604051636381e58960e11b815260206004820152601460248201527f41454749533a205a45524f5f5645524946494552000000000000000000000000604482015260640160405180910390fd5b600080546001600160a01b031916331790556001600160a01b03166080526100dd565b6000602082840312156100bf57600080fd5b81516001600160a01b03811681146100d657600080fd5b9392505050565b6080516106446100fe60003960008181609201526101f801526106446000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80632df4c35814610067578063301d0d6c1461008d5780635089e2c8146100cc578063b21d9fb2146100df578063c05621ad146100f2578063d699533914610125575b600080fd5b61007a6100753660046104df565b610148565b6040519081526020015b60405180910390f35b6100b47f000000000000000000000000000000000000000000000000000000000000000081565b6040516001600160a01b039091168152602001610084565b6000546100b4906001600160a01b031681565b61007a6100ed366004610539565b6103e5565b61011561010036600461055b565b60016020526000908152604090205460ff1681565b6040519015158152602001610084565b61011561013336600461055b565b60026020526000908152604090205460ff1681565b604080518235602080830191909152808401358284015282518083038401815260609092018352815191810191909120600081815260019092529181205490919060ff166101de57604051636381e58960e11b815260206004820152601e60248201527f41454749533a20534e415053484f545f4e4f545f52454749535445524544000060448201526064015b60405180910390fd5b6040516301432a8d60e11b81526000906001600160a01b037f00000000000000000000000000000000000000000000000000000000000000001690630286551a90610233908a908a908a908a90600401610574565b602060405180830381865afa158015610250573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061027491906105d2565b9050806102c457604051636381e58960e11b815260206004820152601760248201527f41454749533a20494e56414c49445f5a4b5f50524f4f4600000000000000000060448201526064016101d5565b868686866040516020016102db9493929190610574565b60408051601f1981840301815291815281516020928301206000818152600290935291205490935060ff161561035457604051636381e58960e11b815260206004820152601d60248201527f41454749533a2050524f4f465f414c52454144595f5245434f5244454400000060448201526064016101d5565b6000838152600260209081526040808320805460ff191660011790555161037d918791016105fb565b60408051808303601f19018152828252805160209182012088830135845260608901359184019190915292508291859187917f5513cbea589cf8ff0719c9467c2b0b4c2bfa323ce7b6a07d7b99de052857f262910160405180910390a4505050949350505050565b600080546001600160a01b0316331461043457604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064016101d5565b604080516020810185905290810183905260600160408051808303601f1901815282825280516020918201206000818152600180845290849020805460ff19169091179055868452908301859052925082917f4fc8d98cb727df17fda5feacd834d89634fc1d75554487cce65d464ff06c1d45910160405180910390a292915050565b80604081018310156104c857600080fd5b92915050565b80608081018310156104c857600080fd5b60008060008061018085870312156104f657600080fd5b61050086866104b7565b935061050f86604087016104ce565b925061051e8660c087016104b7565b915061052e8661010087016104ce565b905092959194509250565b6000806040838503121561054c57600080fd5b50508035926020909101359150565b60006020828403121561056d57600080fd5b5035919050565b61018081016040808784376000838201818152879190815b60028110156105ac5784848337908401828152928401929060010161058c565b5050828760c0870137610100850192508083526080868437835250909695505050505050565b6000602082840312156105e457600080fd5b815180151581146105f457600080fd5b9392505050565b608081810190838337600081529291505056fea2646970667358221220db297206f796e5701668a03185e1f81c1c3e54a61dc31203816d367d09e95db864736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"verifierAddress\",\"type\":\"address\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"proofHash\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"snapshotHash\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"publicSignalsHash\",\"type\":\"bytes32\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amountMinor\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"maxRiskScoreBps\",\"type\":\"uint256\"}],\"name\":\"EligibilityProofVerified\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"snapshotHash\",\"type\":\"bytes32\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"balanceCommitment\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"riskCommitment\",\"type\":\"uint256\"}],\"name\":\"SnapshotRegistered\",\"type\":\"event\"},{\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"balanceCommitment\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"riskCommitment\",\"type\":\"uint256\"}],\"name\":\"registerSnapshot\",\"outputs\":[{\"internalType\":\"bytes32\",\"name\":\"snapshotHash\",\"type\":\"bytes32\"}],\"selector\":[2444341402,2988285874],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"registeredSnapshots\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[2384711325,3226870189],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"\",\"type\":\"bytes32\"}],\"name\":\"verifiedProofs\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[393052356,3600372537],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"verifier\",\"outputs\":[{\"internalType\":\"contract IAegisEligibilityVerifier\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[729465843,807210348],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256[2]\",\"name\":\"pA\",\"type\":\"uint256[2]\"},{\"internalType\":\"uint256[2][2]\",\"name\":\"pB\",\"type\":\"uint256[2][2]\"},{\"internalType\":\"uint256[2]\",\"name\":\"pC\",\"type\":\"uint256[2]\"},{\"internalType\":\"uint256[4]\",\"name\":\"pubSignals\",\"type\":\"uint256[4]\"}],\"name\":\"verifyAndRecord\",\"outputs\":[{\"internalType\":\"bytes32\",\"name\":\"proofHash\",\"type\":\"bytes32\"}],\"selector\":[930624924,771015512],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_REGISTERSNAPSHOT = "registerSnapshot";

    public static final String FUNC_REGISTEREDSNAPSHOTS = "registeredSnapshots";

    public static final String FUNC_VERIFIEDPROOFS = "verifiedProofs";

    public static final String FUNC_VERIFIER = "verifier";

    public static final String FUNC_VERIFYANDRECORD = "verifyAndRecord";

    public static final Event ELIGIBILITYPROOFVERIFIED_EVENT = new Event("EligibilityProofVerified", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SNAPSHOTREGISTERED_EVENT = new Event("SnapshotRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    protected ZKEligibilityGate(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<EligibilityProofVerifiedEventResponse> getEligibilityProofVerifiedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ELIGIBILITYPROOFVERIFIED_EVENT, transactionReceipt);
        ArrayList<EligibilityProofVerifiedEventResponse> responses = new ArrayList<EligibilityProofVerifiedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EligibilityProofVerifiedEventResponse typedResponse = new EligibilityProofVerifiedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.proofHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.snapshotHash = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.publicSignalsHash = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.amountMinor = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.maxRiskScoreBps = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeEligibilityProofVerifiedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(ELIGIBILITYPROOFVERIFIED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeEligibilityProofVerifiedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(ELIGIBILITYPROOFVERIFIED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<SnapshotRegisteredEventResponse> getSnapshotRegisteredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SNAPSHOTREGISTERED_EVENT, transactionReceipt);
        ArrayList<SnapshotRegisteredEventResponse> responses = new ArrayList<SnapshotRegisteredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SnapshotRegisteredEventResponse typedResponse = new SnapshotRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.snapshotHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.balanceCommitment = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.riskCommitment = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeSnapshotRegisteredEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(SNAPSHOTREGISTERED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeSnapshotRegisteredEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(SNAPSHOTREGISTERED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public String owner() throws ContractException {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodOwnerRawFunction() throws ContractException {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return function;
    }

    public TransactionReceipt registerSnapshot(BigInteger balanceCommitment,
            BigInteger riskCommitment) {
        final Function function = new Function(
                FUNC_REGISTERSNAPSHOT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(balanceCommitment), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskCommitment)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodRegisterSnapshotRawFunction(BigInteger balanceCommitment,
            BigInteger riskCommitment) throws ContractException {
        final Function function = new Function(FUNC_REGISTERSNAPSHOT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(balanceCommitment), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskCommitment)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return function;
    }

    public String getSignedTransactionForRegisterSnapshot(BigInteger balanceCommitment,
            BigInteger riskCommitment) {
        final Function function = new Function(
                FUNC_REGISTERSNAPSHOT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(balanceCommitment), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskCommitment)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String registerSnapshot(BigInteger balanceCommitment, BigInteger riskCommitment,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_REGISTERSNAPSHOT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(balanceCommitment), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskCommitment)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<BigInteger, BigInteger> getRegisterSnapshotInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_REGISTERSNAPSHOT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue()
                );
    }

    public Tuple1<byte[]> getRegisterSnapshotOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_REGISTERSNAPSHOT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<byte[]>(

                (byte[]) results.get(0).getValue()
                );
    }

    public Boolean registeredSnapshots(byte[] param0) throws ContractException {
        final Function function = new Function(FUNC_REGISTEREDSNAPSHOTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodRegisteredSnapshotsRawFunction(byte[] param0) throws
            ContractException {
        final Function function = new Function(FUNC_REGISTEREDSNAPSHOTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean verifiedProofs(byte[] param0) throws ContractException {
        final Function function = new Function(FUNC_VERIFIEDPROOFS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifiedProofsRawFunction(byte[] param0) throws ContractException {
        final Function function = new Function(FUNC_VERIFIEDPROOFS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public String verifier() throws ContractException {
        final Function function = new Function(FUNC_VERIFIER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodVerifierRawFunction() throws ContractException {
        final Function function = new Function(FUNC_VERIFIER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return function;
    }

    public TransactionReceipt verifyAndRecord(List<BigInteger> pA, List<List<BigInteger>> pB,
            List<BigInteger> pC, List<BigInteger> pubSignals) {
        final Function function = new Function(
                FUNC_VERIFYANDRECORD, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pA, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pB, org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)) {
                    @Override
                    public String getTypeAsString() {
                        return "uint256[2][2]";
                    }
                }, 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pC, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray4<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pubSignals, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodVerifyAndRecordRawFunction(List<BigInteger> pA,
            List<List<BigInteger>> pB, List<BigInteger> pC, List<BigInteger> pubSignals) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYANDRECORD, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pA, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pB, org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)) {
                    @Override
                    public String getTypeAsString() {
                        return "uint256[2][2]";
                    }
                }, 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pC, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray4<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pubSignals, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class))), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return function;
    }

    public String getSignedTransactionForVerifyAndRecord(List<BigInteger> pA,
            List<List<BigInteger>> pB, List<BigInteger> pC, List<BigInteger> pubSignals) {
        final Function function = new Function(
                FUNC_VERIFYANDRECORD, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pA, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pB, org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)) {
                    @Override
                    public String getTypeAsString() {
                        return "uint256[2][2]";
                    }
                }, 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pC, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray4<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pubSignals, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String verifyAndRecord(List<BigInteger> pA, List<List<BigInteger>> pB,
            List<BigInteger> pC, List<BigInteger> pubSignals, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_VERIFYANDRECORD, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pA, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pB, org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)) {
                    @Override
                    public String getTypeAsString() {
                        return "uint256[2][2]";
                    }
                }, 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pC, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray4<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pubSignals, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple4<List<BigInteger>, List<List<BigInteger>>, List<BigInteger>, List<BigInteger>> getVerifyAndRecordInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_VERIFYANDRECORD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<StaticArray2<Uint256>>() {}, new TypeReference<StaticArray2<StaticArray2<Uint256>>>() {}, new TypeReference<StaticArray2<Uint256>>() {}, new TypeReference<StaticArray4<Uint256>>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<List<BigInteger>, List<List<BigInteger>>, List<BigInteger>, List<BigInteger>>(

                convertToNative((List<Uint256>) results.get(0).getValue()), 
                convertToNative((List<StaticArray2<Uint256>>) results.get(1).getValue()), 
                convertToNative((List<Uint256>) results.get(2).getValue()), 
                convertToNative((List<Uint256>) results.get(3).getValue())
                );
    }

    public Tuple1<byte[]> getVerifyAndRecordOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_VERIFYANDRECORD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<byte[]>(

                (byte[]) results.get(0).getValue()
                );
    }

    public static ZKEligibilityGate load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new ZKEligibilityGate(contractAddress, client, credential);
    }

    public static ZKEligibilityGate deploy(Client client, CryptoKeyPair credential,
            String verifierAddress) throws ContractException {
        byte[] encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(verifierAddress)));
        return deploy(ZKEligibilityGate.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), encodedConstructor, null);
    }

    public static class EligibilityProofVerifiedEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] proofHash;

        public byte[] snapshotHash;

        public byte[] publicSignalsHash;

        public BigInteger amountMinor;

        public BigInteger maxRiskScoreBps;
    }

    public static class SnapshotRegisteredEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] snapshotHash;

        public BigInteger balanceCommitment;

        public BigInteger riskCommitment;
    }
}

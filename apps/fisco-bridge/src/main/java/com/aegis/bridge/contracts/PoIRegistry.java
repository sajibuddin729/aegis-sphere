package com.aegis.bridge.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple10;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple7;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class PoIRegistry extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50600080546001600160a01b0319163317905561073e806100326000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80631fa389461461006757806338a699a41461009f5780638da5cb5b146100c5578063b5f70d86146100f0578063c8836d3c1461020b578063eec95f6f14610220575b600080fd5b61008a61007536600461062e565b60016020526000908152604090205460ff1681565b60405190151581526020015b60405180910390f35b61008a6100ad366004610650565b60009081526002602052604090206009015460ff1690565b6000546100d8906001600160a01b031681565b6040516001600160a01b039091168152602001610096565b6101b06100fe366004610650565b60009081526002602081815260409283902083516101408101855281548082526001830154938201849052938201546001600160a01b039081169582018690526003830154811660608301819052600484015490911660808301819052600584015460a08401819052600685015460c08501819052600786015460e086018190526008870154610100870181905260099097015460ff1615156101209096018690529799969897939692959194909392565b604080519a8b5260208b01999099526001600160a01b03978816988a0198909852948616606089015294909216608087015260a086015260c085015260e0840191909152610100830152151561012082015261014001610096565b61021e610219366004610669565b610233565b005b61021e61022e3660046106a5565b610330565b6000546001600160a01b031633146102855760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064015b60405180910390fd5b6001600160a01b0382166102d15760405162461bcd60e51b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b604482015260640161027c565b6001600160a01b038216600081815260016020908152604091829020805460ff191685151590811790915591519182527f0d9344ef43897b385229621e02645d25ef9cdcc662414aff5a197d47022734ee910160405180910390a25050565b3360009081526001602052604090205460ff1661038f5760405162461bcd60e51b815260206004820152601c60248201527f41454749533a2057414c4c45545f4e4f545f415554484f52495a454400000000604482015260640161027c565b866103d15760405162461bcd60e51b815260206004820152601260248201527141454749533a20494e56414c49445f504f4960701b604482015260640161027c565b60008781526002602052604090206009015460ff16156104335760405162461bcd60e51b815260206004820152601960248201527f41454749533a20504f495f414c52454144595f45584953545300000000000000604482015260640161027c565b604051806101400160405280878152602001868152602001336001600160a01b03168152602001856001600160a01b03168152602001846001600160a01b031681526020018381526020018281526020014281526020014381526020016001151581525060026000898152602001908152602001600020600082015181600001556020820151816001015560408201518160020160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555060608201518160030160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555060808201518160040160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555060a0820151816005015560c0820151816006015560e0820151816007015561010082015181600801556101208201518160090160006101000a81548160ff021916908315150217905550905050336001600160a01b031686887f30a1925002932021c609d0cdf2c1d765ba5e92338a1571c8b8008c7b084567d0878787878c6040516106019594939291906001600160a01b03958616815293909416602084015260408301919091526060820152608081019190915260a00190565b60405180910390a450505050505050565b80356001600160a01b038116811461062957600080fd5b919050565b60006020828403121561064057600080fd5b61064982610612565b9392505050565b60006020828403121561066257600080fd5b5035919050565b6000806040838503121561067c57600080fd5b61068583610612565b91506020830135801515811461069a57600080fd5b809150509250929050565b600080600080600080600060e0888a0312156106c057600080fd5b8735965060208801359550604088013594506106de60608901610612565b93506106ec60808901610612565b925060a0880135915060c088013590509295989194975092955056fea264697066735822122075ccca9f55e84e97c39017e7c4b435ce333a3a4bf8a2988ef78ed6bdcde8be0064736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50600080546001600160a01b03191633179055610743806100326000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80633b663083146100675780635089e2c8146100a25780638b8d558d146100cd57806393a7c48a146100e2578063c3ab82f614610105578063f7a0be2714610118575b600080fd5b61008d610075366004610617565b60009081526002602052604090206009015460ff1690565b60405190151581526020015b60405180910390f35b6000546100b5906001600160a01b031681565b6040516001600160a01b039091168152602001610099565b6100e06100db36600461064c565b610233565b005b61008d6100f03660046106af565b60016020526000908152604090205460ff1681565b6100e06101133660046106d1565b61051d565b6101d8610126366004610617565b60009081526002602081815260409283902083516101408101855281548082526001830154938201849052938201546001600160a01b039081169582018690526003830154811660608301819052600484015490911660808301819052600584015460a08401819052600685015460c08501819052600786015460e086018190526008870154610100870181905260099097015460ff1615156101209096018690529799969897939692959194909392565b604080519a8b5260208b01999099526001600160a01b03978816988a0198909852948616606089015294909216608087015260a086015260c085015260e0840191909152610100830152151561012082015261014001610099565b3360009081526001602052604090205460ff1661029857604051636381e58960e11b815260206004820152601c60248201527f41454749533a2057414c4c45545f4e4f545f415554484f52495a45440000000060448201526064015b60405180910390fd5b866102db57604051636381e58960e11b815260206004820152601260248201527141454749533a20494e56414c49445f504f4960701b604482015260640161028f565b60008781526002602052604090206009015460ff161561033e57604051636381e58960e11b815260206004820152601960248201527f41454749533a20504f495f414c52454144595f45584953545300000000000000604482015260640161028f565b604051806101400160405280878152602001868152602001336001600160a01b03168152602001856001600160a01b03168152602001846001600160a01b031681526020018381526020018281526020014281526020014381526020016001151581525060026000898152602001908152602001600020600082015181600001556020820151816001015560408201518160020160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555060608201518160030160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555060808201518160040160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555060a0820151816005015560c0820151816006015560e0820151816007015561010082015181600801556101208201518160090160006101000a81548160ff021916908315150217905550905050336001600160a01b031686887fc098c7ef5ebbea46f31bde596f38fa0ce2c1f5b8e081f77edee468509dfd5d12878787878c60405161050c9594939291906001600160a01b03958616815293909416602084015260408301919091526060820152608081019190915260a00190565b60405180910390a450505050505050565b6000546001600160a01b0316331461056b57604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b604482015260640161028f565b6001600160a01b0382166105b857604051636381e58960e11b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b604482015260640161028f565b6001600160a01b038216600081815260016020908152604091829020805460ff191685151590811790915591519182527fc65aec4906e5484543443076ae3756a73365c93e331431edbc3be186b7806e2e910160405180910390a25050565b60006020828403121561062957600080fd5b5035919050565b80356001600160a01b038116811461064757600080fd5b919050565b600080600080600080600060e0888a03121561066757600080fd5b87359650602088013595506040880135945061068560608901610630565b935061069360808901610630565b925060a0880135915060c0880135905092959891949750929550565b6000602082840312156106c157600080fd5b6106ca82610630565b9392505050565b600080604083850312156106e457600080fd5b6106ed83610630565b91506020830135801515811461070257600080fd5b80915050925092905056fea2646970667358221220a8bc15096ca8cede29a7d1459b54cc4356dd2542957eff2c3384639c7fb8b40764736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"wallet\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"AuthorizedWalletUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"poiHash\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"intentId\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"wallet\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"recipient\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"riskScoreBps\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"bytes32\",\"name\":\"policyHash\",\"type\":\"bytes32\"}],\"name\":\"PoIRecorded\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":3,\"slot\":1,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"authorizedWallets\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[530811206,2477245578],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"poiHash\",\"type\":\"bytes32\"}],\"name\":\"exists\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[950442404,996552835],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"poiHash\",\"type\":\"bytes32\"}],\"name\":\"getPoI\",\"outputs\":[{\"internalType\":\"bytes32\",\"name\":\"intentId\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"policyHash\",\"type\":\"bytes32\"},{\"internalType\":\"address\",\"name\":\"wallet\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"recipient\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"riskScoreBps\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"blockNumber\",\"type\":\"uint256\"},{\"internalType\":\"bool\",\"name\":\"recordExists\",\"type\":\"bool\"}],\"selector\":[3052866950,4154506791],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":2,\"slot\":1,\"value\":[0]},{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"poiHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"intentId\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"policyHash\",\"type\":\"bytes32\"},{\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"recipient\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"riskScoreBps\",\"type\":\"uint256\"}],\"name\":\"recordPoI\",\"outputs\":[],\"selector\":[4006174575,2341295501],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"wallet\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"setAuthorizedWallet\",\"outputs\":[],\"selector\":[3364056380,3282797302],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_AUTHORIZEDWALLETS = "authorizedWallets";

    public static final String FUNC_EXISTS = "exists";

    public static final String FUNC_GETPOI = "getPoI";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RECORDPOI = "recordPoI";

    public static final String FUNC_SETAUTHORIZEDWALLET = "setAuthorizedWallet";

    public static final Event AUTHORIZEDWALLETUPDATED_EVENT = new Event("AuthorizedWalletUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event POIRECORDED_EVENT = new Event("PoIRecorded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>() {}));
    ;

    protected PoIRegistry(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<AuthorizedWalletUpdatedEventResponse> getAuthorizedWalletUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(AUTHORIZEDWALLETUPDATED_EVENT, transactionReceipt);
        ArrayList<AuthorizedWalletUpdatedEventResponse> responses = new ArrayList<AuthorizedWalletUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AuthorizedWalletUpdatedEventResponse typedResponse = new AuthorizedWalletUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.wallet = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.authorized = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeAuthorizedWalletUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(AUTHORIZEDWALLETUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeAuthorizedWalletUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(AUTHORIZEDWALLETUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<PoIRecordedEventResponse> getPoIRecordedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(POIRECORDED_EVENT, transactionReceipt);
        ArrayList<PoIRecordedEventResponse> responses = new ArrayList<PoIRecordedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PoIRecordedEventResponse typedResponse = new PoIRecordedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.poiHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.wallet = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.token = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.recipient = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.riskScoreBps = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.policyHash = (byte[]) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribePoIRecordedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(POIRECORDED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribePoIRecordedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(POIRECORDED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public Boolean authorizedWallets(String param0) throws ContractException {
        final Function function = new Function(FUNC_AUTHORIZEDWALLETS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodAuthorizedWalletsRawFunction(String param0) throws ContractException {
        final Function function = new Function(FUNC_AUTHORIZEDWALLETS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean exists(byte[] poiHash) throws ContractException {
        final Function function = new Function(FUNC_EXISTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodExistsRawFunction(byte[] poiHash) throws ContractException {
        final Function function = new Function(FUNC_EXISTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Tuple10<byte[], byte[], String, String, String, BigInteger, BigInteger, BigInteger, BigInteger, Boolean> getPoI(
            byte[] poiHash) throws ContractException {
        final Function function = new Function(FUNC_GETPOI, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple10<byte[], byte[], String, String, String, BigInteger, BigInteger, BigInteger, BigInteger, Boolean>(
                (byte[]) results.get(0).getValue(), 
                (byte[]) results.get(1).getValue(), 
                (String) results.get(2).getValue(), 
                (String) results.get(3).getValue(), 
                (String) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue(), 
                (BigInteger) results.get(7).getValue(), 
                (BigInteger) results.get(8).getValue(), 
                (Boolean) results.get(9).getValue());
    }

    public Function getMethodGetPoIRawFunction(byte[] poiHash) throws ContractException {
        final Function function = new Function(FUNC_GETPOI, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        return function;
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

    public TransactionReceipt recordPoI(byte[] poiHash, byte[] intentId, byte[] policyHash,
            String token, String recipient, BigInteger amount, BigInteger riskScoreBps) {
        final Function function = new Function(
                FUNC_RECORDPOI, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(intentId), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return executeTransaction(function);
    }

    public Function getMethodRecordPoIRawFunction(byte[] poiHash, byte[] intentId,
            byte[] policyHash, String token, String recipient, BigInteger amount,
            BigInteger riskScoreBps) throws ContractException {
        final Function function = new Function(FUNC_RECORDPOI, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(intentId), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForRecordPoI(byte[] poiHash, byte[] intentId,
            byte[] policyHash, String token, String recipient, BigInteger amount,
            BigInteger riskScoreBps) {
        final Function function = new Function(
                FUNC_RECORDPOI, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(intentId), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return createSignedTransaction(function);
    }

    public String recordPoI(byte[] poiHash, byte[] intentId, byte[] policyHash, String token,
            String recipient, BigInteger amount, BigInteger riskScoreBps,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_RECORDPOI, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(intentId), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple7<byte[], byte[], byte[], String, String, BigInteger, BigInteger> getRecordPoIInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_RECORDPOI, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple7<byte[], byte[], byte[], String, String, BigInteger, BigInteger>(

                (byte[]) results.get(0).getValue(), 
                (byte[]) results.get(1).getValue(), 
                (byte[]) results.get(2).getValue(), 
                (String) results.get(3).getValue(), 
                (String) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue()
                );
    }

    public TransactionReceipt setAuthorizedWallet(String wallet, Boolean authorized) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDWALLET, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(wallet), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetAuthorizedWalletRawFunction(String wallet, Boolean authorized)
            throws ContractException {
        final Function function = new Function(FUNC_SETAUTHORIZEDWALLET, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(wallet), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetAuthorizedWallet(String wallet, Boolean authorized) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDWALLET, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(wallet), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setAuthorizedWallet(String wallet, Boolean authorized,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDWALLET, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(wallet), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, Boolean> getSetAuthorizedWalletInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETAUTHORIZEDWALLET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Boolean>(

                (String) results.get(0).getValue(), 
                (Boolean) results.get(1).getValue()
                );
    }

    public static PoIRegistry load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new PoIRegistry(contractAddress, client, credential);
    }

    public static PoIRegistry deploy(Client client, CryptoKeyPair credential) throws
            ContractException {
        return deploy(PoIRegistry.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }

    public static class AuthorizedWalletUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public String wallet;

        public Boolean authorized;
    }

    public static class PoIRecordedEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] poiHash;

        public byte[] intentId;

        public String wallet;

        public String token;

        public String recipient;

        public BigInteger amount;

        public BigInteger riskScoreBps;

        public byte[] policyHash;
    }
}

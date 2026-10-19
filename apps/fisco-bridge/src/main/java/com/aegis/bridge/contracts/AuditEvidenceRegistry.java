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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple8;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class AuditEvidenceRegistry extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50600080546001600160a01b0319163317905561064d806100326000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c806338a699a4146100675780634a7221a0146100a2578063526241a814610185578063542a086c146101a85780638da5cb5b146101bd578063de1d5846146101e8575b600080fd5b61008d610075366004610549565b60009081526002602052604090206007015460ff1690565b60405190151581526020015b60405180910390f35b61013f6100b0366004610549565b600090815260026020818152604092839020835161010081018552815480825260018301549382018490529382015494810185905260038201546060820181905260048301546001600160a01b031660808301819052600584015460a08401819052600685015460c0850181905260079095015460ff16151560e0909401849052959794969591949093919290565b6040805198895260208901979097529587019490945260608601929092526001600160a01b0316608085015260a084015260c0830152151560e082015261010001610099565b61008d61019336600461057e565b60016020526000908152604090205460ff1681565b6101bb6101b63660046105a0565b6101fb565b005b6000546101d0906001600160a01b031681565b6040516001600160a01b039091168152602001610099565b6101bb6101f63660046105dc565b6102f7565b6000546001600160a01b0316331461024d5760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064015b60405180910390fd5b6001600160a01b0382166102995760405162461bcd60e51b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b6044820152606401610244565b6001600160a01b038216600081815260016020908152604091829020805460ff191685151590811790915591519182527e8ebc621f24e0e9d1fd4ee2f7df9bb80c8d61ee30a9167a4fee6cefcf8c021e910160405180910390a25050565b3360009081526001602052604090205460ff166103565760405162461bcd60e51b815260206004820152601c60248201527f41454749533a205752495445525f4e4f545f415554484f52495a4544000000006044820152606401610244565b846103a35760405162461bcd60e51b815260206004820152601c60248201527f41454749533a20494e56414c49445f45564944454e43455f48415348000000006044820152606401610244565b60008581526002602052604090206007015460ff16156104055760405162461bcd60e51b815260206004820152601e60248201527f41454749533a2045564944454e43455f414c52454144595f45584953545300006044820152606401610244565b604051806101000160405280858152602001848152602001838152602001828152602001336001600160a01b0316815260200142815260200143815260200160011515815250600260008781526020019081526020016000206000820151816000015560208201518160010155604082015181600201556060820151816003015560808201518160040160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555060a0820151816005015560c0820151816006015560e08201518160070160006101000a81548160ff0219169083151502179055509050508181867f08ee3882fa01715382d72227e0ad009747fbc6cbaa896b634e49fc13b33c1f9887873360405161053a9392919092835260208301919091526001600160a01b0316604082015260600190565b60405180910390a45050505050565b60006020828403121561055b57600080fd5b5035919050565b80356001600160a01b038116811461057957600080fd5b919050565b60006020828403121561059057600080fd5b61059982610562565b9392505050565b600080604083850312156105b357600080fd5b6105bc83610562565b9150602083013580151581146105d157600080fd5b809150509250929050565b600080600080600060a086880312156105f457600080fd5b50508335956020850135955060408501359460608101359450608001359250905056fea26469706673582212202f2b16f245f1ea2420af4e8863312e8f700651091b6917a82bb52faddcc6f38064736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50600080546001600160a01b03191633179055610653806100326000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c806337612786146100675780633b6630831461007c5780635089e2c8146100b7578063880f1cda146100e2578063c898ba8614610105578063f6f38f32146101e8575b600080fd5b61007a61007536600461056b565b6101fb565b005b6100a261008a3660046105a7565b60009081526002602052604090206007015460ff1690565b60405190151581526020015b60405180910390f35b6000546100ca906001600160a01b031681565b6040516001600160a01b0390911681526020016100ae565b6100a26100f03660046105c0565b60016020526000908152604090205460ff1681565b6101a26101133660046105a7565b600090815260026020818152604092839020835161010081018552815480825260018301549382018490529382015494810185905260038201546060820181905260048301546001600160a01b031660808301819052600584015460a08401819052600685015460c0850181905260079095015460ff16151560e0909401849052959794969591949093919290565b6040805198895260208901979097529587019490945260608601929092526001600160a01b0316608085015260a084015260c0830152151560e0820152610100016100ae565b61007a6101f63660046105e2565b6102fa565b6000546001600160a01b0316331461024e57604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064015b60405180910390fd5b6001600160a01b03821661029b57604051636381e58960e11b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b6044820152606401610245565b6001600160a01b038216600081815260016020908152604091829020805460ff191685151590811790915591519182527fa133c6e1e81fb77cee366845ee66f5f0aadaee4879066818ae74e3757453dc1e910160405180910390a25050565b3360009081526001602052604090205460ff1661035a57604051636381e58960e11b815260206004820152601c60248201527f41454749533a205752495445525f4e4f545f415554484f52495a4544000000006044820152606401610245565b846103a857604051636381e58960e11b815260206004820152601c60248201527f41454749533a20494e56414c49445f45564944454e43455f48415348000000006044820152606401610245565b60008581526002602052604090206007015460ff161561040b57604051636381e58960e11b815260206004820152601e60248201527f41454749533a2045564944454e43455f414c52454144595f45584953545300006044820152606401610245565b604051806101000160405280858152602001848152602001838152602001828152602001336001600160a01b0316815260200142815260200143815260200160011515815250600260008781526020019081526020016000206000820151816000015560208201518160010155604082015181600201556060820151816003015560808201518160040160006101000a8154816001600160a01b0302191690836001600160a01b0316021790555060a0820151816005015560c0820151816006015560e08201518160070160006101000a81548160ff0219169083151502179055509050508181867f33bc1aea7a97bdbb99aea5d874e026e668d91aea684883671c49b28e90f742b88787336040516105409392919092835260208301919091526001600160a01b0316604082015260600190565b60405180910390a45050505050565b80356001600160a01b038116811461056657600080fd5b919050565b6000806040838503121561057e57600080fd5b6105878361054f565b91506020830135801515811461059c57600080fd5b809150509250929050565b6000602082840312156105b957600080fd5b5035919050565b6000602082840312156105d257600080fd5b6105db8261054f565b9392505050565b600080600080600060a086880312156105fa57600080fd5b50508335956020850135955060408501359460608101359450608001359250905056fea264697066735822122053e1501b417c4d74ff762f4c71f3c3cf8988d0e0b8df63ae37ddc7e57778aa6564736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"writer\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"AuthorizedWriterUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"evidenceHash\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"relatedPoIHash\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"decisionHash\",\"type\":\"bytes32\"},{\"indexed\":false,\"internalType\":\"bytes32\",\"name\":\"policyVersionHash\",\"type\":\"bytes32\"},{\"indexed\":false,\"internalType\":\"bytes32\",\"name\":\"retrievalSetHash\",\"type\":\"bytes32\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"recorder\",\"type\":\"address\"}],\"name\":\"EvidenceAnchored\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":3,\"slot\":1,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"authorizedWriters\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1382171048,2282691802],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"evidenceHash\",\"type\":\"bytes32\"}],\"name\":\"exists\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[950442404,996552835],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"evidenceHash\",\"type\":\"bytes32\"}],\"name\":\"getEvidence\",\"outputs\":[{\"internalType\":\"bytes32\",\"name\":\"policyVersionHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"retrievalSetHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"decisionHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"relatedPoIHash\",\"type\":\"bytes32\"},{\"internalType\":\"address\",\"name\":\"recorder\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"blockNumber\",\"type\":\"uint256\"},{\"internalType\":\"bool\",\"name\":\"recordExists\",\"type\":\"bool\"}],\"selector\":[1248993696,3365452422],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":2,\"slot\":1,\"value\":[0]},{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"evidenceHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"policyVersionHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"retrievalSetHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"decisionHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"relatedPoIHash\",\"type\":\"bytes32\"}],\"name\":\"recordEvidence\",\"outputs\":[],\"selector\":[3726465094,4143157042],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"writer\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"setAuthorizedWriter\",\"outputs\":[],\"selector\":[1412040812,929113990],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_AUTHORIZEDWRITERS = "authorizedWriters";

    public static final String FUNC_EXISTS = "exists";

    public static final String FUNC_GETEVIDENCE = "getEvidence";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RECORDEVIDENCE = "recordEvidence";

    public static final String FUNC_SETAUTHORIZEDWRITER = "setAuthorizedWriter";

    public static final Event AUTHORIZEDWRITERUPDATED_EVENT = new Event("AuthorizedWriterUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event EVIDENCEANCHORED_EVENT = new Event("EvidenceAnchored", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}));
    ;

    protected AuditEvidenceRegistry(String contractAddress, Client client,
            CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<AuthorizedWriterUpdatedEventResponse> getAuthorizedWriterUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(AUTHORIZEDWRITERUPDATED_EVENT, transactionReceipt);
        ArrayList<AuthorizedWriterUpdatedEventResponse> responses = new ArrayList<AuthorizedWriterUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AuthorizedWriterUpdatedEventResponse typedResponse = new AuthorizedWriterUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.writer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.authorized = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeAuthorizedWriterUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(AUTHORIZEDWRITERUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeAuthorizedWriterUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(AUTHORIZEDWRITERUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<EvidenceAnchoredEventResponse> getEvidenceAnchoredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(EVIDENCEANCHORED_EVENT, transactionReceipt);
        ArrayList<EvidenceAnchoredEventResponse> responses = new ArrayList<EvidenceAnchoredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EvidenceAnchoredEventResponse typedResponse = new EvidenceAnchoredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.evidenceHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.relatedPoIHash = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.decisionHash = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.policyVersionHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.retrievalSetHash = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.recorder = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeEvidenceAnchoredEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(EVIDENCEANCHORED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeEvidenceAnchoredEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(EVIDENCEANCHORED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public Boolean authorizedWriters(String param0) throws ContractException {
        final Function function = new Function(FUNC_AUTHORIZEDWRITERS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodAuthorizedWritersRawFunction(String param0) throws ContractException {
        final Function function = new Function(FUNC_AUTHORIZEDWRITERS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean exists(byte[] evidenceHash) throws ContractException {
        final Function function = new Function(FUNC_EXISTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(evidenceHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodExistsRawFunction(byte[] evidenceHash) throws ContractException {
        final Function function = new Function(FUNC_EXISTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(evidenceHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Tuple8<byte[], byte[], byte[], byte[], String, BigInteger, BigInteger, Boolean> getEvidence(
            byte[] evidenceHash) throws ContractException {
        final Function function = new Function(FUNC_GETEVIDENCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(evidenceHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple8<byte[], byte[], byte[], byte[], String, BigInteger, BigInteger, Boolean>(
                (byte[]) results.get(0).getValue(), 
                (byte[]) results.get(1).getValue(), 
                (byte[]) results.get(2).getValue(), 
                (byte[]) results.get(3).getValue(), 
                (String) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue(), 
                (Boolean) results.get(7).getValue());
    }

    public Function getMethodGetEvidenceRawFunction(byte[] evidenceHash) throws ContractException {
        final Function function = new Function(FUNC_GETEVIDENCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(evidenceHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
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

    public TransactionReceipt recordEvidence(byte[] evidenceHash, byte[] policyVersionHash,
            byte[] retrievalSetHash, byte[] decisionHash, byte[] relatedPoIHash) {
        final Function function = new Function(
                FUNC_RECORDEVIDENCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(evidenceHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyVersionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(retrievalSetHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(decisionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(relatedPoIHash)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return executeTransaction(function);
    }

    public Function getMethodRecordEvidenceRawFunction(byte[] evidenceHash,
            byte[] policyVersionHash, byte[] retrievalSetHash, byte[] decisionHash,
            byte[] relatedPoIHash) throws ContractException {
        final Function function = new Function(FUNC_RECORDEVIDENCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(evidenceHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyVersionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(retrievalSetHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(decisionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(relatedPoIHash)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForRecordEvidence(byte[] evidenceHash,
            byte[] policyVersionHash, byte[] retrievalSetHash, byte[] decisionHash,
            byte[] relatedPoIHash) {
        final Function function = new Function(
                FUNC_RECORDEVIDENCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(evidenceHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyVersionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(retrievalSetHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(decisionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(relatedPoIHash)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return createSignedTransaction(function);
    }

    public String recordEvidence(byte[] evidenceHash, byte[] policyVersionHash,
            byte[] retrievalSetHash, byte[] decisionHash, byte[] relatedPoIHash,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_RECORDEVIDENCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(evidenceHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyVersionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(retrievalSetHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(decisionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(relatedPoIHash)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple5<byte[], byte[], byte[], byte[], byte[]> getRecordEvidenceInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_RECORDEVIDENCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple5<byte[], byte[], byte[], byte[], byte[]>(

                (byte[]) results.get(0).getValue(), 
                (byte[]) results.get(1).getValue(), 
                (byte[]) results.get(2).getValue(), 
                (byte[]) results.get(3).getValue(), 
                (byte[]) results.get(4).getValue()
                );
    }

    public TransactionReceipt setAuthorizedWriter(String writer, Boolean authorized) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDWRITER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(writer), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetAuthorizedWriterRawFunction(String writer, Boolean authorized)
            throws ContractException {
        final Function function = new Function(FUNC_SETAUTHORIZEDWRITER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(writer), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetAuthorizedWriter(String writer, Boolean authorized) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDWRITER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(writer), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setAuthorizedWriter(String writer, Boolean authorized,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDWRITER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(writer), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, Boolean> getSetAuthorizedWriterInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETAUTHORIZEDWRITER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Boolean>(

                (String) results.get(0).getValue(), 
                (Boolean) results.get(1).getValue()
                );
    }

    public static AuditEvidenceRegistry load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new AuditEvidenceRegistry(contractAddress, client, credential);
    }

    public static AuditEvidenceRegistry deploy(Client client, CryptoKeyPair credential) throws
            ContractException {
        return deploy(AuditEvidenceRegistry.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }

    public static class AuthorizedWriterUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public String writer;

        public Boolean authorized;
    }

    public static class EvidenceAnchoredEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] evidenceHash;

        public byte[] relatedPoIHash;

        public byte[] decisionHash;

        public byte[] policyVersionHash;

        public byte[] retrievalSetHash;

        public String recorder;
    }
}

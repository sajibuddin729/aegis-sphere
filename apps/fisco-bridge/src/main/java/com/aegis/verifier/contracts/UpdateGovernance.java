package com.aegis.verifier.contracts;

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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
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
public class UpdateGovernance extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50600080546001600160a01b03191633179055610812806100326000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c806338a699a4146100675780635423ac8e146100a25780636c53ea25146100b75780638da5cb5b146100ca5780639a891716146100f5578063d8c5789214610118575b600080fd5b61008d61007536600461069e565b60009081526002602052604090206007015460ff1690565b60405190151581526020015b60405180910390f35b6100b56100b03660046106d3565b610173565b005b6100b56100c536600461070f565b610270565b6000546100dd906001600160a01b031681565b6040516001600160a01b039091168152602001610099565b61008d610103366004610762565b60016020526000908152604090205460ff1681565b61012b61012636600461069e565b61055a565b604080519889526020890197909752958701949094526001600160a01b03909216606086015260ff16608085015260a084015260c0830152151560e082015261010001610099565b6000546001600160a01b031633146101c55760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064015b60405180910390fd5b6001600160a01b0382166102115760405162461bcd60e51b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b60448201526064016101bc565b6001600160a01b038216600081815260016020908152604091829020805460ff191685151590811790915591519182527f48e57e73f8641a79719652438e03e81257abbeb5960a971d09c2a128d264ac4f910160405180910390a25050565b3360009081526001602052604090205460ff166102cf5760405162461bcd60e51b815260206004820152601e60248201527f41454749533a2056455249464945525f4e4f545f415554484f52495a4544000060448201526064016101bc565b8461031c5760405162461bcd60e51b815260206004820152601d60248201527f41454749533a20494e56414c49445f43414e4449444154455f4841534800000060448201526064016101bc565b60008581526002602052604090206007015460ff161561037e5760405162461bcd60e51b815260206004820152601d60248201527f41454749533a205550444154455f414c52454144595f4445434944454400000060448201526064016101bc565b8060ff166001148061039357508060ff166002145b6103df5760405162461bcd60e51b815260206004820152601760248201527f41454749533a20494e56414c49445f4445434953494f4e00000000000000000060448201526064016101bc565b60008160ff1660028111156103f6576103f6610784565b9050604051806101200160405280878152602001868152602001858152602001848152602001336001600160a01b0316815260200182600281111561043d5761043d610784565b815242602080830191909152436040808401919091526001606093840181905260008b8152600280855290839020865181559386015191840191909155908401518282015591830151600382015560808301516004820180546001600160a01b039092166001600160a01b031983168117825560a0860151939491926001600160a81b0319161790600160a01b9084908111156104dc576104dc610784565b021790555060c0820151600582015560e08201516006820155610100909101516007909101805460ff19169115159190911790556040518590859088907f16aec971c8f9668b76d071f57fbb53fefd2bddf303f3a6c0b7eb30b25f3036099061054a9088903390889061079a565b60405180910390a4505050505050565b6000806000806000806000806000600260008b815260200190815260200160002060405180610120016040529081600082015481526020016001820154815260200160028201548152602001600382015481526020016004820160009054906101000a90046001600160a01b03166001600160a01b03166001600160a01b031681526020016004820160149054906101000a900460ff16600281111561060257610602610784565b600281111561061357610613610784565b815260200160058201548152602001600682015481526020016007820160009054906101000a900460ff161515151581525050905080602001518160400151826060015183608001518460a00151600281111561067257610672610784565b8560c001518660e001518761010001519850985098509850985098509850985050919395975091939597565b6000602082840312156106b057600080fd5b5035919050565b80356001600160a01b03811681146106ce57600080fd5b919050565b600080604083850312156106e657600080fd5b6106ef836106b7565b91506020830135801515811461070457600080fd5b809150509250929050565b600080600080600060a0868803121561072757600080fd5b85359450602086013593506040860135925060608601359150608086013560ff8116811461075457600080fd5b809150509295509295909350565b60006020828403121561077457600080fd5b61077d826106b7565b9392505050565b634e487b7160e01b600052602160045260246000fd5b8381526001600160a01b038316602082015260608101600383106107ce57634e487b7160e01b600052602160045260246000fd5b82604083015294935050505056fea2646970667358221220c6cac4ddaa900a0994ae3dc065996c285bbb6cc01d2ed3573a7d9bc3e02c01c264736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50600080546001600160a01b03191633179055610818806100326000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80633b663083146100675780635089e2c8146100a257806350eca263146100cd5780635d8a851a146100e2578063ce9c6e9b14610105578063ed3bcba914610160575b600080fd5b61008d6100753660046106a4565b60009081526002602052604090206007015460ff1690565b60405190151581526020015b60405180910390f35b6000546100b5906001600160a01b031681565b6040516001600160a01b039091168152602001610099565b6100e06100db3660046106bd565b610173565b005b61008d6100f036600461072c565b60016020526000908152604090205460ff1681565b6101186101133660046106a4565b610466565b604080519889526020890197909752958701949094526001600160a01b03909216606086015260ff16608085015260a084015260c0830152151560e082015261010001610099565b6100e061016e36600461074e565b6105aa565b3360009081526001602052604090205460ff166101d857604051636381e58960e11b815260206004820152601e60248201527f41454749533a2056455249464945525f4e4f545f415554484f52495a4544000060448201526064015b60405180910390fd5b8461022657604051636381e58960e11b815260206004820152601d60248201527f41454749533a20494e56414c49445f43414e4449444154455f4841534800000060448201526064016101cf565b60008581526002602052604090206007015460ff161561028957604051636381e58960e11b815260206004820152601d60248201527f41454749533a205550444154455f414c52454144595f4445434944454400000060448201526064016101cf565b8060ff166001148061029e57508060ff166002145b6102eb57604051636381e58960e11b815260206004820152601760248201527f41454749533a20494e56414c49445f4445434953494f4e00000000000000000060448201526064016101cf565b60008160ff1660028111156103025761030261078a565b9050604051806101200160405280878152602001868152602001858152602001848152602001336001600160a01b031681526020018260028111156103495761034961078a565b815242602080830191909152436040808401919091526001606093840181905260008b8152600280855290839020865181559386015191840191909155908401518282015591830151600382015560808301516004820180546001600160a01b039092166001600160a01b031983168117825560a0860151939491926001600160a81b0319161790600160a01b9084908111156103e8576103e861078a565b021790555060c0820151600582015560e08201516006820155610100909101516007909101805460ff19169115159190911790556040518590859088907f8f164811258ce53752ff2be85db34ff272f4bb05e9a93b807dea1b381a4ade4990610456908890339088906107a0565b60405180910390a4505050505050565b6000806000806000806000806000600260008b815260200190815260200160002060405180610120016040529081600082015481526020016001820154815260200160028201548152602001600382015481526020016004820160009054906101000a90046001600160a01b03166001600160a01b03166001600160a01b031681526020016004820160149054906101000a900460ff16600281111561050e5761050e61078a565b600281111561051f5761051f61078a565b815260200160058201548152602001600682015481526020016007820160009054906101000a900460ff161515151581525050905080602001518160400151826060015183608001518460a00151600281111561057e5761057e61078a565b8560c001518660e001518761010001519850985098509850985098509850985050919395975091939597565b6000546001600160a01b031633146105f857604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064016101cf565b6001600160a01b03821661064557604051636381e58960e11b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b60448201526064016101cf565b6001600160a01b038216600081815260016020908152604091829020805460ff191685151590811790915591519182527f4e4afd14e725c0bc638800e7e05367ab9eb19d59f5c893d46ab8189dcf890967910160405180910390a25050565b6000602082840312156106b657600080fd5b5035919050565b600080600080600060a086880312156106d557600080fd5b85359450602086013593506040860135925060608601359150608086013560ff8116811461070257600080fd5b809150509295509295909350565b80356001600160a01b038116811461072757600080fd5b919050565b60006020828403121561073e57600080fd5b61074782610710565b9392505050565b6000806040838503121561076157600080fd5b61076a83610710565b91506020830135801515811461077f57600080fd5b809150509250929050565b63b95aa35560e01b600052602160045260246000fd5b8381526001600160a01b038316602082015260608101600383106107d45763b95aa35560e01b600052602160045260246000fd5b82604083015294935050505056fea2646970667358221220f9b0f6eef9eb69edd175790a7488539ef7c3bc7c4d41a8124bd73a06f37f5b5e64736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"candidateHash\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"attestationHash\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"reportHash\",\"type\":\"bytes32\"},{\"indexed\":false,\"internalType\":\"bytes32\",\"name\":\"verifierVersionHash\",\"type\":\"bytes32\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"verifier\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"enum UpdateGovernance.Decision\",\"name\":\"decision\",\"type\":\"uint8\"}],\"name\":\"UpdateDecisionRecorded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"verifier\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"VerifierAuthorizationUpdated\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":3,\"slot\":1,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"authorizedVerifiers\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[2592675606,1569359130],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"candidateHash\",\"type\":\"bytes32\"}],\"name\":\"exists\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[950442404,996552835],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"candidateHash\",\"type\":\"bytes32\"}],\"name\":\"getDecision\",\"outputs\":[{\"internalType\":\"bytes32\",\"name\":\"reportHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"attestationHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"verifierVersionHash\",\"type\":\"bytes32\"},{\"internalType\":\"address\",\"name\":\"verifier\",\"type\":\"address\"},{\"internalType\":\"uint8\",\"name\":\"decision\",\"type\":\"uint8\"},{\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"blockNumber\",\"type\":\"uint256\"},{\"internalType\":\"bool\",\"name\":\"recordExists\",\"type\":\"bool\"}],\"selector\":[3636820114,3466358427],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":2,\"slot\":1,\"value\":[0]},{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"candidateHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"reportHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"attestationHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"verifierVersionHash\",\"type\":\"bytes32\"},{\"internalType\":\"uint8\",\"name\":\"decisionValue\",\"type\":\"uint8\"}],\"name\":\"recordDecision\",\"outputs\":[],\"selector\":[1817438757,1357685347],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"verifier\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"setAuthorizedVerifier\",\"outputs\":[],\"selector\":[1411624078,3980118953],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_AUTHORIZEDVERIFIERS = "authorizedVerifiers";

    public static final String FUNC_EXISTS = "exists";

    public static final String FUNC_GETDECISION = "getDecision";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RECORDDECISION = "recordDecision";

    public static final String FUNC_SETAUTHORIZEDVERIFIER = "setAuthorizedVerifier";

    public static final Event UPDATEDECISIONRECORDED_EVENT = new Event("UpdateDecisionRecorded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint8>() {}));
    ;

    public static final Event VERIFIERAUTHORIZATIONUPDATED_EVENT = new Event("VerifierAuthorizationUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    protected UpdateGovernance(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<UpdateDecisionRecordedEventResponse> getUpdateDecisionRecordedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UPDATEDECISIONRECORDED_EVENT, transactionReceipt);
        ArrayList<UpdateDecisionRecordedEventResponse> responses = new ArrayList<UpdateDecisionRecordedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpdateDecisionRecordedEventResponse typedResponse = new UpdateDecisionRecordedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.candidateHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.attestationHash = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.reportHash = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.verifierVersionHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.verifier = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.decision = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeUpdateDecisionRecordedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(UPDATEDECISIONRECORDED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeUpdateDecisionRecordedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(UPDATEDECISIONRECORDED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<VerifierAuthorizationUpdatedEventResponse> getVerifierAuthorizationUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(VERIFIERAUTHORIZATIONUPDATED_EVENT, transactionReceipt);
        ArrayList<VerifierAuthorizationUpdatedEventResponse> responses = new ArrayList<VerifierAuthorizationUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VerifierAuthorizationUpdatedEventResponse typedResponse = new VerifierAuthorizationUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.verifier = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.authorized = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeVerifierAuthorizationUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(VERIFIERAUTHORIZATIONUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeVerifierAuthorizationUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(VERIFIERAUTHORIZATIONUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public Boolean authorizedVerifiers(String param0) throws ContractException {
        final Function function = new Function(FUNC_AUTHORIZEDVERIFIERS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodAuthorizedVerifiersRawFunction(String param0) throws
            ContractException {
        final Function function = new Function(FUNC_AUTHORIZEDVERIFIERS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Boolean exists(byte[] candidateHash) throws ContractException {
        final Function function = new Function(FUNC_EXISTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(candidateHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodExistsRawFunction(byte[] candidateHash) throws ContractException {
        final Function function = new Function(FUNC_EXISTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(candidateHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public Tuple8<byte[], byte[], byte[], String, BigInteger, BigInteger, BigInteger, Boolean> getDecision(
            byte[] candidateHash) throws ContractException {
        final Function function = new Function(FUNC_GETDECISION, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(candidateHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint8>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple8<byte[], byte[], byte[], String, BigInteger, BigInteger, BigInteger, Boolean>(
                (byte[]) results.get(0).getValue(), 
                (byte[]) results.get(1).getValue(), 
                (byte[]) results.get(2).getValue(), 
                (String) results.get(3).getValue(), 
                (BigInteger) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue(), 
                (Boolean) results.get(7).getValue());
    }

    public Function getMethodGetDecisionRawFunction(byte[] candidateHash) throws ContractException {
        final Function function = new Function(FUNC_GETDECISION, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(candidateHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint8>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
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

    public TransactionReceipt recordDecision(byte[] candidateHash, byte[] reportHash,
            byte[] attestationHash, byte[] verifierVersionHash, BigInteger decisionValue) {
        final Function function = new Function(
                FUNC_RECORDDECISION, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(candidateHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(reportHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(attestationHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(verifierVersionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8(decisionValue)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return executeTransaction(function);
    }

    public Function getMethodRecordDecisionRawFunction(byte[] candidateHash, byte[] reportHash,
            byte[] attestationHash, byte[] verifierVersionHash, BigInteger decisionValue) throws
            ContractException {
        final Function function = new Function(FUNC_RECORDDECISION, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(candidateHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(reportHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(attestationHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(verifierVersionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8(decisionValue)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForRecordDecision(byte[] candidateHash, byte[] reportHash,
            byte[] attestationHash, byte[] verifierVersionHash, BigInteger decisionValue) {
        final Function function = new Function(
                FUNC_RECORDDECISION, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(candidateHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(reportHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(attestationHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(verifierVersionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8(decisionValue)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return createSignedTransaction(function);
    }

    public String recordDecision(byte[] candidateHash, byte[] reportHash, byte[] attestationHash,
            byte[] verifierVersionHash, BigInteger decisionValue, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_RECORDDECISION, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(candidateHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(reportHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(attestationHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(verifierVersionHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8(decisionValue)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple5<byte[], byte[], byte[], byte[], BigInteger> getRecordDecisionInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_RECORDDECISION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint8>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple5<byte[], byte[], byte[], byte[], BigInteger>(

                (byte[]) results.get(0).getValue(), 
                (byte[]) results.get(1).getValue(), 
                (byte[]) results.get(2).getValue(), 
                (byte[]) results.get(3).getValue(), 
                (BigInteger) results.get(4).getValue()
                );
    }

    public TransactionReceipt setAuthorizedVerifier(String verifier, Boolean authorized) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDVERIFIER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(verifier), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetAuthorizedVerifierRawFunction(String verifier, Boolean authorized)
            throws ContractException {
        final Function function = new Function(FUNC_SETAUTHORIZEDVERIFIER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(verifier), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetAuthorizedVerifier(String verifier,
            Boolean authorized) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDVERIFIER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(verifier), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setAuthorizedVerifier(String verifier, Boolean authorized,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDVERIFIER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(verifier), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, Boolean> getSetAuthorizedVerifierInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETAUTHORIZEDVERIFIER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Boolean>(

                (String) results.get(0).getValue(), 
                (Boolean) results.get(1).getValue()
                );
    }

    public static UpdateGovernance load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new UpdateGovernance(contractAddress, client, credential);
    }

    public static UpdateGovernance deploy(Client client, CryptoKeyPair credential) throws
            ContractException {
        return deploy(UpdateGovernance.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }

    public static class UpdateDecisionRecordedEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] candidateHash;

        public byte[] attestationHash;

        public byte[] reportHash;

        public byte[] verifierVersionHash;

        public String verifier;

        public BigInteger decision;
    }

    public static class VerifierAuthorizationUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public String verifier;

        public Boolean authorized;
    }
}

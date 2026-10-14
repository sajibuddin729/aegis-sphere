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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
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
public class AgentWallet extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b506040516108df3803806108df83398101604081905261002f91610145565b6001600160a01b03821661008a5760405162461bcd60e51b815260206004820152601b60248201527f41454749533a20494e56414c49445f504f495f5245474953545259000000000060448201526064015b60405180910390fd5b6001600160a01b0381166100e05760405162461bcd60e51b815260206004820152601a60248201527f41454749533a20494e56414c49445f5249534b5f454e47494e450000000000006044820152606401610081565b60008054336001600160a01b031991821681179092556001805482169092179091556002805482166001600160a01b039485161790556003805490911691909216179055610178565b80516001600160a01b038116811461014057600080fd5b919050565b6000806040838503121561015857600080fd5b61016183610129565b915061016f60208401610129565b90509250929050565b610758806101876000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80638da5cb5b1161005b5780638da5cb5b146100f657806392b888ae14610109578063968ddd171461011c578063fe18c7981461013157600080fd5b8063185ef304146100825780633aecd0e3146100b257806364012698146100d3575b600080fd5b600254610095906001600160a01b031681565b6040516001600160a01b0390911681526020015b60405180910390f35b6100c56100c0366004610667565b610144565b6040519081526020016100a9565b6100e66100e1366004610689565b6101b5565b60405190151581526020016100a9565b600054610095906001600160a01b031681565b600354610095906001600160a01b031681565b61012f61012a366004610667565b610560565b005b600154610095906001600160a01b031681565b6040516370a0823160e01b81523060048201526000906001600160a01b038316906370a0823190602401602060405180830381865afa15801561018b573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906101af91906106e7565b92915050565b6001546000906001600160a01b031633146102175760405162461bcd60e51b815260206004820152601960248201527f41454749533a204e4f545f4147454e545f4558454355544f520000000000000060448201526064015b60405180910390fd5b600354600160a01b900460ff16156102655760405162461bcd60e51b815260206004820152601160248201527041454749533a205245454e5452414e435960781b604482015260640161020e565b6003805460ff60a01b1916600160a01b1790556001600160a01b0388166102c55760405162461bcd60e51b815260206004820152601460248201527320a2a3a4a99d1024a72b20a624a22faa27a5a2a760611b604482015260640161020e565b6001600160a01b03871661031b5760405162461bcd60e51b815260206004820152601860248201527f41454749533a20494e56414c49445f524543495049454e540000000000000000604482015260640161020e565b60035460405163b70e5d6b60e01b81526001600160a01b038a81166004830152898116602483015260448201899052606482018590529091169063b70e5d6b906084016020604051808303816000875af115801561037d573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103a191906106e7565b5060025460405163eec95f6f60e01b81526004810187905260248101869052604481018590526001600160a01b038a81166064830152898116608483015260a4820189905260c482018590529091169063eec95f6f9060e401600060405180830381600087803b15801561041457600080fd5b505af1158015610428573d6000803e3d6000fd5b505060405163a9059cbb60e01b81526001600160a01b038a81166004830152602482018a9052600093508b16915063a9059cbb906044016020604051808303816000875af115801561047e573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104a29190610700565b9050806104ea5760405162461bcd60e51b8152602060048201526016602482015275105151d254ce881514905394d1915497d1905253115160521b604482015260640161020e565b604080516001600160a01b038b81168252602082018a90529181018590526060810186905290891690869088907ffe92bfae8b21d45fe292efb052e91adce162e5b9f68b758f00bc30e1de4d0ce49060800160405180910390a450506003805460ff60a01b191690555060019695505050505050565b6000546001600160a01b031633146105ad5760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b604482015260640161020e565b6001600160a01b0381166105f95760405162461bcd60e51b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b604482015260640161020e565b600180546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f45cc89b678bd6d6786c460ef06a5329403ce7395174d8146dae88c837cc9ced790600090a35050565b80356001600160a01b038116811461066257600080fd5b919050565b60006020828403121561067957600080fd5b6106828261064b565b9392505050565b600080600080600080600060e0888a0312156106a457600080fd5b6106ad8861064b565b96506106bb6020890161064b565b96999698505050506040850135946060810135946080820135945060a0820135935060c0909101359150565b6000602082840312156106f957600080fd5b5051919050565b60006020828403121561071257600080fd5b8151801515811461068257600080fdfea26469706673582212208dadcee7247e8d3195314df63b5bc3e3d989e1a0fe03d901c4708d292eb7df1b64736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b506040516108e83803806108e883398101604081905261002f91610147565b6001600160a01b03821661008b57604051636381e58960e11b815260206004820152601b60248201527f41454749533a20494e56414c49445f504f495f5245474953545259000000000060448201526064015b60405180910390fd5b6001600160a01b0381166100e257604051636381e58960e11b815260206004820152601a60248201527f41454749533a20494e56414c49445f5249534b5f454e47494e450000000000006044820152606401610082565b60008054336001600160a01b031991821681179092556001805482169092179091556002805482166001600160a01b03948516179055600380549091169190921617905561017a565b80516001600160a01b038116811461014257600080fd5b919050565b6000806040838503121561015a57600080fd5b6101638361012b565b91506101716020840161012b565b90509250929050565b61075f806101896000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c8063760b8dc01161005b578063760b8dc0146100e6578063ccb3064b146100f9578063e9a1efb71461011c578063f573a3b51461013157600080fd5b80630a4c6e88146100825780635089e2c8146100b25780635f4c780f146100c5575b600080fd5b600254610095906001600160a01b031681565b6040516001600160a01b0390911681526020015b60405180910390f35b600054610095906001600160a01b031681565b6100d86100d336600461066e565b610144565b6040519081526020016100a9565b600154610095906001600160a01b031681565b61010c610107366004610690565b6101b5565b60405190151581526020016100a9565b61012f61012a36600461066e565b610565565b005b600354610095906001600160a01b031681565b604051636645f38760e11b81523060048201526000906001600160a01b0383169063cc8be70e90602401602060405180830381865afa15801561018b573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906101af91906106ee565b92915050565b6001546000906001600160a01b0316331461021857604051636381e58960e11b815260206004820152601960248201527f41454749533a204e4f545f4147454e545f4558454355544f520000000000000060448201526064015b60405180910390fd5b600354600160a01b900460ff161561026757604051636381e58960e11b815260206004820152601160248201527041454749533a205245454e5452414e435960781b604482015260640161020f565b6003805460ff60a01b1916600160a01b1790556001600160a01b0388166102c857604051636381e58960e11b815260206004820152601460248201527320a2a3a4a99d1024a72b20a624a22faa27a5a2a760611b604482015260640161020f565b6001600160a01b03871661031f57604051636381e58960e11b815260206004820152601860248201527f41454749533a20494e56414c49445f524543495049454e540000000000000000604482015260640161020f565b60035460405163021a954360e41b81526001600160a01b038a8116600483015289811660248301526044820189905260648201859052909116906321a95430906084016020604051808303816000875af1158015610381573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103a591906106ee565b50600254604051638b8d558d60e01b81526004810187905260248101869052604481018590526001600160a01b038a81166064830152898116608483015260a4820189905260c4820185905290911690638b8d558d9060e401600060405180830381600087803b15801561041857600080fd5b505af115801561042c573d6000803e3d6000fd5b5050604051636904e96560e01b81526001600160a01b038a81166004830152602482018a9052600093508b169150636904e965906044016020604051808303816000875af1158015610482573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104a69190610707565b9050806104ef57604051636381e58960e11b8152602060048201526016602482015275105151d254ce881514905394d1915497d1905253115160521b604482015260640161020f565b604080516001600160a01b038b81168252602082018a90529181018590526060810186905290891690869088907f8182c523a246240eb8757e00eb5f12f0a263480e5f5e6aae4d8e61f299aa296b9060800160405180910390a450506003805460ff60a01b191690555060019695505050505050565b6000546001600160a01b031633146105b357604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b604482015260640161020f565b6001600160a01b03811661060057604051636381e58960e11b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b604482015260640161020f565b600180546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f89db334cc61f69b3a58a4c39f5a26af3223e47cfb57a6d937a65badeeec8243b90600090a35050565b80356001600160a01b038116811461066957600080fd5b919050565b60006020828403121561068057600080fd5b61068982610652565b9392505050565b600080600080600080600060e0888a0312156106ab57600080fd5b6106b488610652565b96506106c260208901610652565b96999698505050506040850135946060810135946080820135945060a0820135935060c0909101359150565b60006020828403121561070057600080fd5b5051919050565b60006020828403121561071957600080fd5b8151801515811461068957600080fdfea2646970667358221220281ed064f2f5677b45d03d690d2ebe0a362abfd08a7446d7ff717717cda5f07f64736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"poiRegistryAddress\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"riskEngineAddress\",\"type\":\"address\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"previousExecutor\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"newExecutor\",\"type\":\"address\"}],\"name\":\"AgentExecutorUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"poiHash\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"intentId\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"recipient\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"riskScoreBps\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"bytes32\",\"name\":\"policyHash\",\"type\":\"bytes32\"}],\"name\":\"AutonomousTransferExecuted\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"agentExecutor\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[4263036824,1980468672],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"recipient\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"},{\"internalType\":\"bytes32\",\"name\":\"poiHash\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"intentId\",\"type\":\"bytes32\"},{\"internalType\":\"bytes32\",\"name\":\"policyHash\",\"type\":\"bytes32\"},{\"internalType\":\"uint256\",\"name\":\"riskScoreBps\",\"type\":\"uint256\"}],\"name\":\"executeTokenTransfer\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1677797016,3434284619],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"}],\"name\":\"getTokenBalance\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[988598499,1598846991],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"inputs\":[],\"name\":\"poiRegistry\",\"outputs\":[{\"internalType\":\"contract IPoIRegistry\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[408875780,172781192],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"inputs\":[],\"name\":\"riskEngine\",\"outputs\":[{\"internalType\":\"contract IRiskCircuitBreaker\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2461567150,4117996469],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]},{\"kind\":4,\"value\":[1]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"newExecutor\",\"type\":\"address\"}],\"name\":\"setAgentExecutor\",\"outputs\":[],\"selector\":[2525879575,3919703991],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_AGENTEXECUTOR = "agentExecutor";

    public static final String FUNC_EXECUTETOKENTRANSFER = "executeTokenTransfer";

    public static final String FUNC_GETTOKENBALANCE = "getTokenBalance";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_POIREGISTRY = "poiRegistry";

    public static final String FUNC_RISKENGINE = "riskEngine";

    public static final String FUNC_SETAGENTEXECUTOR = "setAgentExecutor";

    public static final Event AGENTEXECUTORUPDATED_EVENT = new Event("AgentExecutorUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event AUTONOMOUSTRANSFEREXECUTED_EVENT = new Event("AutonomousTransferExecuted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>() {}));
    ;

    protected AgentWallet(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<AgentExecutorUpdatedEventResponse> getAgentExecutorUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(AGENTEXECUTORUPDATED_EVENT, transactionReceipt);
        ArrayList<AgentExecutorUpdatedEventResponse> responses = new ArrayList<AgentExecutorUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AgentExecutorUpdatedEventResponse typedResponse = new AgentExecutorUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousExecutor = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newExecutor = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeAgentExecutorUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(AGENTEXECUTORUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeAgentExecutorUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(AGENTEXECUTORUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<AutonomousTransferExecutedEventResponse> getAutonomousTransferExecutedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(AUTONOMOUSTRANSFEREXECUTED_EVENT, transactionReceipt);
        ArrayList<AutonomousTransferExecutedEventResponse> responses = new ArrayList<AutonomousTransferExecutedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AutonomousTransferExecutedEventResponse typedResponse = new AutonomousTransferExecutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.poiHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.recipient = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.token = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.riskScoreBps = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.policyHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeAutonomousTransferExecutedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(AUTONOMOUSTRANSFEREXECUTED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeAutonomousTransferExecutedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(AUTONOMOUSTRANSFEREXECUTED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public String agentExecutor() throws ContractException {
        final Function function = new Function(FUNC_AGENTEXECUTOR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodAgentExecutorRawFunction() throws ContractException {
        final Function function = new Function(FUNC_AGENTEXECUTOR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return function;
    }

    public TransactionReceipt executeTokenTransfer(String token, String recipient,
            BigInteger amount, byte[] poiHash, byte[] intentId, byte[] policyHash,
            BigInteger riskScoreBps) {
        final Function function = new Function(
                FUNC_EXECUTETOKENTRANSFER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(intentId), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodExecuteTokenTransferRawFunction(String token, String recipient,
            BigInteger amount, byte[] poiHash, byte[] intentId, byte[] policyHash,
            BigInteger riskScoreBps) throws ContractException {
        final Function function = new Function(FUNC_EXECUTETOKENTRANSFER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(intentId), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public String getSignedTransactionForExecuteTokenTransfer(String token, String recipient,
            BigInteger amount, byte[] poiHash, byte[] intentId, byte[] policyHash,
            BigInteger riskScoreBps) {
        final Function function = new Function(
                FUNC_EXECUTETOKENTRANSFER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(intentId), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String executeTokenTransfer(String token, String recipient, BigInteger amount,
            byte[] poiHash, byte[] intentId, byte[] policyHash, BigInteger riskScoreBps,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_EXECUTETOKENTRANSFER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(poiHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(intentId), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32(policyHash), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple7<String, String, BigInteger, byte[], byte[], byte[], BigInteger> getExecuteTokenTransferInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_EXECUTETOKENTRANSFER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple7<String, String, BigInteger, byte[], byte[], byte[], BigInteger>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue(), 
                (byte[]) results.get(3).getValue(), 
                (byte[]) results.get(4).getValue(), 
                (byte[]) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue()
                );
    }

    public Tuple1<Boolean> getExecuteTokenTransferOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_EXECUTETOKENTRANSFER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Boolean>(

                (Boolean) results.get(0).getValue()
                );
    }

    public BigInteger getTokenBalance(String token) throws ContractException {
        final Function function = new Function(FUNC_GETTOKENBALANCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodGetTokenBalanceRawFunction(String token) throws ContractException {
        final Function function = new Function(FUNC_GETTOKENBALANCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
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

    public String poiRegistry() throws ContractException {
        final Function function = new Function(FUNC_POIREGISTRY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodPoiRegistryRawFunction() throws ContractException {
        final Function function = new Function(FUNC_POIREGISTRY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return function;
    }

    public String riskEngine() throws ContractException {
        final Function function = new Function(FUNC_RISKENGINE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodRiskEngineRawFunction() throws ContractException {
        final Function function = new Function(FUNC_RISKENGINE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return function;
    }

    public TransactionReceipt setAgentExecutor(String newExecutor) {
        final Function function = new Function(
                FUNC_SETAGENTEXECUTOR, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(newExecutor)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetAgentExecutorRawFunction(String newExecutor) throws
            ContractException {
        final Function function = new Function(FUNC_SETAGENTEXECUTOR, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(newExecutor)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetAgentExecutor(String newExecutor) {
        final Function function = new Function(
                FUNC_SETAGENTEXECUTOR, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(newExecutor)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setAgentExecutor(String newExecutor, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETAGENTEXECUTOR, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(newExecutor)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getSetAgentExecutorInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETAGENTEXECUTOR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>(

                (String) results.get(0).getValue()
                );
    }

    public static AgentWallet load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new AgentWallet(contractAddress, client, credential);
    }

    public static AgentWallet deploy(Client client, CryptoKeyPair credential,
            String poiRegistryAddress, String riskEngineAddress) throws ContractException {
        byte[] encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(poiRegistryAddress), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(riskEngineAddress)));
        return deploy(AgentWallet.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), encodedConstructor, null);
    }

    public static class AgentExecutorUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public String previousExecutor;

        public String newExecutor;
    }

    public static class AutonomousTransferExecutedEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] poiHash;

        public byte[] intentId;

        public String recipient;

        public String token;

        public BigInteger amount;

        public BigInteger riskScoreBps;

        public byte[] policyHash;
    }
}

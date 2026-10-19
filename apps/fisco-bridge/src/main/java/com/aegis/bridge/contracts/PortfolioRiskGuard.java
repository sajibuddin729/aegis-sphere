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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class PortfolioRiskGuard extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b5060405161092738038061092783398101604081905261002f91610149565b600083116100845760405162461bcd60e51b815260206004820152601860248201527f41454749533a20494e56414c49445f4d41585f5452414445000000000000000060448201526064015b60405180910390fd5b828210156100d45760405162461bcd60e51b815260206004820152601a60248201527f41454749533a20494e56414c49445f4441494c595f4c494d4954000000000000604482015260640161007b565b6127108111156101265760405162461bcd60e51b815260206004820152601360248201527f41454749533a20494e56414c49445f5249534b00000000000000000000000000604482015260640161007b565b600080546001600160a01b03191633179055600192909255600255600355610177565b60008060006060848603121561015e57600080fd5b8351925060208401519150604084015190509250925092565b6107a1806101866000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c80635c975abb116100715780635c975abb146101385780638da5cb5b1461014c5780639b928f7d14610177578063cb58e24a14610180578063d1eb3e4a14610189578063e9385271146101b457600080fd5b80630a3cb21b146100ae57806316c38b3c146100e65780632b71fb3a146100fb57806349ff658e1461010e5780634c7dfb381461012f575b600080fd5b6100d16100bc366004610657565b60046020526000908152604090205460ff1681565b60405190151581526020015b60405180910390f35b6100f96100f4366004610689565b6101c7565b005b6100f96101093660046106a4565b610271565b61012161011c366004610657565b610369565b6040519081526020016100dd565b61012160015481565b6000546100d190600160a01b900460ff1681565b60005461015f906001600160a01b031681565b6040516001600160a01b0390911681526020016100dd565b61012160025481565b61012160035481565b6101216101973660046106d7565b600560209081526000928352604080842090915290825290205481565b6101216101c2366004610701565b6103a6565b6000546001600160a01b031633146102195760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064015b60405180910390fd5b60008054821515600160a01b0260ff60a01b199091161790556040517f77860e247ab9186dbe64e5bd0e0b93273cc4273e01818420e788f500078886f59061026690831515815260200190565b60405180910390a150565b6000546001600160a01b031633146102be5760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b6044820152606401610210565b6001600160a01b03821661030a5760405162461bcd60e51b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b6044820152606401610210565b6001600160a01b038216600081815260046020908152604091829020805460ff191685151590811790915591519182527fc06a533685b96ece1dece3f4f59d06ff3c8f62e13d4388b9fc66a51f1b1ac9d3910160405180910390a25050565b6000806103796201518042610723565b6001600160a01b039093166000908152600560209081526040808320958352949052929092205492915050565b3360009081526004602052604081205460ff166104055760405162461bcd60e51b815260206004820152601d60248201527f41454749533a204d414e414745525f4e4f545f415554484f52495a45440000006044820152606401610210565b600054600160a01b900460ff161561045f5760405162461bcd60e51b815260206004820152601760248201527f41454749533a20504f5254464f4c494f5f5041555345440000000000000000006044820152606401610210565b600083116104a65760405162461bcd60e51b8152602060048201526014602482015273105151d254ce8816915493d7d393d51253d3905360621b6044820152606401610210565b6001548311156104f85760405162461bcd60e51b815260206004820152601c60248201527f41454749533a20504f5254464f4c494f5f54524144455f4c494d4954000000006044820152606401610210565b60035482111561054a5760405162461bcd60e51b815260206004820152601e60248201527f41454749533a20504f5254464f4c494f5f5249534b5f544f4f5f4849474800006044820152606401610210565b60006105596201518042610723565b33600090815260056020908152604080832084845290915281205491925090610583908690610745565b90506002548111156105d75760405162461bcd60e51b815260206004820152601c60248201527f41454749533a20504f5254464f4c494f5f4441494c595f4c494d4954000000006044820152606401610210565b33600081815260056020908152604080832086845282529182902084905581518881529081018790529081018390527f3870f2bbc3a2ee62f96a4f775731686e98a2f3698da4d25e4cde7f0bfce0aaf59060600160405180910390a2949350505050565b80356001600160a01b038116811461065257600080fd5b919050565b60006020828403121561066957600080fd5b6106728261063b565b9392505050565b8035801515811461065257600080fd5b60006020828403121561069b57600080fd5b61067282610679565b600080604083850312156106b757600080fd5b6106c08361063b565b91506106ce60208401610679565b90509250929050565b600080604083850312156106ea57600080fd5b6106f38361063b565b946020939093013593505050565b6000806040838503121561071457600080fd5b50508035926020909101359150565b60008261074057634e487b7160e01b600052601260045260246000fd5b500490565b6000821982111561076657634e487b7160e01b600052601160045260246000fd5b50019056fea2646970667358221220163958010786423658c9c52e3348028161b9c2e4339a85b51a6de90cabb68da164736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b5060405161093338038061093383398101604081905261002f9161014c565b6000831161008557604051636381e58960e11b815260206004820152601860248201527f41454749533a20494e56414c49445f4d41585f5452414445000000000000000060448201526064015b60405180910390fd5b828210156100d657604051636381e58960e11b815260206004820152601a60248201527f41454749533a20494e56414c49445f4441494c595f4c494d4954000000000000604482015260640161007c565b61271081111561012957604051636381e58960e11b815260206004820152601360248201527f41454749533a20494e56414c49445f5249534b00000000000000000000000000604482015260640161007c565b600080546001600160a01b0319163317905560019290925560025560035561017a565b60008060006060848603121561016157600080fd5b8351925060208401519150604084015190509250925092565b6107aa806101896000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c806391c81a551161007157806391c81a5514610126578063bc97a15414610159578063cb3a650714610162578063d4bc960114610175578063db53e72f14610189578063e8381ce11461019c57600080fd5b806325eab4f9146100ae57806337399bf7146100ca57806341c90e36146100df5780635089e2c8146100e85780638e62b9f014610113575b600080fd5b6100b760035481565b6040519081526020015b60405180910390f35b6100dd6100d8366004610659565b6101c7565b005b6100b760015481565b6000546100fb906001600160a01b031681565b6040516001600160a01b0390911681526020016100c1565b6100dd610121366004610692565b610272565b6101496101343660046106c5565b60046020526000908152604090205460ff1681565b60405190151581526020016100c1565b6100b760025481565b6100b76101703660046106c5565b61036c565b60005461014990600160a01b900460ff1681565b6100b76101973660046106e0565b6103a9565b6100b76101aa366004610702565b600560209081526000928352604080842090915290825290205481565b6000546001600160a01b0316331461021a57604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064015b60405180910390fd5b60008054821515600160a01b0260ff60a01b199091161790556040517f2c95d1d4da94335341c6d5e662af98365eef1d79761fd3265f2356ed7ae6eeee9061026790831515815260200190565b60405180910390a150565b6000546001600160a01b031633146102c057604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b6044820152606401610211565b6001600160a01b03821661030d57604051636381e58960e11b815260206004820152601360248201527241454749533a205a45524f5f4144445245535360681b6044820152606401610211565b6001600160a01b038216600081815260046020908152604091829020805460ff191685151590811790915591519182527f10fea7e64ee653f3d5f018eb8150c01d6b2cd36353e27fcde019b30b425036ec910160405180910390a25050565b60008061037c620151804261072c565b6001600160a01b039093166000908152600560209081526040808320958352949052929092205492915050565b3360009081526004602052604081205460ff1661040957604051636381e58960e11b815260206004820152601d60248201527f41454749533a204d414e414745525f4e4f545f415554484f52495a45440000006044820152606401610211565b600054600160a01b900460ff161561046457604051636381e58960e11b815260206004820152601760248201527f41454749533a20504f5254464f4c494f5f5041555345440000000000000000006044820152606401610211565b600083116104ac57604051636381e58960e11b8152602060048201526014602482015273105151d254ce8816915493d7d393d51253d3905360621b6044820152606401610211565b6001548311156104ff57604051636381e58960e11b815260206004820152601c60248201527f41454749533a20504f5254464f4c494f5f54524144455f4c494d4954000000006044820152606401610211565b60035482111561055257604051636381e58960e11b815260206004820152601e60248201527f41454749533a20504f5254464f4c494f5f5249534b5f544f4f5f4849474800006044820152606401610211565b6000610561620151804261072c565b3360009081526005602090815260408083208484529091528120549192509061058b90869061074e565b90506002548111156105e057604051636381e58960e11b815260206004820152601c60248201527f41454749533a20504f5254464f4c494f5f4441494c595f4c494d4954000000006044820152606401610211565b33600081815260056020908152604080832086845282529182902084905581518881529081018790529081018390527f80080cf0d1fe12ff1324e9a3139c38a1287c53e426c9feb138d6298bfb89fd4d9060600160405180910390a2949350505050565b8035801515811461065457600080fd5b919050565b60006020828403121561066b57600080fd5b61067482610644565b9392505050565b80356001600160a01b038116811461065457600080fd5b600080604083850312156106a557600080fd5b6106ae8361067b565b91506106bc60208401610644565b90509250929050565b6000602082840312156106d757600080fd5b6106748261067b565b600080604083850312156106f357600080fd5b50508035926020909101359150565b6000806040838503121561071557600080fd5b61071e8361067b565b946020939093013593505050565b6000826107495763b95aa35560e01b600052601260045260246000fd5b500490565b6000821982111561076f5763b95aa35560e01b600052601160045260246000fd5b50019056fea2646970667358221220efacee835a554953379e81ab6e522d97888512c1b2a56d74ba911475c531a87e64736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"initialMaxTradeNotional\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"initialMaxDailyNotional\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"initialMaxRiskScoreBps\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"manager\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"ManagerAuthorizationUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"paused\",\"type\":\"bool\"}],\"name\":\"PauseUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"manager\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"notionalAcnyMinor\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"riskScoreBps\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"spentToday\",\"type\":\"uint256\"}],\"name\":\"PortfolioRiskApproved\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":3,\"slot\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"authorizedManagers\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[171749915,2445810261],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"manager\",\"type\":\"address\"}],\"name\":\"getSpentToday\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[1241474446,3409601799],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"inputs\":[],\"name\":\"maxDailyNotional\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[2610073469,3164053844],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"inputs\":[],\"name\":\"maxRiskScoreBps\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[3411599946,636138745],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"maxTradeNotional\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[1283324728,1103695414],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"paused\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1553423035,3569128961],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"manager\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"setAuthorizedManager\",\"outputs\":[],\"selector\":[728890170,2388834800],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bool\",\"name\":\"state\",\"type\":\"bool\"}],\"name\":\"setPaused\",\"outputs\":[],\"selector\":[381913916,926522359],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":5,\"value\":[0]},{\"kind\":3,\"slot\":5,\"value\":[1]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"spentByManagerDay\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[3521855050,3895991521],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":2,\"slot\":4,\"value\":[0]},{\"kind\":2,\"slot\":5,\"value\":[0]},{\"kind\":4,\"value\":[0]},{\"kind\":4,\"value\":[1]},{\"kind\":4,\"value\":[2]},{\"kind\":4,\"value\":[3]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"notionalAcnyMinor\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"riskScoreBps\",\"type\":\"uint256\"}],\"name\":\"validateAndConsume\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[3912782449,3679708975],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_AUTHORIZEDMANAGERS = "authorizedManagers";

    public static final String FUNC_GETSPENTTODAY = "getSpentToday";

    public static final String FUNC_MAXDAILYNOTIONAL = "maxDailyNotional";

    public static final String FUNC_MAXRISKSCOREBPS = "maxRiskScoreBps";

    public static final String FUNC_MAXTRADENOTIONAL = "maxTradeNotional";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PAUSED = "paused";

    public static final String FUNC_SETAUTHORIZEDMANAGER = "setAuthorizedManager";

    public static final String FUNC_SETPAUSED = "setPaused";

    public static final String FUNC_SPENTBYMANAGERDAY = "spentByManagerDay";

    public static final String FUNC_VALIDATEANDCONSUME = "validateAndConsume";

    public static final Event MANAGERAUTHORIZATIONUPDATED_EVENT = new Event("ManagerAuthorizationUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event PAUSEUPDATED_EVENT = new Event("PauseUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event PORTFOLIORISKAPPROVED_EVENT = new Event("PortfolioRiskApproved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    protected PortfolioRiskGuard(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<ManagerAuthorizationUpdatedEventResponse> getManagerAuthorizationUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MANAGERAUTHORIZATIONUPDATED_EVENT, transactionReceipt);
        ArrayList<ManagerAuthorizationUpdatedEventResponse> responses = new ArrayList<ManagerAuthorizationUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ManagerAuthorizationUpdatedEventResponse typedResponse = new ManagerAuthorizationUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.manager = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.authorized = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeManagerAuthorizationUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(MANAGERAUTHORIZATIONUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeManagerAuthorizationUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(MANAGERAUTHORIZATIONUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<PauseUpdatedEventResponse> getPauseUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PAUSEUPDATED_EVENT, transactionReceipt);
        ArrayList<PauseUpdatedEventResponse> responses = new ArrayList<PauseUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PauseUpdatedEventResponse typedResponse = new PauseUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.paused = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribePauseUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(PAUSEUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribePauseUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(PAUSEUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<PortfolioRiskApprovedEventResponse> getPortfolioRiskApprovedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PORTFOLIORISKAPPROVED_EVENT, transactionReceipt);
        ArrayList<PortfolioRiskApprovedEventResponse> responses = new ArrayList<PortfolioRiskApprovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PortfolioRiskApprovedEventResponse typedResponse = new PortfolioRiskApprovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.manager = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.notionalAcnyMinor = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.riskScoreBps = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.spentToday = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribePortfolioRiskApprovedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(PORTFOLIORISKAPPROVED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribePortfolioRiskApprovedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(PORTFOLIORISKAPPROVED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public Boolean authorizedManagers(String param0) throws ContractException {
        final Function function = new Function(FUNC_AUTHORIZEDMANAGERS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodAuthorizedManagersRawFunction(String param0) throws ContractException {
        final Function function = new Function(FUNC_AUTHORIZEDMANAGERS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public BigInteger getSpentToday(String manager) throws ContractException {
        final Function function = new Function(FUNC_GETSPENTTODAY, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(manager)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodGetSpentTodayRawFunction(String manager) throws ContractException {
        final Function function = new Function(FUNC_GETSPENTTODAY, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(manager)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public BigInteger maxDailyNotional() throws ContractException {
        final Function function = new Function(FUNC_MAXDAILYNOTIONAL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodMaxDailyNotionalRawFunction() throws ContractException {
        final Function function = new Function(FUNC_MAXDAILYNOTIONAL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public BigInteger maxRiskScoreBps() throws ContractException {
        final Function function = new Function(FUNC_MAXRISKSCOREBPS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodMaxRiskScoreBpsRawFunction() throws ContractException {
        final Function function = new Function(FUNC_MAXRISKSCOREBPS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public BigInteger maxTradeNotional() throws ContractException {
        final Function function = new Function(FUNC_MAXTRADENOTIONAL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodMaxTradeNotionalRawFunction() throws ContractException {
        final Function function = new Function(FUNC_MAXTRADENOTIONAL, 
                Arrays.<Type>asList(), 
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

    public Boolean paused() throws ContractException {
        final Function function = new Function(FUNC_PAUSED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodPausedRawFunction() throws ContractException {
        final Function function = new Function(FUNC_PAUSED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public TransactionReceipt setAuthorizedManager(String manager, Boolean authorized) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDMANAGER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(manager), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetAuthorizedManagerRawFunction(String manager, Boolean authorized)
            throws ContractException {
        final Function function = new Function(FUNC_SETAUTHORIZEDMANAGER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(manager), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetAuthorizedManager(String manager, Boolean authorized) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDMANAGER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(manager), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setAuthorizedManager(String manager, Boolean authorized,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETAUTHORIZEDMANAGER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(manager), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(authorized)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, Boolean> getSetAuthorizedManagerInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETAUTHORIZEDMANAGER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Boolean>(

                (String) results.get(0).getValue(), 
                (Boolean) results.get(1).getValue()
                );
    }

    public TransactionReceipt setPaused(Boolean state) {
        final Function function = new Function(
                FUNC_SETPAUSED, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(state)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetPausedRawFunction(Boolean state) throws ContractException {
        final Function function = new Function(FUNC_SETPAUSED, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(state)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetPaused(Boolean state) {
        final Function function = new Function(
                FUNC_SETPAUSED, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(state)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setPaused(Boolean state, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETPAUSED, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(state)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<Boolean> getSetPausedInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETPAUSED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Boolean>(

                (Boolean) results.get(0).getValue()
                );
    }

    public BigInteger spentByManagerDay(String param0, BigInteger param1) throws ContractException {
        final Function function = new Function(FUNC_SPENTBYMANAGERDAY, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodSpentByManagerDayRawFunction(String param0, BigInteger param1) throws
            ContractException {
        final Function function = new Function(FUNC_SPENTBYMANAGERDAY, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public TransactionReceipt validateAndConsume(BigInteger notionalAcnyMinor,
            BigInteger riskScoreBps) {
        final Function function = new Function(
                FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(notionalAcnyMinor), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodValidateAndConsumeRawFunction(BigInteger notionalAcnyMinor,
            BigInteger riskScoreBps) throws ContractException {
        final Function function = new Function(FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(notionalAcnyMinor), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public String getSignedTransactionForValidateAndConsume(BigInteger notionalAcnyMinor,
            BigInteger riskScoreBps) {
        final Function function = new Function(
                FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(notionalAcnyMinor), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String validateAndConsume(BigInteger notionalAcnyMinor, BigInteger riskScoreBps,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(notionalAcnyMinor), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<BigInteger, BigInteger> getValidateAndConsumeInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue()
                );
    }

    public Tuple1<BigInteger> getValidateAndConsumeOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>(

                (BigInteger) results.get(0).getValue()
                );
    }

    public static PortfolioRiskGuard load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new PortfolioRiskGuard(contractAddress, client, credential);
    }

    public static PortfolioRiskGuard deploy(Client client, CryptoKeyPair credential,
            BigInteger initialMaxTradeNotional, BigInteger initialMaxDailyNotional,
            BigInteger initialMaxRiskScoreBps) throws ContractException {
        byte[] encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(initialMaxTradeNotional), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(initialMaxDailyNotional), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(initialMaxRiskScoreBps)));
        return deploy(PortfolioRiskGuard.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), encodedConstructor, null);
    }

    public static class ManagerAuthorizationUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public String manager;

        public Boolean authorized;
    }

    public static class PauseUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public Boolean paused;
    }

    public static class PortfolioRiskApprovedEventResponse {
        public TransactionReceipt.Logs log;

        public String manager;

        public BigInteger notionalAcnyMinor;

        public BigInteger riskScoreBps;

        public BigInteger spentToday;
    }
}

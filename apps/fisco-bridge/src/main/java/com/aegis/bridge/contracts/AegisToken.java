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
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class AegisToken extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50600180546001600160a01b0319163317905561088e806100326000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c806340c10f191161007157806340c10f191461015257806370a08231146101675780638da5cb5b1461018757806395d89b41146101b2578063a9059cbb146101d5578063dd62ed3e146101e857600080fd5b806306fdde03146100ae578063095ea7b3146100eb57806318160ddd1461010e57806323b872dd14610125578063313ce56714610138575b600080fd5b6100d5604051806040016040528060088152602001674165676973434e5960c01b81525081565b6040516100e291906106ba565b60405180910390f35b6100fe6100f936600461072b565b610213565b60405190151581526020016100e2565b61011760005481565b6040519081526020016100e2565b6100fe610133366004610755565b6102a8565b610140600281565b60405160ff90911681526020016100e2565b61016561016036600461072b565b6103a0565b005b610117610175366004610791565b60026020526000908152604090205481565b60015461019a906001600160a01b031681565b6040516001600160a01b0390911681526020016100e2565b6100d56040518060400160405280600481526020016341434e5960e01b81525081565b6100fe6101e336600461072b565b610523565b6101176101f63660046107b3565b600360209081526000928352604080842090915290825290205481565b60006001600160a01b0383166102445760405162461bcd60e51b815260040161023b906107e6565b60405180910390fd5b3360008181526003602090815260408083206001600160a01b03881680855290835292819020869055518581529192917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925910160405180910390a350600192915050565b6001600160a01b03831660009081526003602090815260408083203384529091528120548281101561031c5760405162461bcd60e51b815260206004820152601960248201527f41454749533a20414c4c4f57414e43455f455843454544454400000000000000604482015260640161023b565b600019811461038a5761032f8382610829565b6001600160a01b038616600081815260036020908152604080832033808552908352928190208590555193845290927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925910160405180910390a35b610395858585610539565b506001949350505050565b6001546001600160a01b031633146103ed5760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b604482015260640161023b565b6001600160a01b0382166104135760405162461bcd60e51b815260040161023b906107e6565b600081116104585760405162461bcd60e51b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b604482015260640161023b565b806000808282546104699190610840565b90915550506001600160a01b03821660009081526002602052604081208054839290610496908490610840565b90915550506040518181526001600160a01b038316906000907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9060200160405180910390a3816001600160a01b03167f3f2c9d57c068687834f0de942a9babb9e5acab57d516d3480a3c16ee165a42738260405161051791815260200190565b60405180910390a25050565b6000610530338484610539565b50600192915050565b6001600160a01b03821661055f5760405162461bcd60e51b815260040161023b906107e6565b600081116105a45760405162461bcd60e51b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b604482015260640161023b565b6001600160a01b03831660009081526002602052604090205481111561060c5760405162461bcd60e51b815260206004820152601b60248201527f41454749533a20494e53554646494349454e545f42414c414e43450000000000604482015260640161023b565b6001600160a01b03831660009081526002602052604081208054839290610634908490610829565b90915550506001600160a01b03821660009081526002602052604081208054839290610661908490610840565b92505081905550816001600160a01b0316836001600160a01b03167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040516106ad91815260200190565b60405180910390a3505050565b600060208083528351808285015260005b818110156106e7578581018301518582016040015282016106cb565b818111156106f9576000604083870101525b50601f01601f1916929092016040019392505050565b80356001600160a01b038116811461072657600080fd5b919050565b6000806040838503121561073e57600080fd5b6107478361070f565b946020939093013593505050565b60008060006060848603121561076a57600080fd5b6107738461070f565b92506107816020850161070f565b9150604084013590509250925092565b6000602082840312156107a357600080fd5b6107ac8261070f565b9392505050565b600080604083850312156107c657600080fd5b6107cf8361070f565b91506107dd6020840161070f565b90509250929050565b60208082526013908201527241454749533a205a45524f5f4144445245535360681b604082015260600190565b634e487b7160e01b600052601160045260246000fd5b60008282101561083b5761083b610813565b500390565b6000821982111561085357610853610813565b50019056fea26469706673582212203e6d0aae1ab39e1426e5c0d93e0cbc0f8c6fdc706f5fab0a231b1faab9f494a664736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50600180546001600160a01b03191633179055610892806100326000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c80635bfa2796116100715780635bfa2796146101475780636904e96514610177578063852d92131461018a578063ad8a9731146101b5578063b11b6883146101c8578063cc8be70e146101ef57600080fd5b80630256e278146100ae5780631f2d4860146100ca57806346b13615146100ed5780634f5e00c7146101075780635089e2c81461011c575b600080fd5b6100b760005481565b6040519081526020015b60405180910390f35b6100dd6100d83660046106da565b61020f565b60405190151581526020016100c1565b6100f5600281565b60405160ff90911681526020016100c1565b61011a6101153660046106da565b6102a5565b005b60015461012f906001600160a01b031681565b6040516001600160a01b0390911681526020016100c1565b61016a6040518060400160405280600481526020016341434e5960e01b81525081565b6040516100c19190610704565b6100dd6101853660046106da565b61042b565b6100b7610198366004610759565b600360209081526000928352604080842090915290825290205481565b6100dd6101c336600461078c565b610441565b61016a604051806040016040528060088152602001674165676973434e5960c01b81525081565b6100b76101fd3660046107c8565b60026020526000908152604090205481565b60006001600160a01b03831661024157604051636381e58960e11b8152600401610238906107ea565b60405180910390fd5b3360008181526003602090815260408083206001600160a01b03881680855290835292819020869055518581529192917fd1e45707b3f71c77903b61f04c900f772db264b9bf618f1cc3308fb516eb6169910160405180910390a350600192915050565b6001546001600160a01b031633146102f357604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b6044820152606401610238565b6001600160a01b03821661031a57604051636381e58960e11b8152600401610238906107ea565b6000811161036057604051636381e58960e11b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b6044820152606401610238565b80600080828254610371919061082d565b90915550506001600160a01b0382166000908152600260205260408120805483929061039e90849061082d565b90915550506040518181526001600160a01b038316906000907f18f84334255a242551aa98c68047b5da8063eab9fbeaec1eddeea280044b9ff19060200160405180910390a3816001600160a01b03167f4b01acf99dfb56412b276d24130417f059056e9c387e4ec46559f794189d851b8260405161041f91815260200190565b60405180910390a25050565b600061043833848461053a565b50600192915050565b6001600160a01b0383166000908152600360209081526040808320338452909152812054828110156104b657604051636381e58960e11b815260206004820152601960248201527f41454749533a20414c4c4f57414e43455f4558434545444544000000000000006044820152606401610238565b6000198114610524576104c98382610845565b6001600160a01b038616600081815260036020908152604080832033808552908352928190208590555193845290927fd1e45707b3f71c77903b61f04c900f772db264b9bf618f1cc3308fb516eb6169910160405180910390a35b61052f85858561053a565b506001949350505050565b6001600160a01b03821661056157604051636381e58960e11b8152600401610238906107ea565b600081116105a757604051636381e58960e11b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b6044820152606401610238565b6001600160a01b03831660009081526002602052604090205481111561061057604051636381e58960e11b815260206004820152601b60248201527f41454749533a20494e53554646494349454e545f42414c414e434500000000006044820152606401610238565b6001600160a01b03831660009081526002602052604081208054839290610638908490610845565b90915550506001600160a01b0382166000908152600260205260408120805483929061066590849061082d565b92505081905550816001600160a01b0316836001600160a01b03167f18f84334255a242551aa98c68047b5da8063eab9fbeaec1eddeea280044b9ff1836040516106b191815260200190565b60405180910390a3505050565b80356001600160a01b03811681146106d557600080fd5b919050565b600080604083850312156106ed57600080fd5b6106f6836106be565b946020939093013593505050565b600060208083528351808285015260005b8181101561073157858101830151858201604001528201610715565b81811115610743576000604083870101525b50601f01601f1916929092016040019392505050565b6000806040838503121561076c57600080fd5b610775836106be565b9150610783602084016106be565b90509250929050565b6000806000606084860312156107a157600080fd5b6107aa846106be565b92506107b8602085016106be565b9150604084013590509250925092565b6000602082840312156107da57600080fd5b6107e3826106be565b9392505050565b60208082526013908201527241454749533a205a45524f5f4144445245535360681b604082015260600190565b63b95aa35560e01b600052601160045260246000fd5b6000821982111561084057610840610817565b500190565b60008282101561085757610857610817565b50039056fea26469706673582212200cc5345f7a15016fc91c0a0b0b51c7af50cec4592ae56253957c465a713e49a864736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"spender\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Approval\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"TokensMinted\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":3,\"slot\":3,\"value\":[0]},{\"kind\":3,\"slot\":3,\"value\":[1]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[3714247998,2234356243],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":2,\"slot\":3,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"spender\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[157198259,523061344],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[1889567281,3431720718],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[826074471,1186018837],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[0]},{\"kind\":4,\"value\":[1]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"mint\",\"outputs\":[],\"selector\":[1086394137,1331560647],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[117300739,2971363459],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[2514000705,1543120790],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[404098525,39248504],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":2,\"slot\":2,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[2835717307,1761929573],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":2,\"slot\":3,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[599290589,2911541041],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TOKENSMINTED_EVENT = new Event("TokensMinted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    protected AegisToken(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeApprovalEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(APPROVAL_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeApprovalEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(APPROVAL_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<TokensMintedEventResponse> getTokensMintedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENSMINTED_EVENT, transactionReceipt);
        ArrayList<TokensMintedEventResponse> responses = new ArrayList<TokensMintedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokensMintedEventResponse typedResponse = new TokensMintedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeTokensMintedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(TOKENSMINTED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeTokensMintedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(TOKENSMINTED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeTransferEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(TRANSFER_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeTransferEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(TRANSFER_EVENT);
        subscribeEvent(topic0,callback);
    }

    public BigInteger allowance(String param0, String param1) throws ContractException {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodAllowanceRawFunction(String param0, String param1) throws
            ContractException {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public TransactionReceipt approve(String spender, BigInteger amount) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(spender), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return executeTransaction(function);
    }

    public Function getMethodApproveRawFunction(String spender, BigInteger amount) throws
            ContractException {
        final Function function = new Function(FUNC_APPROVE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(spender), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public String getSignedTransactionForApprove(String spender, BigInteger amount) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(spender), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return createSignedTransaction(function);
    }

    public String approve(String spender, BigInteger amount, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(spender), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getApproveInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_APPROVE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(

                (String) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue()
                );
    }

    public Tuple1<Boolean> getApproveOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_APPROVE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Boolean>(

                (Boolean) results.get(0).getValue()
                );
    }

    public BigInteger balanceOf(String param0) throws ContractException {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodBalanceOfRawFunction(String param0) throws ContractException {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public BigInteger decimals() throws ContractException {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodDecimalsRawFunction() throws ContractException {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return function;
    }

    public TransactionReceipt mint(String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodMintRawFunction(String to, BigInteger amount) throws
            ContractException {
        final Function function = new Function(FUNC_MINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForMint(String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String mint(String to, BigInteger amount, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getMintInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_MINT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(

                (String) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue()
                );
    }

    public String name() throws ContractException {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodNameRawFunction() throws ContractException {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
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

    public String symbol() throws ContractException {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodSymbolRawFunction() throws ContractException {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return function;
    }

    public BigInteger totalSupply() throws ContractException {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodTotalSupplyRawFunction() throws ContractException {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public TransactionReceipt transfer(String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodTransferRawFunction(String to, BigInteger amount) throws
            ContractException {
        final Function function = new Function(FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public String getSignedTransactionForTransfer(String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String transfer(String to, BigInteger amount, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getTransferInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_TRANSFER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(

                (String) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue()
                );
    }

    public Tuple1<Boolean> getTransferOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_TRANSFER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Boolean>(

                (Boolean) results.get(0).getValue()
                );
    }

    public TransactionReceipt transferFrom(String from, String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(from), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodTransferFromRawFunction(String from, String to, BigInteger amount)
            throws ContractException {
        final Function function = new Function(FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(from), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public String getSignedTransactionForTransferFrom(String from, String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(from), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String transferFrom(String from, String to, BigInteger amount,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(from), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(to), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple3<String, String, BigInteger> getTransferFromInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, BigInteger>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue()
                );
    }

    public Tuple1<Boolean> getTransferFromOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Boolean>(

                (Boolean) results.get(0).getValue()
                );
    }

    public static AegisToken load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new AegisToken(contractAddress, client, credential);
    }

    public static AegisToken deploy(Client client, CryptoKeyPair credential) throws
            ContractException {
        return deploy(AegisToken.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }

    public static class ApprovalEventResponse {
        public TransactionReceipt.Logs log;

        public String owner;

        public String spender;

        public BigInteger amount;
    }

    public static class TokensMintedEventResponse {
        public TransactionReceipt.Logs log;

        public String to;

        public BigInteger amount;
    }

    public static class TransferEventResponse {
        public TransactionReceipt.Logs log;

        public String from;

        public String to;

        public BigInteger amount;
    }
}

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
public class AegisAssetToken extends Contract {
    public static final String[] BINARY_ARRAY = {"60806040523480156200001157600080fd5b5060405162000c0438038062000c04833981016040819052620000349162000202565b600480546001600160a01b0319163317905582516200005b9060009060208601906200008f565b508151620000719060019060208501906200008f565b506002805460ff191660ff9290921691909117905550620002c49050565b8280546200009d9062000287565b90600052602060002090601f016020900481019282620000c157600085556200010c565b82601f10620000dc57805160ff19168380011785556200010c565b828001600101855582156200010c579182015b828111156200010c578251825591602001919060010190620000ef565b506200011a9291506200011e565b5090565b5b808211156200011a57600081556001016200011f565b634e487b7160e01b600052604160045260246000fd5b600082601f8301126200015d57600080fd5b81516001600160401b03808211156200017a576200017a62000135565b604051601f8301601f19908116603f01168101908282118183101715620001a557620001a562000135565b81604052838152602092508683858801011115620001c257600080fd5b600091505b83821015620001e65785820183015181830184015290820190620001c7565b83821115620001f85760008385830101525b9695505050505050565b6000806000606084860312156200021857600080fd5b83516001600160401b03808211156200023057600080fd5b6200023e878388016200014b565b945060208601519150808211156200025557600080fd5b5062000264868287016200014b565b925050604084015160ff811681146200027c57600080fd5b809150509250925092565b600181811c908216806200029c57607f821691505b60208210811415620002be57634e487b7160e01b600052602260045260246000fd5b50919050565b61093080620002d46000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c806340c10f191161007157806340c10f191461013857806370a082311461014d5780638da5cb5b1461016d57806395d89b4114610198578063a9059cbb146101a0578063dd62ed3e146101b357600080fd5b806306fdde03146100ae578063095ea7b3146100cc57806318160ddd146100ef57806323b872dd14610106578063313ce56714610119575b600080fd5b6100b66101de565b6040516100c39190610721565b60405180910390f35b6100df6100da366004610792565b61026c565b60405190151581526020016100c3565b6100f860035481565b6040519081526020016100c3565b6100df6101143660046107bc565b610301565b6002546101269060ff1681565b60405160ff90911681526020016100c3565b61014b610146366004610792565b6103f9565b005b6100f861015b3660046107f8565b60056020526000908152604090205481565b600454610180906001600160a01b031681565b6040516001600160a01b0390911681526020016100c3565b6100b661057d565b6100df6101ae366004610792565b61058a565b6100f86101c136600461081a565b600660209081526000928352604080842090915290825290205481565b600080546101eb9061084d565b80601f01602080910402602001604051908101604052809291908181526020018280546102179061084d565b80156102645780601f1061023957610100808354040283529160200191610264565b820191906000526020600020905b81548152906001019060200180831161024757829003601f168201915b505050505081565b60006001600160a01b03831661029d5760405162461bcd60e51b815260040161029490610888565b60405180910390fd5b3360008181526006602090815260408083206001600160a01b03881680855290835292819020869055518581529192917f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925910160405180910390a350600192915050565b6001600160a01b0383166000908152600660209081526040808320338452909152812054828110156103755760405162461bcd60e51b815260206004820152601960248201527f41454749533a20414c4c4f57414e43455f4558434545444544000000000000006044820152606401610294565b60001981146103e35761038883826108cb565b6001600160a01b038616600081815260066020908152604080832033808552908352928190208590555193845290927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925910160405180910390a35b6103ee8585856105a0565b506001949350505050565b6004546001600160a01b031633146104465760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b6044820152606401610294565b6001600160a01b03821661046c5760405162461bcd60e51b815260040161029490610888565b600081116104b15760405162461bcd60e51b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b6044820152606401610294565b80600360008282546104c391906108e2565b90915550506001600160a01b038216600090815260056020526040812080548392906104f09084906108e2565b90915550506040518181526001600160a01b038316906000907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9060200160405180910390a3816001600160a01b03167f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d41213968858260405161057191815260200190565b60405180910390a25050565b600180546101eb9061084d565b60006105973384846105a0565b50600192915050565b6001600160a01b0382166105c65760405162461bcd60e51b815260040161029490610888565b6000811161060b5760405162461bcd60e51b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b6044820152606401610294565b6001600160a01b0383166000908152600560205260409020548111156106735760405162461bcd60e51b815260206004820152601b60248201527f41454749533a20494e53554646494349454e545f42414c414e434500000000006044820152606401610294565b6001600160a01b0383166000908152600560205260408120805483929061069b9084906108cb565b90915550506001600160a01b038216600090815260056020526040812080548392906106c89084906108e2565b92505081905550816001600160a01b0316836001600160a01b03167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8360405161071491815260200190565b60405180910390a3505050565b600060208083528351808285015260005b8181101561074e57858101830151858201604001528201610732565b81811115610760576000604083870101525b50601f01601f1916929092016040019392505050565b80356001600160a01b038116811461078d57600080fd5b919050565b600080604083850312156107a557600080fd5b6107ae83610776565b946020939093013593505050565b6000806000606084860312156107d157600080fd5b6107da84610776565b92506107e860208501610776565b9150604084013590509250925092565b60006020828403121561080a57600080fd5b61081382610776565b9392505050565b6000806040838503121561082d57600080fd5b61083683610776565b915061084460208401610776565b90509250929050565b600181811c9082168061086157607f821691505b6020821081141561088257634e487b7160e01b600052602260045260246000fd5b50919050565b60208082526013908201527241454749533a205a45524f5f4144445245535360681b604082015260600190565b634e487b7160e01b600052601160045260246000fd5b6000828210156108dd576108dd6108b5565b500390565b600082198211156108f5576108f56108b5565b50019056fea264697066735822122036ac776a2ac6fee8a8e9187632a26e7545ff86183fb3d719e64e24271e02283b64736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"60806040523480156200001157600080fd5b5060405162000c0838038062000c08833981016040819052620000349162000202565b600480546001600160a01b0319163317905582516200005b9060009060208601906200008f565b508151620000719060019060208501906200008f565b506002805460ff191660ff9290921691909117905550620002c49050565b8280546200009d9062000287565b90600052602060002090601f016020900481019282620000c157600085556200010c565b82601f10620000dc57805160ff19168380011785556200010c565b828001600101855582156200010c579182015b828111156200010c578251825591602001919060010190620000ef565b506200011a9291506200011e565b5090565b5b808211156200011a57600081556001016200011f565b63b95aa35560e01b600052604160045260246000fd5b600082601f8301126200015d57600080fd5b81516001600160401b03808211156200017a576200017a62000135565b604051601f8301601f19908116603f01168101908282118183101715620001a557620001a562000135565b81604052838152602092508683858801011115620001c257600080fd5b600091505b83821015620001e65785820183015181830184015290820190620001c7565b83821115620001f85760008385830101525b9695505050505050565b6000806000606084860312156200021857600080fd5b83516001600160401b03808211156200023057600080fd5b6200023e878388016200014b565b945060208601519150808211156200025557600080fd5b5062000264868287016200014b565b925050604084015160ff811681146200027c57600080fd5b809150509250925092565b600181811c908216806200029c57607f821691505b60208210811415620002be5763b95aa35560e01b600052602260045260246000fd5b50919050565b61093480620002d46000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c80635bfa2796116100715780635bfa27961461014c5780636904e96514610161578063852d921314610174578063ad8a97311461019f578063b11b6883146101b2578063cc8be70e146101ba57600080fd5b80630256e278146100ae5780631f2d4860146100ca57806346b13615146100ed5780634f5e00c71461010c5780635089e2c814610121575b600080fd5b6100b760035481565b6040519081526020015b60405180910390f35b6100dd6100d8366004610741565b6101da565b60405190151581526020016100c1565b6002546100fa9060ff1681565b60405160ff90911681526020016100c1565b61011f61011a366004610741565b610270565b005b600454610134906001600160a01b031681565b6040516001600160a01b0390911681526020016100c1565b6101546103f7565b6040516100c1919061076b565b6100dd61016f366004610741565b610485565b6100b76101823660046107c0565b600660209081526000928352604080842090915290825290205481565b6100dd6101ad3660046107f3565b61049b565b610154610594565b6100b76101c836600461082f565b60056020526000908152604090205481565b60006001600160a01b03831661020c57604051636381e58960e11b815260040161020390610851565b60405180910390fd5b3360008181526006602090815260408083206001600160a01b03881680855290835292819020869055518581529192917fd1e45707b3f71c77903b61f04c900f772db264b9bf618f1cc3308fb516eb6169910160405180910390a350600192915050565b6004546001600160a01b031633146102be57604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b6044820152606401610203565b6001600160a01b0382166102e557604051636381e58960e11b815260040161020390610851565b6000811161032b57604051636381e58960e11b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b6044820152606401610203565b806003600082825461033d9190610894565b90915550506001600160a01b0382166000908152600560205260408120805483929061036a908490610894565b90915550506040518181526001600160a01b038316906000907f18f84334255a242551aa98c68047b5da8063eab9fbeaec1eddeea280044b9ff19060200160405180910390a3816001600160a01b03167f9bd4cbc05bb513e9f6fb6158d754d0cdf43dbb7b9d69b9daf99da04eaa35d6ef826040516103eb91815260200190565b60405180910390a25050565b60018054610404906108ac565b80601f0160208091040260200160405190810160405280929190818152602001828054610430906108ac565b801561047d5780601f106104525761010080835404028352916020019161047d565b820191906000526020600020905b81548152906001019060200180831161046057829003601f168201915b505050505081565b60006104923384846105a1565b50600192915050565b6001600160a01b03831660009081526006602090815260408083203384529091528120548281101561051057604051636381e58960e11b815260206004820152601960248201527f41454749533a20414c4c4f57414e43455f4558434545444544000000000000006044820152606401610203565b600019811461057e5761052383826108e7565b6001600160a01b038616600081815260066020908152604080832033808552908352928190208590555193845290927fd1e45707b3f71c77903b61f04c900f772db264b9bf618f1cc3308fb516eb6169910160405180910390a35b6105898585856105a1565b506001949350505050565b60008054610404906108ac565b6001600160a01b0382166105c857604051636381e58960e11b815260040161020390610851565b6000811161060e57604051636381e58960e11b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b6044820152606401610203565b6001600160a01b03831660009081526005602052604090205481111561067757604051636381e58960e11b815260206004820152601b60248201527f41454749533a20494e53554646494349454e545f42414c414e434500000000006044820152606401610203565b6001600160a01b0383166000908152600560205260408120805483929061069f9084906108e7565b90915550506001600160a01b038216600090815260056020526040812080548392906106cc908490610894565b92505081905550816001600160a01b0316836001600160a01b03167f18f84334255a242551aa98c68047b5da8063eab9fbeaec1eddeea280044b9ff18360405161071891815260200190565b60405180910390a3505050565b80356001600160a01b038116811461073c57600080fd5b919050565b6000806040838503121561075457600080fd5b61075d83610725565b946020939093013593505050565b600060208083528351808285015260005b818110156107985785810183015185820160400152820161077c565b818111156107aa576000604083870101525b50601f01601f1916929092016040019392505050565b600080604083850312156107d357600080fd5b6107dc83610725565b91506107ea60208401610725565b90509250929050565b60008060006060848603121561080857600080fd5b61081184610725565b925061081f60208501610725565b9150604084013590509250925092565b60006020828403121561084157600080fd5b61084a82610725565b9392505050565b60208082526013908201527241454749533a205a45524f5f4144445245535360681b604082015260600190565b63b95aa35560e01b600052601160045260246000fd5b600082198211156108a7576108a761087e565b500190565b600181811c908216806108c057607f821691505b602082108114156108e15763b95aa35560e01b600052602260045260246000fd5b50919050565b6000828210156108f9576108f961087e565b50039056fea2646970667358221220ce70725c431970a36ad6e15aca465be0d3281f5ccc64f6ae939c2c9a8151c20264736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tokenName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"tokenSymbol\",\"type\":\"string\"},{\"internalType\":\"uint8\",\"name\":\"tokenDecimals\",\"type\":\"uint8\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"spender\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Approval\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Mint\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":3,\"slot\":6,\"value\":[0]},{\"kind\":3,\"slot\":6,\"value\":[1]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[3714247998,2234356243],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":2,\"slot\":6,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"spender\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[157198259,523061344],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":5,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[1889567281,3431720718],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[826074471,1186018837],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[3]},{\"kind\":4,\"value\":[4]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"mint\",\"outputs\":[],\"selector\":[1086394137,1331560647],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[117300739,2971363459],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[4]}],\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[2514000705,1543120790],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[404098525,39248504],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":2,\"slot\":5,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[2835717307,1761929573],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":2,\"slot\":6,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[599290589,2911541041],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

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

    public static final Event MINT_EVENT = new Event("Mint", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    protected AegisAssetToken(String contractAddress, Client client, CryptoKeyPair credential) {
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

    public List<MintEventResponse> getMintEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINT_EVENT, transactionReceipt);
        ArrayList<MintEventResponse> responses = new ArrayList<MintEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintEventResponse typedResponse = new MintEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeMintEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(MINT_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeMintEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(MINT_EVENT);
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

    public static AegisAssetToken load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new AegisAssetToken(contractAddress, client, credential);
    }

    public static AegisAssetToken deploy(Client client, CryptoKeyPair credential, String tokenName,
            String tokenSymbol, BigInteger tokenDecimals) throws ContractException {
        byte[] encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(tokenName), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(tokenSymbol), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8(tokenDecimals)));
        return deploy(AegisAssetToken.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), encodedConstructor, null);
    }

    public static class ApprovalEventResponse {
        public TransactionReceipt.Logs log;

        public String owner;

        public String spender;

        public BigInteger amount;
    }

    public static class MintEventResponse {
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

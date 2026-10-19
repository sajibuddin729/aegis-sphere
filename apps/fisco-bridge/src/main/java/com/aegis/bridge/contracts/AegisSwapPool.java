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
public class AegisSwapPool extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50604051610d8a380380610d8a83398101604081905261002f916101e5565b6001600160a01b03831661008a5760405162461bcd60e51b815260206004820152601660248201527f41454749533a20494e56414c49445f544f4b454e5f410000000000000000000060448201526064015b60405180910390fd5b6001600160a01b0382166100e05760405162461bcd60e51b815260206004820152601660248201527f41454749533a20494e56414c49445f544f4b454e5f42000000000000000000006044820152606401610081565b816001600160a01b0316836001600160a01b031614156101365760405162461bcd60e51b815260206004820152601160248201527020a2a3a4a99d1029a0a6a2afaa27a5a2a760791b6044820152606401610081565b600081116101865760405162461bcd60e51b815260206004820152601460248201527f41454749533a20494e56414c49445f50524943450000000000000000000000006044820152606401610081565b600080546001600160a01b03199081163317909155600180546001600160a01b039586169083161790556002805493909416921691909117909155600355610221565b80516001600160a01b03811681146101e057600080fd5b919050565b6000806000606084860312156101fa57600080fd5b610203846101c9565b9250610211602085016101c9565b9150604084015190509250925092565b610b5a806102306000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c80631bd6d25e116100715780631bd6d25e1461013a5780634ff1e384146101435780635c975abb146101565780635f64b55b146101735780638da5cb5b14610186578063d19798a81461019957600080fd5b8063052b9d3c146100ae5780630fc63d10146100d4578063101e3355146100ff57806316c38b3c14610114578063178b520b14610127575b600080fd5b6100c16100bc3660046109ed565b6101ac565b6040519081526020015b60405180910390f35b6001546100e7906001600160a01b031681565b6040516001600160a01b0390911681526020016100cb565b61011261010d366004610a0f565b61052d565b005b610112610122366004610a39565b610606565b6100c16101353660046109ed565b61069a565b6100c160035481565b6100c1610151366004610a0f565b6109c7565b6004546101639060ff1681565b60405190151581526020016100cb565b6002546100e7906001600160a01b031681565b6000546100e7906001600160a01b031681565b6100c16101a7366004610a0f565b6109dd565b60045460009060ff16156101fc5760405162461bcd60e51b8152602060048201526012602482015271105151d254ce881413d3d317d4105554d15160721b60448201526064015b60405180910390fd5b600083116102415760405162461bcd60e51b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b60448201526064016101f3565b61024a836109dd565b9050600081116102915760405162461bcd60e51b815260206004820152601260248201527141454749533a204f55545055545f5a45524f60701b60448201526064016101f3565b818110156102d95760405162461bcd60e51b8152602060048201526015602482015274105151d254ce8814d31254141051d157d312535255605a1b60448201526064016101f3565b6002546040516370a0823160e01b815230600482015282916001600160a01b0316906370a0823190602401602060405180830381865afa158015610321573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103459190610a5d565b10156103635760405162461bcd60e51b81526004016101f390610a76565b6001546040516323b872dd60e01b8152336004820152306024820152604481018590526001600160a01b03909116906323b872dd906064016020604051808303816000875af11580156103ba573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103de9190610ab8565b61042a5760405162461bcd60e51b815260206004820152601e60248201527f41454749533a20544f4b454e5f415f5452414e534645525f4641494c4544000060448201526064016101f3565b60025460405163a9059cbb60e01b8152336004820152602481018390526001600160a01b039091169063a9059cbb906044016020604051808303816000875af115801561047b573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061049f9190610ab8565b6104eb5760405162461bcd60e51b815260206004820152601e60248201527f41454749533a20544f4b454e5f425f5452414e534645525f4641494c4544000060448201526064016101f3565b604080518481526020810183905233917f1a442df1f5a0e29117f2a0d6ceb8d5db8b4e68203b0d5edac8e267920708522e91015b60405180910390a292915050565b6000546001600160a01b0316331461057a5760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064016101f3565b600081116105c15760405162461bcd60e51b815260206004820152601460248201527341454749533a20494e56414c49445f505249434560601b60448201526064016101f3565b600380549082905560408051828152602081018490527f945c1c4e99aa89f648fbfe3df471b916f719e16d960fcec0737d4d56bd696838910160405180910390a15050565b6000546001600160a01b031633146106535760405162461bcd60e51b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064016101f3565b6004805460ff19168215159081179091556040519081527f77860e247ab9186dbe64e5bd0e0b93273cc4273e01818420e788f500078886f59060200160405180910390a150565b60045460009060ff16156106e55760405162461bcd60e51b8152602060048201526012602482015271105151d254ce881413d3d317d4105554d15160721b60448201526064016101f3565b6000831161072a5760405162461bcd60e51b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b60448201526064016101f3565b610733836109c7565b90508181101561077d5760405162461bcd60e51b8152602060048201526015602482015274105151d254ce8814d31254141051d157d312535255605a1b60448201526064016101f3565b6001546040516370a0823160e01b815230600482015282916001600160a01b0316906370a0823190602401602060405180830381865afa1580156107c5573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906107e99190610a5d565b10156108075760405162461bcd60e51b81526004016101f390610a76565b6002546040516323b872dd60e01b8152336004820152306024820152604481018590526001600160a01b03909116906323b872dd906064016020604051808303816000875af115801561085e573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906108829190610ab8565b6108ce5760405162461bcd60e51b815260206004820152601e60248201527f41454749533a20544f4b454e5f425f5452414e534645525f4641494c4544000060448201526064016101f3565b60015460405163a9059cbb60e01b8152336004820152602481018390526001600160a01b039091169063a9059cbb906044016020604051808303816000875af115801561091f573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906109439190610ab8565b61098f5760405162461bcd60e51b815260206004820152601e60248201527f41454749533a20544f4b454e5f415f5452414e534645525f4641494c4544000060448201526064016101f3565b604080518481526020810183905233917f1201e4c6b2a8cfdbb8440a213b3b16f2e11b23315ddb1751c9522fa3f3e6b355910161051f565b6000600354826109d79190610ad5565b92915050565b6000600354826109d79190610b02565b60008060408385031215610a0057600080fd5b50508035926020909101359150565b600060208284031215610a2157600080fd5b5035919050565b8015158114610a3657600080fd5b50565b600060208284031215610a4b57600080fd5b8135610a5681610a28565b9392505050565b600060208284031215610a6f57600080fd5b5051919050565b60208082526022908201527f41454749533a20494e53554646494349454e545f504f4f4c5f4c495155494449604082015261545960f01b606082015260800190565b600060208284031215610aca57600080fd5b8151610a5681610a28565b6000816000190483118215151615610afd57634e487b7160e01b600052601160045260246000fd5b500290565b600082610b1f57634e487b7160e01b600052601260045260246000fd5b50049056fea26469706673582212202f6d9b5c8012c17649a8bb1f18376f954e9b5af2c4aa1ea3bc604839dfd935c864736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50604051610d9e380380610d9e83398101604081905261002f916101e9565b6001600160a01b03831661008b57604051636381e58960e11b815260206004820152601660248201527f41454749533a20494e56414c49445f544f4b454e5f410000000000000000000060448201526064015b60405180910390fd5b6001600160a01b0382166100e257604051636381e58960e11b815260206004820152601660248201527f41454749533a20494e56414c49445f544f4b454e5f42000000000000000000006044820152606401610082565b816001600160a01b0316836001600160a01b0316141561013957604051636381e58960e11b815260206004820152601160248201527020a2a3a4a99d1029a0a6a2afaa27a5a2a760791b6044820152606401610082565b6000811161018a57604051636381e58960e11b815260206004820152601460248201527f41454749533a20494e56414c49445f50524943450000000000000000000000006044820152606401610082565b600080546001600160a01b03199081163317909155600180546001600160a01b039586169083161790556002805493909416921691909117909155600355610225565b80516001600160a01b03811681146101e457600080fd5b919050565b6000806000606084860312156101fe57600080fd5b610207846101cd565b9250610215602085016101cd565b9150604084015190509250925092565b610b6a806102346000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c80635c30c152116100715780635c30c1521461013a5780635c981e9c1461014d5780637fb6caab14610160578063c8ed123514610173578063d4bc96011461017c578063fa4af68b1461019957600080fd5b806332df3aee146100ae57806337399bf7146100de5780633e6673a2146100f357806341d03580146101145780635089e2c814610127575b600080fd5b6001546100c1906001600160a01b031681565b6040516001600160a01b0390911681526020015b60405180910390f35b6100f16100ec366004610a0e565b6101ac565b005b610106610101366004610a32565b610246565b6040519081526020016100d5565b610106610122366004610a54565b6105c9565b6000546100c1906001600160a01b031681565b6002546100c1906001600160a01b031681565b61010661015b366004610a32565b6105df565b6100f161016e366004610a54565b610912565b61010660035481565b6004546101899060ff1681565b60405190151581526020016100d5565b6101066101a7366004610a54565b6109ed565b6000546001600160a01b031633146101ff57604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064015b60405180910390fd5b6004805460ff19168215159081179091556040519081527f2c95d1d4da94335341c6d5e662af98365eef1d79761fd3265f2356ed7ae6eeee9060200160405180910390a150565b60045460009060ff161561029257604051636381e58960e11b8152602060048201526012602482015271105151d254ce881413d3d317d4105554d15160721b60448201526064016101f6565b600083116102d857604051636381e58960e11b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b60448201526064016101f6565b6102e1836105c9565b90506000811161032957604051636381e58960e11b815260206004820152601260248201527141454749533a204f55545055545f5a45524f60701b60448201526064016101f6565b8181101561037257604051636381e58960e11b8152602060048201526015602482015274105151d254ce8814d31254141051d157d312535255605a1b60448201526064016101f6565b600254604051636645f38760e11b815230600482015282916001600160a01b03169063cc8be70e90602401602060405180830381865afa1580156103ba573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906103de9190610a6d565b10156103fd57604051636381e58960e11b81526004016101f690610a86565b60015460405163ad8a973160e01b8152336004820152306024820152604481018590526001600160a01b039091169063ad8a9731906064016020604051808303816000875af1158015610454573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906104789190610ac8565b6104c557604051636381e58960e11b815260206004820152601e60248201527f41454749533a20544f4b454e5f415f5452414e534645525f4641494c4544000060448201526064016101f6565b600254604051636904e96560e01b8152336004820152602481018390526001600160a01b0390911690636904e965906044016020604051808303816000875af1158015610516573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061053a9190610ac8565b61058757604051636381e58960e11b815260206004820152601e60248201527f41454749533a20544f4b454e5f425f5452414e534645525f4641494c4544000060448201526064016101f6565b604080518481526020810183905233917f394fb3a6e9aea4dd2c587953df00bfe3861620069d621232d41f803563d0a31e91015b60405180910390a292915050565b6000600354826105d99190610ae5565b92915050565b60045460009060ff161561062b57604051636381e58960e11b8152602060048201526012602482015271105151d254ce881413d3d317d4105554d15160721b60448201526064016101f6565b6000831161067157604051636381e58960e11b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b60448201526064016101f6565b61067a836109ed565b9050818110156106c557604051636381e58960e11b8152602060048201526015602482015274105151d254ce8814d31254141051d157d312535255605a1b60448201526064016101f6565b600154604051636645f38760e11b815230600482015282916001600160a01b03169063cc8be70e90602401602060405180830381865afa15801561070d573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906107319190610a6d565b101561075057604051636381e58960e11b81526004016101f690610a86565b60025460405163ad8a973160e01b8152336004820152306024820152604481018590526001600160a01b039091169063ad8a9731906064016020604051808303816000875af11580156107a7573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906107cb9190610ac8565b61081857604051636381e58960e11b815260206004820152601e60248201527f41454749533a20544f4b454e5f425f5452414e534645525f4641494c4544000060448201526064016101f6565b600154604051636904e96560e01b8152336004820152602481018390526001600160a01b0390911690636904e965906044016020604051808303816000875af1158015610869573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061088d9190610ac8565b6108da57604051636381e58960e11b815260206004820152601e60248201527f41454749533a20544f4b454e5f415f5452414e534645525f4641494c4544000060448201526064016101f6565b604080518481526020810183905233917f0edb42ce57253f1951d27d44ed0dd3b366a29019b73bd8673b1c07544d86ca2d91016105bb565b6000546001600160a01b0316331461096057604051636381e58960e11b815260206004820152601060248201526f20a2a3a4a99d102727aa2fa7aba722a960811b60448201526064016101f6565b600081116109a857604051636381e58960e11b815260206004820152601460248201527341454749533a20494e56414c49445f505249434560601b60448201526064016101f6565b600380549082905560408051828152602081018490527fa13eb739dd9d99998ce8cc1767cfe76609e8f6c591be0e6ea3940c6a4452aa5f910160405180910390a15050565b6000600354826105d99190610b07565b8015158114610a0b57600080fd5b50565b600060208284031215610a2057600080fd5b8135610a2b816109fd565b9392505050565b60008060408385031215610a4557600080fd5b50508035926020909101359150565b600060208284031215610a6657600080fd5b5035919050565b600060208284031215610a7f57600080fd5b5051919050565b60208082526022908201527f41454749533a20494e53554646494349454e545f504f4f4c5f4c495155494449604082015261545960f01b606082015260800190565b600060208284031215610ada57600080fd5b8151610a2b816109fd565b600082610b025763b95aa35560e01b600052601260045260246000fd5b500490565b6000816000190483118215151615610b2f5763b95aa35560e01b600052601160045260246000fd5b50029056fea26469706673582212204f785e730c5549cde369d2670b94e5ae920d3302c51d818a8313ed42598985d964736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"tokenAAddress\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"tokenBAddress\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"initialPriceBInA\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"paused\",\"type\":\"bool\"}],\"name\":\"PauseUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"previousPrice\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"newPrice\",\"type\":\"uint256\"}],\"name\":\"PriceUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"trader\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amountA\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amountB\",\"type\":\"uint256\"}],\"name\":\"SwapAToB\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"trader\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amountB\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amountA\",\"type\":\"uint256\"}],\"name\":\"SwapBToA\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"amountA\",\"type\":\"uint256\"}],\"name\":\"getQuoteAToB\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[3516373160,1104164224],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"amountB\",\"type\":\"uint256\"}],\"name\":\"getQuoteBToA\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[1341252484,4199216779],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[4]}],\"inputs\":[],\"name\":\"paused\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1553423035,3569128961],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"inputs\":[],\"name\":\"priceBInA\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[467063390,3370979893],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]},{\"kind\":4,\"value\":[4]}],\"inputs\":[{\"internalType\":\"bool\",\"name\":\"state\",\"type\":\"bool\"}],\"name\":\"setPaused\",\"outputs\":[],\"selector\":[381913916,926522359],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]},{\"kind\":4,\"value\":[3]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"newPrice\",\"type\":\"uint256\"}],\"name\":\"setPriceBInA\",\"outputs\":[],\"selector\":[270414677,2142685867],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"amountA\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"minAmountB\",\"type\":\"uint256\"}],\"name\":\"swapAToB\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"amountB\",\"type\":\"uint256\"}],\"selector\":[86744380,1046901666],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"amountB\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"minAmountA\",\"type\":\"uint256\"}],\"name\":\"swapBToA\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"amountA\",\"type\":\"uint256\"}],\"selector\":[395006475,1553473180],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"tokenA\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[264650000,853490414],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"inputs\":[],\"name\":\"tokenB\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[1600435547,1546699090],\"stateMutability\":\"view\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GETQUOTEATOB = "getQuoteAToB";

    public static final String FUNC_GETQUOTEBTOA = "getQuoteBToA";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PAUSED = "paused";

    public static final String FUNC_PRICEBINA = "priceBInA";

    public static final String FUNC_SETPAUSED = "setPaused";

    public static final String FUNC_SETPRICEBINA = "setPriceBInA";

    public static final String FUNC_SWAPATOB = "swapAToB";

    public static final String FUNC_SWAPBTOA = "swapBToA";

    public static final String FUNC_TOKENA = "tokenA";

    public static final String FUNC_TOKENB = "tokenB";

    public static final Event PAUSEUPDATED_EVENT = new Event("PauseUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event PRICEUPDATED_EVENT = new Event("PriceUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SWAPATOB_EVENT = new Event("SwapAToB", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SWAPBTOA_EVENT = new Event("SwapBToA", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    protected AegisSwapPool(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
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

    public List<PriceUpdatedEventResponse> getPriceUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PRICEUPDATED_EVENT, transactionReceipt);
        ArrayList<PriceUpdatedEventResponse> responses = new ArrayList<PriceUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PriceUpdatedEventResponse typedResponse = new PriceUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousPrice = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newPrice = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribePriceUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(PRICEUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribePriceUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(PRICEUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<SwapAToBEventResponse> getSwapAToBEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SWAPATOB_EVENT, transactionReceipt);
        ArrayList<SwapAToBEventResponse> responses = new ArrayList<SwapAToBEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SwapAToBEventResponse typedResponse = new SwapAToBEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.trader = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amountA = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amountB = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeSwapAToBEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(SWAPATOB_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeSwapAToBEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(SWAPATOB_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<SwapBToAEventResponse> getSwapBToAEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SWAPBTOA_EVENT, transactionReceipt);
        ArrayList<SwapBToAEventResponse> responses = new ArrayList<SwapBToAEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SwapBToAEventResponse typedResponse = new SwapBToAEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.trader = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amountB = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amountA = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeSwapBToAEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(SWAPBTOA_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeSwapBToAEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(SWAPBTOA_EVENT);
        subscribeEvent(topic0,callback);
    }

    public BigInteger getQuoteAToB(BigInteger amountA) throws ContractException {
        final Function function = new Function(FUNC_GETQUOTEATOB, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountA)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodGetQuoteAToBRawFunction(BigInteger amountA) throws ContractException {
        final Function function = new Function(FUNC_GETQUOTEATOB, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountA)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public BigInteger getQuoteBToA(BigInteger amountB) throws ContractException {
        final Function function = new Function(FUNC_GETQUOTEBTOA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountB)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodGetQuoteBToARawFunction(BigInteger amountB) throws ContractException {
        final Function function = new Function(FUNC_GETQUOTEBTOA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountB)), 
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

    public BigInteger priceBInA() throws ContractException {
        final Function function = new Function(FUNC_PRICEBINA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodPriceBInARawFunction() throws ContractException {
        final Function function = new Function(FUNC_PRICEBINA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
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

    public TransactionReceipt setPriceBInA(BigInteger newPrice) {
        final Function function = new Function(
                FUNC_SETPRICEBINA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newPrice)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetPriceBInARawFunction(BigInteger newPrice) throws ContractException {
        final Function function = new Function(FUNC_SETPRICEBINA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newPrice)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetPriceBInA(BigInteger newPrice) {
        final Function function = new Function(
                FUNC_SETPRICEBINA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newPrice)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setPriceBInA(BigInteger newPrice, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETPRICEBINA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newPrice)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<BigInteger> getSetPriceBInAInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETPRICEBINA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>(

                (BigInteger) results.get(0).getValue()
                );
    }

    public TransactionReceipt swapAToB(BigInteger amountA, BigInteger minAmountB) {
        final Function function = new Function(
                FUNC_SWAPATOB, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountA), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(minAmountB)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSwapAToBRawFunction(BigInteger amountA, BigInteger minAmountB) throws
            ContractException {
        final Function function = new Function(FUNC_SWAPATOB, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountA), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(minAmountB)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public String getSignedTransactionForSwapAToB(BigInteger amountA, BigInteger minAmountB) {
        final Function function = new Function(
                FUNC_SWAPATOB, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountA), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(minAmountB)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String swapAToB(BigInteger amountA, BigInteger minAmountB,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SWAPATOB, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountA), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(minAmountB)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<BigInteger, BigInteger> getSwapAToBInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SWAPATOB, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue()
                );
    }

    public Tuple1<BigInteger> getSwapAToBOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_SWAPATOB, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>(

                (BigInteger) results.get(0).getValue()
                );
    }

    public TransactionReceipt swapBToA(BigInteger amountB, BigInteger minAmountA) {
        final Function function = new Function(
                FUNC_SWAPBTOA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountB), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(minAmountA)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSwapBToARawFunction(BigInteger amountB, BigInteger minAmountA) throws
            ContractException {
        final Function function = new Function(FUNC_SWAPBTOA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountB), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(minAmountA)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public String getSignedTransactionForSwapBToA(BigInteger amountB, BigInteger minAmountA) {
        final Function function = new Function(
                FUNC_SWAPBTOA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountB), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(minAmountA)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String swapBToA(BigInteger amountB, BigInteger minAmountA,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SWAPBTOA, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amountB), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(minAmountA)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<BigInteger, BigInteger> getSwapBToAInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SWAPBTOA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue()
                );
    }

    public Tuple1<BigInteger> getSwapBToAOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_SWAPBTOA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>(

                (BigInteger) results.get(0).getValue()
                );
    }

    public String tokenA() throws ContractException {
        final Function function = new Function(FUNC_TOKENA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodTokenARawFunction() throws ContractException {
        final Function function = new Function(FUNC_TOKENA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return function;
    }

    public String tokenB() throws ContractException {
        final Function function = new Function(FUNC_TOKENB, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodTokenBRawFunction() throws ContractException {
        final Function function = new Function(FUNC_TOKENB, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return function;
    }

    public static AegisSwapPool load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new AegisSwapPool(contractAddress, client, credential);
    }

    public static AegisSwapPool deploy(Client client, CryptoKeyPair credential,
            String tokenAAddress, String tokenBAddress, BigInteger initialPriceBInA) throws
            ContractException {
        byte[] encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(tokenAAddress), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(tokenBAddress), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(initialPriceBInA)));
        return deploy(AegisSwapPool.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), encodedConstructor, null);
    }

    public static class PauseUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public Boolean paused;
    }

    public static class PriceUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger previousPrice;

        public BigInteger newPrice;
    }

    public static class SwapAToBEventResponse {
        public TransactionReceipt.Logs log;

        public String trader;

        public BigInteger amountA;

        public BigInteger amountB;
    }

    public static class SwapBToAEventResponse {
        public TransactionReceipt.Logs log;

        public String trader;

        public BigInteger amountB;

        public BigInteger amountA;
    }
}

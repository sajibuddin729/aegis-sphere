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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
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
public class RiskCircuitBreaker extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50604051610e0d380380610e0d83398101604081905261002f91610149565b600083116100845760405162461bcd60e51b815260206004820152601560248201527f41454749533a20494e56414c49445f4d41585f5458000000000000000000000060448201526064015b60405180910390fd5b828210156100d45760405162461bcd60e51b815260206004820152601a60248201527f41454749533a20494e56414c49445f4441494c595f4c494d4954000000000000604482015260640161007b565b6127108111156101265760405162461bcd60e51b815260206004820152601360248201527f41454749533a20494e56414c49445f5249534b00000000000000000000000000604482015260640161007b565b600080546001600160a01b03191633179055600192909255600255600355610177565b60008060006060848603121561015e57600080fd5b8351925060208401519150604084015190509250925092565b610c87806101866000396000f3fe608060405234801561001057600080fd5b50600436106101005760003560e01c80638aaa228411610097578063c8836d3c11610066578063c8836d3c14610255578063c8c8ebe414610268578063cb58e24a14610271578063e744092e1461027a57600080fd5b80638aaa2284146101f15780638da5cb5b146102045780638e44cbd41461022f578063b70e5d6b1461024257600080fd5b80632213030b116100d35780632213030b1461018c57806336a0f8a5146101b757806349ff658e146101ca5780635c975abb146101dd57600080fd5b806316c38b3c146101055780631ce3a1091461011a5780631fa3894614610136578063205b10a014610169575b600080fd5b610118610113366004610a93565b61029d565b005b61012360025481565b6040519081526020015b60405180910390f35b610159610144366004610acc565b60046020526000908152604090205460ff1681565b604051901515815260200161012d565b610159610177366004610acc565b60066020526000908152604090205460ff1681565b61012361019a366004610ae7565b600760209081526000928352604080842090915290825290205481565b6101186101c5366004610b11565b610328565b6101236101d8366004610acc565b610487565b60005461015990600160a01b900460ff1681565b6101186101ff366004610b3d565b6104c4565b600054610217906001600160a01b031681565b6040516001600160a01b03909116815260200161012d565b61011861023d366004610b3d565b610574565b610123610250366004610b70565b61061c565b610118610263366004610b3d565b6109d6565b61012360015481565b61012360035481565b610159610288366004610acc565b60056020526000908152604090205460ff1681565b6000546001600160a01b031633146102d05760405162461bcd60e51b81526004016102c790610bb2565b60405180910390fd5b60008054821515600160a01b0260ff60a01b199091161790556040517fb31006682779d0ac02864bee834675baf4592a679bfe75edd5e5847b52ef6f6e9061031d90831515815260200190565b60405180910390a150565b6000546001600160a01b031633146103525760405162461bcd60e51b81526004016102c790610bb2565b6000831161039a5760405162461bcd60e51b81526020600482015260156024820152740828a8e92a67440929cac82989288be9a82b0bea8b605b1b60448201526064016102c7565b828210156103ea5760405162461bcd60e51b815260206004820152601a60248201527f41454749533a20494e56414c49445f4441494c595f4c494d495400000000000060448201526064016102c7565b6127108111156104325760405162461bcd60e51b815260206004820152601360248201527241454749533a20494e56414c49445f5249534b60681b60448201526064016102c7565b60018390556002829055600381905560408051848152602081018490529081018290527f7f5d8298097e91154a8a02ed917c385a1678affa8abb264f3e3dc1159bc8e5e89060600160405180910390a1505050565b6000806104976201518042610bdc565b6001600160a01b039093166000908152600760209081526040808320958352949052929092205492915050565b6000546001600160a01b031633146104ee5760405162461bcd60e51b81526004016102c790610bb2565b6001600160a01b0382166105145760405162461bcd60e51b81526004016102c790610bfe565b6001600160a01b038216600081815260056020908152604091829020805460ff191685151590811790915591519182527f3a7d4b919c66e25bb96b646b1fa5df47aca5411773bfe1829fc8e86ab7006e6a91015b60405180910390a25050565b6000546001600160a01b0316331461059e5760405162461bcd60e51b81526004016102c790610bb2565b6001600160a01b0382166105c45760405162461bcd60e51b81526004016102c790610bfe565b6001600160a01b038216600081815260066020908152604091829020805460ff191685151590811790915591519182527fcb7e4e993b6c4764b7fce1094e4a1bf94a0af061a98cb1a9d02566049eff65e79101610568565b3360009081526004602052604081205460ff1661067b5760405162461bcd60e51b815260206004820152601c60248201527f41454749533a2057414c4c45545f4e4f545f415554484f52495a45440000000060448201526064016102c7565b600054600160a01b900460ff16156106cc5760405162461bcd60e51b8152602060048201526014602482015273105151d254ce8814d654d5115357d4105554d15160621b60448201526064016102c7565b6001600160a01b03851660009081526005602052604090205460ff166107345760405162461bcd60e51b815260206004820152601860248201527f41454749533a20544f4b454e5f4e4f545f414c4c4f574544000000000000000060448201526064016102c7565b6001600160a01b03841661078a5760405162461bcd60e51b815260206004820152601860248201527f41454749533a20494e56414c49445f524543495049454e54000000000000000060448201526064016102c7565b6001600160a01b03841660009081526006602052604090205460ff16156107f35760405162461bcd60e51b815260206004820152601860248201527f41454749533a20524543495049454e545f424c4f434b4544000000000000000060448201526064016102c7565b600083116108385760405162461bcd60e51b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b60448201526064016102c7565b60015483111561088a5760405162461bcd60e51b815260206004820152601860248201527f41454749533a2054585f4c494d49545f4558434545444544000000000000000060448201526064016102c7565b6003548211156108d35760405162461bcd60e51b81526020600482015260146024820152730828a8e92a67440a492a696bea89e9ebe90928e960631b60448201526064016102c7565b60006108e26201518042610bdc565b3360009081526007602090815260408083208484529091528120549192509061090c908690610c2b565b90506002548111156109605760405162461bcd60e51b815260206004820152601b60248201527f41454749533a204441494c595f4c494d49545f4558434545444544000000000060448201526064016102c7565b33600081815260076020908152604080832086845282529182902084905581518881529081018790529081018390526001600160a01b0388811692908a16917f3c4b93429355e924eff9e3bbd5fc36435e23c06711258be4b08f7d51f9221e609060600160405180910390a49695505050505050565b6000546001600160a01b03163314610a005760405162461bcd60e51b81526004016102c790610bb2565b6001600160a01b038216610a265760405162461bcd60e51b81526004016102c790610bfe565b6001600160a01b038216600081815260046020908152604091829020805460ff191685151590811790915591519182527f0d9344ef43897b385229621e02645d25ef9cdcc662414aff5a197d47022734ee9101610568565b80358015158114610a8e57600080fd5b919050565b600060208284031215610aa557600080fd5b610aae82610a7e565b9392505050565b80356001600160a01b0381168114610a8e57600080fd5b600060208284031215610ade57600080fd5b610aae82610ab5565b60008060408385031215610afa57600080fd5b610b0383610ab5565b946020939093013593505050565b600080600060608486031215610b2657600080fd5b505081359360208301359350604090920135919050565b60008060408385031215610b5057600080fd5b610b5983610ab5565b9150610b6760208401610a7e565b90509250929050565b60008060008060808587031215610b8657600080fd5b610b8f85610ab5565b9350610b9d60208601610ab5565b93969395505050506040820135916060013590565b60208082526010908201526f20a2a3a4a99d102727aa2fa7aba722a960811b604082015260600190565b600082610bf957634e487b7160e01b600052601260045260246000fd5b500490565b60208082526013908201527241454749533a205a45524f5f4144445245535360681b604082015260600190565b60008219821115610c4c57634e487b7160e01b600052601160045260246000fd5b50019056fea264697066735822122097ac4ae83ddf647162e7e0d8b6fafe3c0480ba5d1a1910446bb0a6d930fc43df64736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50604051610e24380380610e2483398101604081905261002f9161014c565b6000831161008557604051636381e58960e11b815260206004820152601560248201527f41454749533a20494e56414c49445f4d41585f5458000000000000000000000060448201526064015b60405180910390fd5b828210156100d657604051636381e58960e11b815260206004820152601a60248201527f41454749533a20494e56414c49445f4441494c595f4c494d4954000000000000604482015260640161007c565b61271081111561012957604051636381e58960e11b815260206004820152601360248201527f41454749533a20494e56414c49445f5249534b00000000000000000000000000604482015260640161007c565b600080546001600160a01b0319163317905560019290925560025560035561017a565b60008060006060848603121561016157600080fd5b8351925060208401519150604084015190509250925092565b610c9b806101896000396000f3fe608060405234801561001057600080fd5b50600436106101005760003560e01c806393a7c48a11610097578063cb3a650711610066578063cb3a650714610228578063d4bc96011461023b578063ee131d661461024f578063f7325f591461027257600080fd5b806393a7c48a146101ac578063a0ff2cf0146101df578063a282c8ec146101f2578063c3ab82f61461021557600080fd5b806325eab4f9116100d357806325eab4f91461015c57806337399bf7146101655780635089e2c8146101785780638c387644146101a357600080fd5b806302d1a69d146101055780630ad6d2431461012157806310d93d951461013657806321a9543014610149575b600080fd5b61010e60015481565b6040519081526020015b60405180910390f35b61013461012f366004610abe565b61029d565b005b610134610144366004610af1565b610358565b61010e610157366004610b1d565b6104bb565b61010e60035481565b610134610173366004610b5f565b61087e565b60005461018b906001600160a01b031681565b6040516001600160a01b039091168152602001610118565b61010e60025481565b6101cf6101ba366004610b81565b60046020526000908152604090205460ff1681565b6040519015158152602001610118565b6101346101ed366004610abe565b610901565b6101cf610200366004610b81565b60056020526000908152604090205460ff1681565b610134610223366004610abe565b6109ab565b61010e610236366004610b81565b610a55565b6000546101cf90600160a01b900460ff1681565b6101cf61025d366004610b81565b60066020526000908152604090205460ff1681565b61010e610280366004610b9c565b600760209081526000928352604080842090915290825290205481565b6000546001600160a01b031633146102d157604051636381e58960e11b81526004016102c890610bc6565b60405180910390fd5b6001600160a01b0382166102f857604051636381e58960e11b81526004016102c890610bf0565b6001600160a01b038216600081815260056020908152604091829020805460ff191685151590811790915591519182527fdc5bc5bb425ba2f1a110445644c3bdc33b24685396aa119ee2e0ee21e97ed95e91015b60405180910390a25050565b6000546001600160a01b0316331461038357604051636381e58960e11b81526004016102c890610bc6565b600083116103cc57604051636381e58960e11b81526020600482015260156024820152740828a8e92a67440929cac82989288be9a82b0bea8b605b1b60448201526064016102c8565b8282101561041d57604051636381e58960e11b815260206004820152601a60248201527f41454749533a20494e56414c49445f4441494c595f4c494d495400000000000060448201526064016102c8565b61271081111561046657604051636381e58960e11b815260206004820152601360248201527241454749533a20494e56414c49445f5249534b60681b60448201526064016102c8565b60018390556002829055600381905560408051848152602081018490529081018290527fe31c708861d1bff2505bce246e82f5e807d5e25033e8edae7dab06c16d179a659060600160405180910390a1505050565b3360009081526004602052604081205460ff1661051b57604051636381e58960e11b815260206004820152601c60248201527f41454749533a2057414c4c45545f4e4f545f415554484f52495a45440000000060448201526064016102c8565b600054600160a01b900460ff161561056d57604051636381e58960e11b8152602060048201526014602482015273105151d254ce8814d654d5115357d4105554d15160621b60448201526064016102c8565b6001600160a01b03851660009081526005602052604090205460ff166105d657604051636381e58960e11b815260206004820152601860248201527f41454749533a20544f4b454e5f4e4f545f414c4c4f574544000000000000000060448201526064016102c8565b6001600160a01b03841661062d57604051636381e58960e11b815260206004820152601860248201527f41454749533a20494e56414c49445f524543495049454e54000000000000000060448201526064016102c8565b6001600160a01b03841660009081526006602052604090205460ff161561069757604051636381e58960e11b815260206004820152601860248201527f41454749533a20524543495049454e545f424c4f434b4544000000000000000060448201526064016102c8565b600083116106dd57604051636381e58960e11b8152602060048201526012602482015271105151d254ce8816915493d7d05353d5539560721b60448201526064016102c8565b60015483111561073057604051636381e58960e11b815260206004820152601860248201527f41454749533a2054585f4c494d49545f4558434545444544000000000000000060448201526064016102c8565b60035482111561077a57604051636381e58960e11b81526020600482015260146024820152730828a8e92a67440a492a696bea89e9ebe90928e960631b60448201526064016102c8565b60006107896201518042610c1d565b336000908152600760209081526040808320848452909152812054919250906107b3908690610c3f565b905060025481111561080857604051636381e58960e11b815260206004820152601b60248201527f41454749533a204441494c595f4c494d49545f4558434545444544000000000060448201526064016102c8565b33600081815260076020908152604080832086845282529182902084905581518881529081018790529081018390526001600160a01b0388811692908a16917fe6a7ba5fe29cf21e1a2b8bfabd33661d1a6947f081eba3d296f8c7aa561c0f849060600160405180910390a49695505050505050565b6000546001600160a01b031633146108a957604051636381e58960e11b81526004016102c890610bc6565b60008054821515600160a01b0260ff60a01b199091161790556040517fc3121804c4b98af60a4e3bba8f070ca0c0887ecb4e2734c4eda7246d1d2524b1906108f690831515815260200190565b60405180910390a150565b6000546001600160a01b0316331461092c57604051636381e58960e11b81526004016102c890610bc6565b6001600160a01b03821661095357604051636381e58960e11b81526004016102c890610bf0565b6001600160a01b038216600081815260066020908152604091829020805460ff191685151590811790915591519182527f5e49f684f71e38beae7c7c271ef1d502d70a54723a553f0e0d1e2f00a0257bf0910161034c565b6000546001600160a01b031633146109d657604051636381e58960e11b81526004016102c890610bc6565b6001600160a01b0382166109fd57604051636381e58960e11b81526004016102c890610bf0565b6001600160a01b038216600081815260046020908152604091829020805460ff191685151590811790915591519182527fc65aec4906e5484543443076ae3756a73365c93e331431edbc3be186b7806e2e910161034c565b600080610a656201518042610c1d565b6001600160a01b039093166000908152600760209081526040808320958352949052929092205492915050565b80356001600160a01b0381168114610aa957600080fd5b919050565b80358015158114610aa957600080fd5b60008060408385031215610ad157600080fd5b610ada83610a92565b9150610ae860208401610aae565b90509250929050565b600080600060608486031215610b0657600080fd5b505081359360208301359350604090920135919050565b60008060008060808587031215610b3357600080fd5b610b3c85610a92565b9350610b4a60208601610a92565b93969395505050506040820135916060013590565b600060208284031215610b7157600080fd5b610b7a82610aae565b9392505050565b600060208284031215610b9357600080fd5b610b7a82610a92565b60008060408385031215610baf57600080fd5b610bb883610a92565b946020939093013593505050565b60208082526010908201526f20a2a3a4a99d102727aa2fa7aba722a960811b604082015260600190565b60208082526013908201527241454749533a205a45524f5f4144445245535360681b604082015260600190565b600082610c3a5763b95aa35560e01b600052601260045260246000fd5b500490565b60008219821115610c605763b95aa35560e01b600052601160045260246000fd5b50019056fea2646970667358221220bd7dff304770fc27118e370aed1faf0bc0211ea828dc74e3940f5b523f9ca2c064736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"initialMaxTransactionAmount\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"initialMaxDailyAmount\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"initialMaxRiskScoreBps\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"allowed\",\"type\":\"bool\"}],\"name\":\"AllowedTokenUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"wallet\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"AuthorizedWalletUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"paused\",\"type\":\"bool\"}],\"name\":\"PauseStateUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"recipient\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"blocked\",\"type\":\"bool\"}],\"name\":\"RecipientBlockUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"maxTransactionAmount\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"maxDailyAmount\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"maxRiskScoreBps\",\"type\":\"uint256\"}],\"name\":\"RiskLimitsUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"wallet\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"recipient\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"riskScoreBps\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"spentToday\",\"type\":\"uint256\"}],\"name\":\"TransactionRiskApproved\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":3,\"slot\":5,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"allowedTokens\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3879995694,2726480108],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"authorizedWallets\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[530811206,2477245578],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":6,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"blockedRecipients\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[542838944,3994230118],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"wallet\",\"type\":\"address\"}],\"name\":\"getSpentToday\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[1241474446,3409601799],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"inputs\":[],\"name\":\"maxDailyAmount\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[484679945,2352510532],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"inputs\":[],\"name\":\"maxRiskScoreBps\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[3411599946,636138745],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"maxTransactionAmount\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[3368610788,47294109],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2376452955,1351213768],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"paused\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1553423035,3569128961],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"allowed\",\"type\":\"bool\"}],\"name\":\"setAllowedToken\",\"outputs\":[],\"selector\":[2326405764,181850691],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"wallet\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"authorized\",\"type\":\"bool\"}],\"name\":\"setAuthorizedWallet\",\"outputs\":[],\"selector\":[3364056380,3282797302],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"recipient\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"blocked\",\"type\":\"bool\"}],\"name\":\"setBlockedRecipient\",\"outputs\":[],\"selector\":[2386873300,2701077744],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"bool\",\"name\":\"pauseState\",\"type\":\"bool\"}],\"name\":\"setPaused\",\"outputs\":[],\"selector\":[381913916,926522359],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":7,\"value\":[0]},{\"kind\":3,\"slot\":7,\"value\":[1]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"spentByWalletDay\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[571671307,4147273561],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]},{\"kind\":4,\"value\":[1]},{\"kind\":4,\"value\":[2]},{\"kind\":4,\"value\":[3]}],\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"newMaxTransactionAmount\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"newMaxDailyAmount\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"newMaxRiskScoreBps\",\"type\":\"uint256\"}],\"name\":\"updateLimits\",\"outputs\":[],\"selector\":[916519077,282672533],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":2,\"slot\":4,\"value\":[0]},{\"kind\":2,\"slot\":7,\"value\":[0]},{\"kind\":4,\"value\":[0]},{\"kind\":4,\"value\":[1]},{\"kind\":4,\"value\":[2]},{\"kind\":4,\"value\":[3]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"token\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"recipient\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"riskScoreBps\",\"type\":\"uint256\"}],\"name\":\"validateAndConsume\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[3071171947,564745264],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_ALLOWEDTOKENS = "allowedTokens";

    public static final String FUNC_AUTHORIZEDWALLETS = "authorizedWallets";

    public static final String FUNC_BLOCKEDRECIPIENTS = "blockedRecipients";

    public static final String FUNC_GETSPENTTODAY = "getSpentToday";

    public static final String FUNC_MAXDAILYAMOUNT = "maxDailyAmount";

    public static final String FUNC_MAXRISKSCOREBPS = "maxRiskScoreBps";

    public static final String FUNC_MAXTRANSACTIONAMOUNT = "maxTransactionAmount";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PAUSED = "paused";

    public static final String FUNC_SETALLOWEDTOKEN = "setAllowedToken";

    public static final String FUNC_SETAUTHORIZEDWALLET = "setAuthorizedWallet";

    public static final String FUNC_SETBLOCKEDRECIPIENT = "setBlockedRecipient";

    public static final String FUNC_SETPAUSED = "setPaused";

    public static final String FUNC_SPENTBYWALLETDAY = "spentByWalletDay";

    public static final String FUNC_UPDATELIMITS = "updateLimits";

    public static final String FUNC_VALIDATEANDCONSUME = "validateAndConsume";

    public static final Event ALLOWEDTOKENUPDATED_EVENT = new Event("AllowedTokenUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event AUTHORIZEDWALLETUPDATED_EVENT = new Event("AuthorizedWalletUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event PAUSESTATEUPDATED_EVENT = new Event("PauseStateUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event RECIPIENTBLOCKUPDATED_EVENT = new Event("RecipientBlockUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event RISKLIMITSUPDATED_EVENT = new Event("RiskLimitsUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSACTIONRISKAPPROVED_EVENT = new Event("TransactionRiskApproved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    protected RiskCircuitBreaker(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<AllowedTokenUpdatedEventResponse> getAllowedTokenUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ALLOWEDTOKENUPDATED_EVENT, transactionReceipt);
        ArrayList<AllowedTokenUpdatedEventResponse> responses = new ArrayList<AllowedTokenUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AllowedTokenUpdatedEventResponse typedResponse = new AllowedTokenUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.token = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.allowed = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeAllowedTokenUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(ALLOWEDTOKENUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeAllowedTokenUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(ALLOWEDTOKENUPDATED_EVENT);
        subscribeEvent(topic0,callback);
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

    public List<PauseStateUpdatedEventResponse> getPauseStateUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PAUSESTATEUPDATED_EVENT, transactionReceipt);
        ArrayList<PauseStateUpdatedEventResponse> responses = new ArrayList<PauseStateUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PauseStateUpdatedEventResponse typedResponse = new PauseStateUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.paused = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribePauseStateUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(PAUSESTATEUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribePauseStateUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(PAUSESTATEUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<RecipientBlockUpdatedEventResponse> getRecipientBlockUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RECIPIENTBLOCKUPDATED_EVENT, transactionReceipt);
        ArrayList<RecipientBlockUpdatedEventResponse> responses = new ArrayList<RecipientBlockUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RecipientBlockUpdatedEventResponse typedResponse = new RecipientBlockUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.recipient = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.blocked = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeRecipientBlockUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(RECIPIENTBLOCKUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeRecipientBlockUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(RECIPIENTBLOCKUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<RiskLimitsUpdatedEventResponse> getRiskLimitsUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RISKLIMITSUPDATED_EVENT, transactionReceipt);
        ArrayList<RiskLimitsUpdatedEventResponse> responses = new ArrayList<RiskLimitsUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RiskLimitsUpdatedEventResponse typedResponse = new RiskLimitsUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.maxTransactionAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.maxDailyAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.maxRiskScoreBps = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeRiskLimitsUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(RISKLIMITSUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeRiskLimitsUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(RISKLIMITSUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<TransactionRiskApprovedEventResponse> getTransactionRiskApprovedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSACTIONRISKAPPROVED_EVENT, transactionReceipt);
        ArrayList<TransactionRiskApprovedEventResponse> responses = new ArrayList<TransactionRiskApprovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransactionRiskApprovedEventResponse typedResponse = new TransactionRiskApprovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.wallet = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.token = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.recipient = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.riskScoreBps = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.spentToday = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeTransactionRiskApprovedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(TRANSACTIONRISKAPPROVED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeTransactionRiskApprovedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(TRANSACTIONRISKAPPROVED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public Boolean allowedTokens(String param0) throws ContractException {
        final Function function = new Function(FUNC_ALLOWEDTOKENS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodAllowedTokensRawFunction(String param0) throws ContractException {
        final Function function = new Function(FUNC_ALLOWEDTOKENS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
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

    public Boolean blockedRecipients(String param0) throws ContractException {
        final Function function = new Function(FUNC_BLOCKEDRECIPIENTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodBlockedRecipientsRawFunction(String param0) throws ContractException {
        final Function function = new Function(FUNC_BLOCKEDRECIPIENTS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public BigInteger getSpentToday(String wallet) throws ContractException {
        final Function function = new Function(FUNC_GETSPENTTODAY, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(wallet)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodGetSpentTodayRawFunction(String wallet) throws ContractException {
        final Function function = new Function(FUNC_GETSPENTTODAY, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(wallet)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public BigInteger maxDailyAmount() throws ContractException {
        final Function function = new Function(FUNC_MAXDAILYAMOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodMaxDailyAmountRawFunction() throws ContractException {
        final Function function = new Function(FUNC_MAXDAILYAMOUNT, 
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

    public BigInteger maxTransactionAmount() throws ContractException {
        final Function function = new Function(FUNC_MAXTRANSACTIONAMOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodMaxTransactionAmountRawFunction() throws ContractException {
        final Function function = new Function(FUNC_MAXTRANSACTIONAMOUNT, 
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

    public TransactionReceipt setAllowedToken(String token, Boolean allowed) {
        final Function function = new Function(
                FUNC_SETALLOWEDTOKEN, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(allowed)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetAllowedTokenRawFunction(String token, Boolean allowed) throws
            ContractException {
        final Function function = new Function(FUNC_SETALLOWEDTOKEN, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(allowed)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetAllowedToken(String token, Boolean allowed) {
        final Function function = new Function(
                FUNC_SETALLOWEDTOKEN, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(allowed)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setAllowedToken(String token, Boolean allowed, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETALLOWEDTOKEN, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(allowed)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, Boolean> getSetAllowedTokenInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETALLOWEDTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Boolean>(

                (String) results.get(0).getValue(), 
                (Boolean) results.get(1).getValue()
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

    public TransactionReceipt setBlockedRecipient(String recipient, Boolean blocked) {
        final Function function = new Function(
                FUNC_SETBLOCKEDRECIPIENT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(blocked)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetBlockedRecipientRawFunction(String recipient, Boolean blocked)
            throws ContractException {
        final Function function = new Function(FUNC_SETBLOCKEDRECIPIENT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(blocked)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetBlockedRecipient(String recipient, Boolean blocked) {
        final Function function = new Function(
                FUNC_SETBLOCKEDRECIPIENT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(blocked)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setBlockedRecipient(String recipient, Boolean blocked,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETBLOCKEDRECIPIENT, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(blocked)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, Boolean> getSetBlockedRecipientInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SETBLOCKEDRECIPIENT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Boolean>(

                (String) results.get(0).getValue(), 
                (Boolean) results.get(1).getValue()
                );
    }

    public TransactionReceipt setPaused(Boolean pauseState) {
        final Function function = new Function(
                FUNC_SETPAUSED, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(pauseState)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetPausedRawFunction(Boolean pauseState) throws ContractException {
        final Function function = new Function(FUNC_SETPAUSED, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(pauseState)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSetPaused(Boolean pauseState) {
        final Function function = new Function(
                FUNC_SETPAUSED, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(pauseState)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String setPaused(Boolean pauseState, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SETPAUSED, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Bool(pauseState)), 
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

    public BigInteger spentByWalletDay(String param0, BigInteger param1) throws ContractException {
        final Function function = new Function(FUNC_SPENTBYWALLETDAY, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodSpentByWalletDayRawFunction(String param0, BigInteger param1) throws
            ContractException {
        final Function function = new Function(FUNC_SPENTBYWALLETDAY, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(param0), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public TransactionReceipt updateLimits(BigInteger newMaxTransactionAmount,
            BigInteger newMaxDailyAmount, BigInteger newMaxRiskScoreBps) {
        final Function function = new Function(
                FUNC_UPDATELIMITS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxTransactionAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxDailyAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxRiskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodUpdateLimitsRawFunction(BigInteger newMaxTransactionAmount,
            BigInteger newMaxDailyAmount, BigInteger newMaxRiskScoreBps) throws ContractException {
        final Function function = new Function(FUNC_UPDATELIMITS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxTransactionAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxDailyAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxRiskScoreBps)), 
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForUpdateLimits(BigInteger newMaxTransactionAmount,
            BigInteger newMaxDailyAmount, BigInteger newMaxRiskScoreBps) {
        final Function function = new Function(
                FUNC_UPDATELIMITS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxTransactionAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxDailyAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxRiskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String updateLimits(BigInteger newMaxTransactionAmount, BigInteger newMaxDailyAmount,
            BigInteger newMaxRiskScoreBps, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_UPDATELIMITS, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxTransactionAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxDailyAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(newMaxRiskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple3<BigInteger, BigInteger, BigInteger> getUpdateLimitsInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_UPDATELIMITS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue()
                );
    }

    public TransactionReceipt validateAndConsume(String token, String recipient, BigInteger amount,
            BigInteger riskScoreBps) {
        final Function function = new Function(
                FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodValidateAndConsumeRawFunction(String token, String recipient,
            BigInteger amount, BigInteger riskScoreBps) throws ContractException {
        final Function function = new Function(FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public String getSignedTransactionForValidateAndConsume(String token, String recipient,
            BigInteger amount, BigInteger riskScoreBps) {
        final Function function = new Function(
                FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String validateAndConsume(String token, String recipient, BigInteger amount,
            BigInteger riskScoreBps, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.Address(token), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.Address(recipient), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(amount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(riskScoreBps)), 
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple4<String, String, BigInteger, BigInteger> getValidateAndConsumeInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_VALIDATEANDCONSUME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, String, BigInteger, BigInteger>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue(), 
                (BigInteger) results.get(3).getValue()
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

    public static RiskCircuitBreaker load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new RiskCircuitBreaker(contractAddress, client, credential);
    }

    public static RiskCircuitBreaker deploy(Client client, CryptoKeyPair credential,
            BigInteger initialMaxTransactionAmount, BigInteger initialMaxDailyAmount,
            BigInteger initialMaxRiskScoreBps) throws ContractException {
        byte[] encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(initialMaxTransactionAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(initialMaxDailyAmount), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(initialMaxRiskScoreBps)));
        return deploy(RiskCircuitBreaker.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), encodedConstructor, null);
    }

    public static class AllowedTokenUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public String token;

        public Boolean allowed;
    }

    public static class AuthorizedWalletUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public String wallet;

        public Boolean authorized;
    }

    public static class PauseStateUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public Boolean paused;
    }

    public static class RecipientBlockUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public String recipient;

        public Boolean blocked;
    }

    public static class RiskLimitsUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger maxTransactionAmount;

        public BigInteger maxDailyAmount;

        public BigInteger maxRiskScoreBps;
    }

    public static class TransactionRiskApprovedEventResponse {
        public TransactionReceipt.Logs log;

        public String wallet;

        public String token;

        public String recipient;

        public BigInteger amount;

        public BigInteger riskScoreBps;

        public BigInteger spentToday;
    }
}

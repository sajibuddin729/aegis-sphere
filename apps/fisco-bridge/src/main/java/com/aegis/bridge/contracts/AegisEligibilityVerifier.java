package com.aegis.bridge.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class AegisEligibilityVerifier extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50610676806100206000396000f3fe608060405234801561001057600080fd5b506004361061002b5760003560e01c80635fe8c13b14610030575b600080fd5b61004361003e3660046105e6565b610057565b604051901515815260200160405180910390f35b6000610567565b7f30644e72e131a029b85045b68181585d2833e84879b9709143e1f593f0000001811061008f576000805260206000f35b50565b600060405183815284602082015285604082015260408160608360076107d05a03fa9150816100c5576000805260206000f35b825160408201526020830151606082015260408360808360066107d05a03fa915050806100f6576000805260206000f35b5050505050565b7f09d46598c82d8dbef6e485a7a9b336c5b25b99375fd8215c4f33bf300147b35e85527e9b5b8a0f5d112b0e4ee4556cff33380ebe1e8e98c32ecafba273c7c68d704a60208601526000608086018661019987357f20ce3a719a80913714925c46658b560aaa73a1ad5bef2f5f51d7b964b7f881a57f14b52b85501aa71ee4a347a4a8ad218d08e557b0a21f6e663beb86f657d062d684610092565b6101e960208801357f0d396415a5187f083e7d66667bbe053e7156d0e4bd1a1261415f816782d29b767f07b6130cc1ccdcf558895a61ef2b603c90208a8b8fc5eaeb27da904edb17121384610092565b61023960408801357f1d3e14fe11e678bce560a7abd62a0cd430e41d8609a73515d11548585e3a7b6f7f04c53377bb1cf99c94ebd472e63bcdb5f87fe8178f5e23e04120e37c77a7d63e84610092565b61028960608801357f0301be9dca753d9a6256fa559bdcba7370b481e280128f11421c907e1f9c5cd67f2d645a607b8960b160ada467fb3b488c45946513184eda0c550da64d4ae379b184610092565b50823581527f30644e72e131a029b85045b68181585d97816a916871ca8d3c208c16d87cfd4760208401357f30644e72e131a029b85045b68181585d97816a916871ca8d3c208c16d87cfd4703066020820152833560408201526020840135606082015260408401356080820152606084013560a08201527f19047d68bfce6686820f3bf32b56188c1d64d344a3863eea527074f4ef0c8e0560c08201527f0d514f82fea3c533a495716d1d9234d8e5bd808693bcd04b264dca2687097e9960e08201527f2372b92b10c2352ec3d473dbed188b9692338c11dcc90800ca4a1ac0beed3c126101008201527e8b99ff99e9ddf2b7bb611c5671bfc876f6af2c2b781a5930173549ca9444b06101208201527f2ab826335cd58136dcf036a6dae1e285f7ac51dfea299323cfea888e53f56df16101408201527e86cc733842539b194531efe9bee3c08ad6816a33a544e15282171a99852323610160820152600087015161018082015260206000018701516101a08201527f198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c26101c08201527f1800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed6101e08201527f090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b6102008201527f12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa610220820152843561024082015260208501356102608201527f283c87bb31ba5a8b84027b3106a1312dda7afdf5be0e567228f407d8ffd49cdb6102808201527f227d30dac9e973f3ff64d0828e5f6ad04d554100637de1db9eb29fabe3f89c566102a08201527f032811e4dc02167a9dca746e09f7f123472ed16d6acaf3531d73fa7d962189f26102c08201527f25361be5c824dad1e223efdd89a25927563e21b60f85cea877e95b416513f8406102e08201526020816103008360086107d05a03fa9051169695505050505050565b604051610380810160405261057f600084013561005e565b61058c602084013561005e565b610599604084013561005e565b6105a6606084013561005e565b6105b3818486888a6100fd565b600090815260209150f35b80604081018310156105cf57600080fd5b92915050565b80608081018310156105cf57600080fd5b60008060008061018085870312156105fd57600080fd5b61060786866105be565b935061061686604087016105d5565b92506106258660c087016105be565b91506106358661010087016105d5565b90509295919450925056fea2646970667358221220f6acf2c602ba80f61bb31b50d2749205e0f9948b540bdc4275f553bc536aeb6964736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b50610676806100206000396000f3fe608060405234801561001057600080fd5b506004361061002b5760003560e01c80630286551a14610030575b600080fd5b61004361003e3660046105e6565b610057565b604051901515815260200160405180910390f35b6000610567565b7f30644e72e131a029b85045b68181585d2833e84879b9709143e1f593f0000001811061008f576000805260206000f35b50565b600060405183815284602082015285604082015260408160608360076107d05a03fa9150816100c5576000805260206000f35b825160408201526020830151606082015260408360808360066107d05a03fa915050806100f6576000805260206000f35b5050505050565b7f09d46598c82d8dbef6e485a7a9b336c5b25b99375fd8215c4f33bf300147b35e85527e9b5b8a0f5d112b0e4ee4556cff33380ebe1e8e98c32ecafba273c7c68d704a60208601526000608086018661019987357f20ce3a719a80913714925c46658b560aaa73a1ad5bef2f5f51d7b964b7f881a57f14b52b85501aa71ee4a347a4a8ad218d08e557b0a21f6e663beb86f657d062d684610092565b6101e960208801357f0d396415a5187f083e7d66667bbe053e7156d0e4bd1a1261415f816782d29b767f07b6130cc1ccdcf558895a61ef2b603c90208a8b8fc5eaeb27da904edb17121384610092565b61023960408801357f1d3e14fe11e678bce560a7abd62a0cd430e41d8609a73515d11548585e3a7b6f7f04c53377bb1cf99c94ebd472e63bcdb5f87fe8178f5e23e04120e37c77a7d63e84610092565b61028960608801357f0301be9dca753d9a6256fa559bdcba7370b481e280128f11421c907e1f9c5cd67f2d645a607b8960b160ada467fb3b488c45946513184eda0c550da64d4ae379b184610092565b50823581527f30644e72e131a029b85045b68181585d97816a916871ca8d3c208c16d87cfd4760208401357f30644e72e131a029b85045b68181585d97816a916871ca8d3c208c16d87cfd4703066020820152833560408201526020840135606082015260408401356080820152606084013560a08201527f19047d68bfce6686820f3bf32b56188c1d64d344a3863eea527074f4ef0c8e0560c08201527f0d514f82fea3c533a495716d1d9234d8e5bd808693bcd04b264dca2687097e9960e08201527f2372b92b10c2352ec3d473dbed188b9692338c11dcc90800ca4a1ac0beed3c126101008201527e8b99ff99e9ddf2b7bb611c5671bfc876f6af2c2b781a5930173549ca9444b06101208201527f2ab826335cd58136dcf036a6dae1e285f7ac51dfea299323cfea888e53f56df16101408201527e86cc733842539b194531efe9bee3c08ad6816a33a544e15282171a99852323610160820152600087015161018082015260206000018701516101a08201527f198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c26101c08201527f1800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed6101e08201527f090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b6102008201527f12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa610220820152843561024082015260208501356102608201527f283c87bb31ba5a8b84027b3106a1312dda7afdf5be0e567228f407d8ffd49cdb6102808201527f227d30dac9e973f3ff64d0828e5f6ad04d554100637de1db9eb29fabe3f89c566102a08201527f032811e4dc02167a9dca746e09f7f123472ed16d6acaf3531d73fa7d962189f26102c08201527f25361be5c824dad1e223efdd89a25927563e21b60f85cea877e95b416513f8406102e08201526020816103008360086107d05a03fa9051169695505050505050565b604051610380810160405261057f600084013561005e565b61058c602084013561005e565b610599604084013561005e565b6105a6606084013561005e565b6105b3818486888a6100fd565b600090815260209150f35b80604081018310156105cf57600080fd5b92915050565b80608081018310156105cf57600080fd5b60008060008061018085870312156105fd57600080fd5b61060786866105be565b935061061686604087016105d5565b92506106258660c087016105be565b91506106358661010087016105d5565b90509295919450925056fea2646970667358221220aec1870445222d39cd93d8f8a8163a7fa2e28fbb0bb3ee3e3729c11aa9a16d4764736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"uint256[2]\",\"name\":\"_pA\",\"type\":\"uint256[2]\"},{\"internalType\":\"uint256[2][2]\",\"name\":\"_pB\",\"type\":\"uint256[2][2]\"},{\"internalType\":\"uint256[2]\",\"name\":\"_pC\",\"type\":\"uint256[2]\"},{\"internalType\":\"uint256[4]\",\"name\":\"_pubSignals\",\"type\":\"uint256[4]\"}],\"name\":\"verifyProof\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1609089339,42358042],\"stateMutability\":\"view\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_VERIFYPROOF = "verifyProof";

    protected AegisEligibilityVerifier(String contractAddress, Client client,
            CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public Boolean verifyProof(List<BigInteger> _pA, List<List<BigInteger>> _pB,
            List<BigInteger> _pC, List<BigInteger> _pubSignals) throws ContractException {
        final Function function = new Function(FUNC_VERIFYPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(_pA, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(_pB, org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)) {
                    @Override
                    public String getTypeAsString() {
                        return "uint256[2][2]";
                    }
                }, 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(_pC, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray4<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(_pubSignals, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class))), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Function getMethodVerifyProofRawFunction(List<BigInteger> _pA,
            List<List<BigInteger>> _pB, List<BigInteger> _pC, List<BigInteger> _pubSignals) throws
            ContractException {
        final Function function = new Function(FUNC_VERIFYPROOF, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(_pA, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(_pB, org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2.class,
                org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)) {
                    @Override
                    public String getTypeAsString() {
                        return "uint256[2][2]";
                    }
                }, 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(_pC, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class)), 
                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray4<org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256>(
                        org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(_pubSignals, org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256.class))), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    public static AegisEligibilityVerifier load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new AegisEligibilityVerifier(contractAddress, client, credential);
    }

    public static AegisEligibilityVerifier deploy(Client client, CryptoKeyPair credential) throws
            ContractException {
        return deploy(AegisEligibilityVerifier.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }
}

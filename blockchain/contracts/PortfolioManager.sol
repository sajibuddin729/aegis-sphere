// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

interface IPortfolioToken {

    function approve(
        address spender,
        uint256 amount
    )
        external
        returns (bool);

    function balanceOf(
        address account
    )
        external
        view
        returns (uint256);
}

interface IPortfolioPoIRegistry {

    function recordPoI(
        bytes32 poiHash,
        bytes32 intentId,
        bytes32 policyHash,
        address token,
        address recipient,
        uint256 amount,
        uint256 riskScoreBps
    )
        external;
}

interface IPortfolioRiskGuard {

    function validateAndConsume(
        uint256 notionalAcnyMinor,
        uint256 riskScoreBps
    )
        external
        returns (uint256);
}

interface IPortfolioSwapPool {

    function tokenA()
        external
        view
        returns (address);

    function tokenB()
        external
        view
        returns (address);

    function getQuoteAToB(
        uint256 amountA
    )
        external
        view
        returns (uint256);

    function getQuoteBToA(
        uint256 amountB
    )
        external
        view
        returns (uint256);

    function swapAToB(
        uint256 amountA,
        uint256 minAmountB
    )
        external
        returns (uint256);

    function swapBToA(
        uint256 amountB,
        uint256 minAmountA
    )
        external
        returns (uint256);
}

/// @title Aegis Portfolio Manager
/// @notice Autonomous multi-asset portfolio management sub-vault.
contract PortfolioManager {

    address public owner;
    address public agentExecutor;

    address public tokenA;
    address public tokenB;

    address public swapPool;

    IPortfolioPoIRegistry
        public poiRegistry;

    IPortfolioRiskGuard
        public riskGuard;

    bool private locked;

    event AgentExecutorUpdated(
        address indexed previousExecutor,
        address indexed newExecutor
    );

    event PortfolioRebalanced(
        bytes32 indexed poiHash,
        bytes32 indexed intentId,
        address indexed tokenIn,
        address tokenOut,
        uint256 amountIn,
        uint256 amountOut,
        uint256 notionalAcnyMinor,
        uint256 riskScoreBps
    );

    modifier onlyOwner() {
        require(
            msg.sender == owner,
            "AEGIS: NOT_OWNER"
        );
        _;
    }

    modifier onlyAgentExecutor() {
        require(
            msg.sender == agentExecutor,
            "AEGIS: NOT_AGENT_EXECUTOR"
        );
        _;
    }

    modifier nonReentrant() {
        require(
            !locked,
            "AEGIS: REENTRANCY"
        );

        locked = true;
        _;
        locked = false;
    }

    constructor(
        address poiRegistryAddress,
        address riskGuardAddress,
        address poolAddress,
        address tokenAAddress,
        address tokenBAddress
    ) {
        require(
            poiRegistryAddress != address(0),
            "AEGIS: INVALID_POI"
        );

        require(
            riskGuardAddress != address(0),
            "AEGIS: INVALID_RISK"
        );

        require(
            poolAddress != address(0),
            "AEGIS: INVALID_POOL"
        );

        require(
            tokenAAddress != address(0)
            && tokenBAddress != address(0),
            "AEGIS: INVALID_TOKEN"
        );

        require(
            IPortfolioSwapPool(poolAddress)
                .tokenA()
            == tokenAAddress,
            "AEGIS: POOL_TOKEN_A_MISMATCH"
        );

        require(
            IPortfolioSwapPool(poolAddress)
                .tokenB()
            == tokenBAddress,
            "AEGIS: POOL_TOKEN_B_MISMATCH"
        );

        owner = msg.sender;

        agentExecutor = msg.sender;

        poiRegistry =
            IPortfolioPoIRegistry(
                poiRegistryAddress
            );

        riskGuard =
            IPortfolioRiskGuard(
                riskGuardAddress
            );

        swapPool = poolAddress;

        tokenA = tokenAAddress;
        tokenB = tokenBAddress;
    }

    function setAgentExecutor(
        address newExecutor
    )
        external
        onlyOwner
    {
        require(
            newExecutor != address(0),
            "AEGIS: ZERO_ADDRESS"
        );

        address previous =
            agentExecutor;

        agentExecutor =
            newExecutor;

        emit AgentExecutorUpdated(
            previous,
            newExecutor
        );
    }

    function executeRebalance(
        address tokenIn,
        uint256 amountIn,
        uint256 minAmountOut,
        bytes32 poiHash,
        bytes32 intentId,
        bytes32 policyHash,
        uint256 riskScoreBps
    )
        external
        onlyAgentExecutor
        nonReentrant
        returns (uint256 amountOut)
    {
        require(
            amountIn > 0,
            "AEGIS: ZERO_AMOUNT"
        );

        require(
            poiHash != bytes32(0),
            "AEGIS: INVALID_POI"
        );

        uint256 notionalAcnyMinor;

        address tokenOut;

        if (
            tokenIn == tokenA
        ) {
            tokenOut =
                tokenB;

            notionalAcnyMinor =
                amountIn;

        } else if (
            tokenIn == tokenB
        ) {
            tokenOut =
                tokenA;

            notionalAcnyMinor =
                IPortfolioSwapPool(
                    swapPool
                )
                .getQuoteBToA(
                    amountIn
                );

        } else {
            revert(
                "AEGIS: UNSUPPORTED_TOKEN"
            );
        }

        riskGuard
            .validateAndConsume(
                notionalAcnyMinor,
                riskScoreBps
            );

        poiRegistry
            .recordPoI(
                poiHash,
                intentId,
                policyHash,
                tokenIn,
                swapPool,
                amountIn,
                riskScoreBps
            );

        require(
            IPortfolioToken(tokenIn)
                .approve(
                    swapPool,
                    0
                ),
            "AEGIS: APPROVE_RESET_FAILED"
        );

        require(
            IPortfolioToken(tokenIn)
                .approve(
                    swapPool,
                    amountIn
                ),
            "AEGIS: APPROVE_FAILED"
        );

        if (
            tokenIn == tokenA
        ) {
            amountOut =
                IPortfolioSwapPool(
                    swapPool
                )
                .swapAToB(
                    amountIn,
                    minAmountOut
                );

        } else {
            amountOut =
                IPortfolioSwapPool(
                    swapPool
                )
                .swapBToA(
                    amountIn,
                    minAmountOut
                );
        }

        emit PortfolioRebalanced(
            poiHash,
            intentId,
            tokenIn,
            tokenOut,
            amountIn,
            amountOut,
            notionalAcnyMinor,
            riskScoreBps
        );
    }

    function getBalanceA()
        external
        view
        returns (uint256)
    {
        return
            IPortfolioToken(tokenA)
                .balanceOf(
                    address(this)
                );
    }

    function getBalanceB()
        external
        view
        returns (uint256)
    {
        return
            IPortfolioToken(tokenB)
                .balanceOf(
                    address(this)
                );
    }

    function getPortfolioState()
        external
        view
        returns (
            uint256 balanceA,
            uint256 balanceB,
            uint256 totalValueA,
            uint256 allocationABps,
            uint256 allocationBBps
        )
    {
        balanceA =
            IPortfolioToken(tokenA)
                .balanceOf(
                    address(this)
                );

        balanceB =
            IPortfolioToken(tokenB)
                .balanceOf(
                    address(this)
                );

        uint256 valueBInA =
            IPortfolioSwapPool(
                swapPool
            )
            .getQuoteBToA(
                balanceB
            );

        totalValueA =
            balanceA
            + valueBInA;

        if (
            totalValueA == 0
        ) {
            return (
                balanceA,
                balanceB,
                0,
                0,
                0
            );
        }

        allocationABps =
            (
                balanceA
                * 10000
            )
            / totalValueA;

        allocationBBps =
            (
                valueBInA
                * 10000
            )
            / totalValueA;
    }
}

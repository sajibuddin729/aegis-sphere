// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

interface IAegisPoolToken {

    function transfer(
        address to,
        uint256 amount
    )
        external
        returns (bool);

    function transferFrom(
        address from,
        address to,
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

/// @title Aegis Synthetic Swap Pool
/// @notice Deterministic testnet liquidity pool for portfolio rebalancing.
contract AegisSwapPool {

    address public owner;

    address public tokenA;
    address public tokenB;

    /// @notice Value of one tokenB minor unit
    ///         expressed in tokenA minor units.
    ///
    /// Example:
    /// priceBInA = 10
    ///
    /// 1.00 AGIX = 10.00 ACNY
    /// when both assets use 2 decimals.
    uint256 public priceBInA;

    bool public paused;

    event PriceUpdated(
        uint256 previousPrice,
        uint256 newPrice
    );

    event PauseUpdated(
        bool paused
    );

    event SwapAToB(
        address indexed trader,
        uint256 amountA,
        uint256 amountB
    );

    event SwapBToA(
        address indexed trader,
        uint256 amountB,
        uint256 amountA
    );

    modifier onlyOwner() {
        require(
            msg.sender == owner,
            "AEGIS: NOT_OWNER"
        );
        _;
    }

    constructor(
        address tokenAAddress,
        address tokenBAddress,
        uint256 initialPriceBInA
    ) {
        require(
            tokenAAddress != address(0),
            "AEGIS: INVALID_TOKEN_A"
        );

        require(
            tokenBAddress != address(0),
            "AEGIS: INVALID_TOKEN_B"
        );

        require(
            tokenAAddress != tokenBAddress,
            "AEGIS: SAME_TOKEN"
        );

        require(
            initialPriceBInA > 0,
            "AEGIS: INVALID_PRICE"
        );

        owner = msg.sender;

        tokenA = tokenAAddress;
        tokenB = tokenBAddress;

        priceBInA =
            initialPriceBInA;
    }

    function setPriceBInA(
        uint256 newPrice
    )
        external
        onlyOwner
    {
        require(
            newPrice > 0,
            "AEGIS: INVALID_PRICE"
        );

        uint256 previous =
            priceBInA;

        priceBInA =
            newPrice;

        emit PriceUpdated(
            previous,
            newPrice
        );
    }

    function setPaused(
        bool state
    )
        external
        onlyOwner
    {
        paused = state;

        emit PauseUpdated(state);
    }

    function getQuoteAToB(
        uint256 amountA
    )
        public
        view
        returns (uint256)
    {
        return (
            amountA
            / priceBInA
        );
    }

    function getQuoteBToA(
        uint256 amountB
    )
        public
        view
        returns (uint256)
    {
        return (
            amountB
            * priceBInA
        );
    }

    function swapAToB(
        uint256 amountA,
        uint256 minAmountB
    )
        external
        returns (uint256 amountB)
    {
        require(
            !paused,
            "AEGIS: POOL_PAUSED"
        );

        require(
            amountA > 0,
            "AEGIS: ZERO_AMOUNT"
        );

        amountB =
            getQuoteAToB(
                amountA
            );

        require(
            amountB > 0,
            "AEGIS: OUTPUT_ZERO"
        );

        require(
            amountB >= minAmountB,
            "AEGIS: SLIPPAGE_LIMIT"
        );

        require(
            IAegisPoolToken(tokenB)
                .balanceOf(
                    address(this)
                )
            >= amountB,
            "AEGIS: INSUFFICIENT_POOL_LIQUIDITY"
        );

        require(
            IAegisPoolToken(tokenA)
                .transferFrom(
                    msg.sender,
                    address(this),
                    amountA
                ),
            "AEGIS: TOKEN_A_TRANSFER_FAILED"
        );

        require(
            IAegisPoolToken(tokenB)
                .transfer(
                    msg.sender,
                    amountB
                ),
            "AEGIS: TOKEN_B_TRANSFER_FAILED"
        );

        emit SwapAToB(
            msg.sender,
            amountA,
            amountB
        );
    }

    function swapBToA(
        uint256 amountB,
        uint256 minAmountA
    )
        external
        returns (uint256 amountA)
    {
        require(
            !paused,
            "AEGIS: POOL_PAUSED"
        );

        require(
            amountB > 0,
            "AEGIS: ZERO_AMOUNT"
        );

        amountA =
            getQuoteBToA(
                amountB
            );

        require(
            amountA >= minAmountA,
            "AEGIS: SLIPPAGE_LIMIT"
        );

        require(
            IAegisPoolToken(tokenA)
                .balanceOf(
                    address(this)
                )
            >= amountA,
            "AEGIS: INSUFFICIENT_POOL_LIQUIDITY"
        );

        require(
            IAegisPoolToken(tokenB)
                .transferFrom(
                    msg.sender,
                    address(this),
                    amountB
                ),
            "AEGIS: TOKEN_B_TRANSFER_FAILED"
        );

        require(
            IAegisPoolToken(tokenA)
                .transfer(
                    msg.sender,
                    amountA
                ),
            "AEGIS: TOKEN_A_TRANSFER_FAILED"
        );

        emit SwapBToA(
            msg.sender,
            amountB,
            amountA
        );
    }
}

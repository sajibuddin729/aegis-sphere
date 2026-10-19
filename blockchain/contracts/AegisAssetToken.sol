// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

/// @title Aegis Synthetic Asset Token
/// @notice Generic testnet asset for Aegis-Sphere portfolio simulation.
/// @dev Not a representation of any official currency, security, ETF or cryptocurrency.
contract AegisAssetToken {

    string public name;
    string public symbol;
    uint8 public decimals;

    uint256 public totalSupply;

    address public owner;

    mapping(address => uint256)
        public balanceOf;

    mapping(address => mapping(address => uint256))
        public allowance;

    event Transfer(
        address indexed from,
        address indexed to,
        uint256 amount
    );

    event Approval(
        address indexed owner,
        address indexed spender,
        uint256 amount
    );

    event Mint(
        address indexed to,
        uint256 amount
    );

    modifier onlyOwner() {
        require(
            msg.sender == owner,
            "AEGIS: NOT_OWNER"
        );
        _;
    }

    constructor(
        string memory tokenName,
        string memory tokenSymbol,
        uint8 tokenDecimals
    ) {
        owner = msg.sender;

        name = tokenName;
        symbol = tokenSymbol;
        decimals = tokenDecimals;
    }

    function mint(
        address to,
        uint256 amount
    )
        external
        onlyOwner
    {
        require(
            to != address(0),
            "AEGIS: ZERO_ADDRESS"
        );

        require(
            amount > 0,
            "AEGIS: ZERO_AMOUNT"
        );

        totalSupply += amount;
        balanceOf[to] += amount;

        emit Transfer(
            address(0),
            to,
            amount
        );

        emit Mint(
            to,
            amount
        );
    }

    function transfer(
        address to,
        uint256 amount
    )
        external
        returns (bool)
    {
        _transfer(
            msg.sender,
            to,
            amount
        );

        return true;
    }

    function approve(
        address spender,
        uint256 amount
    )
        external
        returns (bool)
    {
        require(
            spender != address(0),
            "AEGIS: ZERO_ADDRESS"
        );

        allowance[msg.sender][spender] =
            amount;

        emit Approval(
            msg.sender,
            spender,
            amount
        );

        return true;
    }

    function transferFrom(
        address from,
        address to,
        uint256 amount
    )
        external
        returns (bool)
    {
        uint256 allowed =
            allowance[from][msg.sender];

        require(
            allowed >= amount,
            "AEGIS: ALLOWANCE_EXCEEDED"
        );

        if (
            allowed
            != type(uint256).max
        ) {
            allowance[from][msg.sender] =
                allowed - amount;

            emit Approval(
                from,
                msg.sender,
                allowance[from][msg.sender]
            );
        }

        _transfer(
            from,
            to,
            amount
        );

        return true;
    }

    function _transfer(
        address from,
        address to,
        uint256 amount
    )
        internal
    {
        require(
            to != address(0),
            "AEGIS: ZERO_ADDRESS"
        );

        require(
            amount > 0,
            "AEGIS: ZERO_AMOUNT"
        );

        require(
            balanceOf[from] >= amount,
            "AEGIS: INSUFFICIENT_BALANCE"
        );

        balanceOf[from] -= amount;
        balanceOf[to] += amount;

        emit Transfer(
            from,
            to,
            amount
        );
    }
}

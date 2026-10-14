// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

/// @title AegisCNY Testnet Token
/// @notice Simulated financial asset for the Aegis-Sphere prototype.
/// @dev This token is NOT official e-CNY.
contract AegisToken {
    string public constant name = "AegisCNY";
    string public constant symbol = "ACNY";
    uint8 public constant decimals = 2;

    uint256 public totalSupply;
    address public owner;

    mapping(address => uint256) public balanceOf;
    mapping(address => mapping(address => uint256)) public allowance;

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

    event TokensMinted(
        address indexed to,
        uint256 amount
    );

    modifier onlyOwner() {
        require(msg.sender == owner, "AEGIS: NOT_OWNER");
        _;
    }

    constructor() {
        owner = msg.sender;
    }

    function mint(address to, uint256 amount) external onlyOwner {
        require(to != address(0), "AEGIS: ZERO_ADDRESS");
        require(amount > 0, "AEGIS: ZERO_AMOUNT");

        totalSupply += amount;
        balanceOf[to] += amount;

        emit Transfer(address(0), to, amount);
        emit TokensMinted(to, amount);
    }

    function transfer(
        address to,
        uint256 amount
    ) external returns (bool) {
        _transfer(msg.sender, to, amount);
        return true;
    }

    function approve(
        address spender,
        uint256 amount
    ) external returns (bool) {
        require(spender != address(0), "AEGIS: ZERO_ADDRESS");

        allowance[msg.sender][spender] = amount;

        emit Approval(msg.sender, spender, amount);

        return true;
    }

    function transferFrom(
        address from,
        address to,
        uint256 amount
    ) external returns (bool) {
        uint256 currentAllowance = allowance[from][msg.sender];

        require(
            currentAllowance >= amount,
            "AEGIS: ALLOWANCE_EXCEEDED"
        );

        if (currentAllowance != type(uint256).max) {
            allowance[from][msg.sender] =
                currentAllowance - amount;

            emit Approval(
                from,
                msg.sender,
                allowance[from][msg.sender]
            );
        }

        _transfer(from, to, amount);

        return true;
    }

    function _transfer(
        address from,
        address to,
        uint256 amount
    ) internal {
        require(to != address(0), "AEGIS: ZERO_ADDRESS");
        require(amount > 0, "AEGIS: ZERO_AMOUNT");
        require(
            balanceOf[from] >= amount,
            "AEGIS: INSUFFICIENT_BALANCE"
        );

        balanceOf[from] -= amount;
        balanceOf[to] += amount;

        emit Transfer(from, to, amount);
    }
}

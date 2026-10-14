// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

/// @title Aegis Risk Circuit Breaker
/// @notice Automated deterministic financial risk controls.
contract RiskCircuitBreaker {
    address public owner;
    bool public paused;

    uint256 public maxTransactionAmount;
    uint256 public maxDailyAmount;
    uint256 public maxRiskScoreBps;

    mapping(address => bool) public authorizedWallets;
    mapping(address => bool) public allowedTokens;
    mapping(address => bool) public blockedRecipients;

    mapping(address => mapping(uint256 => uint256)) public spentByWalletDay;

    event AuthorizedWalletUpdated(
        address indexed wallet,
        bool authorized
    );

    event AllowedTokenUpdated(
        address indexed token,
        bool allowed
    );

    event RecipientBlockUpdated(
        address indexed recipient,
        bool blocked
    );

    event PauseStateUpdated(
        bool paused
    );

    event RiskLimitsUpdated(
        uint256 maxTransactionAmount,
        uint256 maxDailyAmount,
        uint256 maxRiskScoreBps
    );

    event TransactionRiskApproved(
        address indexed wallet,
        address indexed token,
        address indexed recipient,
        uint256 amount,
        uint256 riskScoreBps,
        uint256 spentToday
    );

    modifier onlyOwner() {
        require(msg.sender == owner, "AEGIS: NOT_OWNER");
        _;
    }

    modifier onlyAuthorizedWallet() {
        require(
            authorizedWallets[msg.sender],
            "AEGIS: WALLET_NOT_AUTHORIZED"
        );
        _;
    }

    constructor(
        uint256 initialMaxTransactionAmount,
        uint256 initialMaxDailyAmount,
        uint256 initialMaxRiskScoreBps
    ) {
        require(
            initialMaxTransactionAmount > 0,
            "AEGIS: INVALID_MAX_TX"
        );
        require(
            initialMaxDailyAmount >= initialMaxTransactionAmount,
            "AEGIS: INVALID_DAILY_LIMIT"
        );
        require(
            initialMaxRiskScoreBps <= 10000,
            "AEGIS: INVALID_RISK"
        );

        owner = msg.sender;

        maxTransactionAmount = initialMaxTransactionAmount;
        maxDailyAmount = initialMaxDailyAmount;
        maxRiskScoreBps = initialMaxRiskScoreBps;
    }

    function setAuthorizedWallet(
        address wallet,
        bool authorized
    ) external onlyOwner {
        require(wallet != address(0), "AEGIS: ZERO_ADDRESS");

        authorizedWallets[wallet] = authorized;

        emit AuthorizedWalletUpdated(
            wallet,
            authorized
        );
    }

    function setAllowedToken(
        address token,
        bool allowed
    ) external onlyOwner {
        require(token != address(0), "AEGIS: ZERO_ADDRESS");

        allowedTokens[token] = allowed;

        emit AllowedTokenUpdated(token, allowed);
    }

    function setBlockedRecipient(
        address recipient,
        bool blocked
    ) external onlyOwner {
        require(
            recipient != address(0),
            "AEGIS: ZERO_ADDRESS"
        );

        blockedRecipients[recipient] = blocked;

        emit RecipientBlockUpdated(
            recipient,
            blocked
        );
    }

    function setPaused(
        bool pauseState
    ) external onlyOwner {
        paused = pauseState;

        emit PauseStateUpdated(pauseState);
    }

    function updateLimits(
        uint256 newMaxTransactionAmount,
        uint256 newMaxDailyAmount,
        uint256 newMaxRiskScoreBps
    ) external onlyOwner {
        require(
            newMaxTransactionAmount > 0,
            "AEGIS: INVALID_MAX_TX"
        );
        require(
            newMaxDailyAmount >= newMaxTransactionAmount,
            "AEGIS: INVALID_DAILY_LIMIT"
        );
        require(
            newMaxRiskScoreBps <= 10000,
            "AEGIS: INVALID_RISK"
        );

        maxTransactionAmount = newMaxTransactionAmount;
        maxDailyAmount = newMaxDailyAmount;
        maxRiskScoreBps = newMaxRiskScoreBps;

        emit RiskLimitsUpdated(
            newMaxTransactionAmount,
            newMaxDailyAmount,
            newMaxRiskScoreBps
        );
    }

    function validateAndConsume(
        address token,
        address recipient,
        uint256 amount,
        uint256 riskScoreBps
    ) external onlyAuthorizedWallet returns (uint256) {
        require(!paused, "AEGIS: SYSTEM_PAUSED");
        require(
            allowedTokens[token],
            "AEGIS: TOKEN_NOT_ALLOWED"
        );
        require(
            recipient != address(0),
            "AEGIS: INVALID_RECIPIENT"
        );
        require(
            !blockedRecipients[recipient],
            "AEGIS: RECIPIENT_BLOCKED"
        );
        require(amount > 0, "AEGIS: ZERO_AMOUNT");
        require(
            amount <= maxTransactionAmount,
            "AEGIS: TX_LIMIT_EXCEEDED"
        );
        require(
            riskScoreBps <= maxRiskScoreBps,
            "AEGIS: RISK_TOO_HIGH"
        );

        uint256 dayIndex = block.timestamp / 1 days;
        uint256 newSpent = spentByWalletDay[msg.sender][dayIndex] + amount;

        require(
            newSpent <= maxDailyAmount,
            "AEGIS: DAILY_LIMIT_EXCEEDED"
        );

        spentByWalletDay[msg.sender][dayIndex] = newSpent;

        emit TransactionRiskApproved(
            msg.sender,
            token,
            recipient,
            amount,
            riskScoreBps,
            newSpent
        );

        return newSpent;
    }

    function getSpentToday(
        address wallet
    ) external view returns (uint256) {
        uint256 dayIndex = block.timestamp / 1 days;
        return spentByWalletDay[wallet][dayIndex];
    }
}

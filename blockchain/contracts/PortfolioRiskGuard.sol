// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

/// @title Aegis Portfolio Risk Guard
/// @notice Enforces portfolio trade risk using ACNY-equivalent notional value.
contract PortfolioRiskGuard {

    address public owner;

    bool public paused;

    uint256 public maxTradeNotional;
    uint256 public maxDailyNotional;
    uint256 public maxRiskScoreBps;

    mapping(address => bool)
        public authorizedManagers;

    mapping(address => mapping(uint256 => uint256))
        public spentByManagerDay;

    event ManagerAuthorizationUpdated(
        address indexed manager,
        bool authorized
    );

    event PauseUpdated(
        bool paused
    );

    event PortfolioRiskApproved(
        address indexed manager,
        uint256 notionalAcnyMinor,
        uint256 riskScoreBps,
        uint256 spentToday
    );

    modifier onlyOwner() {
        require(
            msg.sender == owner,
            "AEGIS: NOT_OWNER"
        );
        _;
    }

    modifier onlyAuthorizedManager() {
        require(
            authorizedManagers[msg.sender],
            "AEGIS: MANAGER_NOT_AUTHORIZED"
        );
        _;
    }

    constructor(
        uint256 initialMaxTradeNotional,
        uint256 initialMaxDailyNotional,
        uint256 initialMaxRiskScoreBps
    ) {
        require(
            initialMaxTradeNotional > 0,
            "AEGIS: INVALID_MAX_TRADE"
        );

        require(
            initialMaxDailyNotional
            >= initialMaxTradeNotional,
            "AEGIS: INVALID_DAILY_LIMIT"
        );

        require(
            initialMaxRiskScoreBps <= 10000,
            "AEGIS: INVALID_RISK"
        );

        owner = msg.sender;

        maxTradeNotional =
            initialMaxTradeNotional;

        maxDailyNotional =
            initialMaxDailyNotional;

        maxRiskScoreBps =
            initialMaxRiskScoreBps;
    }

    function setAuthorizedManager(
        address manager,
        bool authorized
    )
        external
        onlyOwner
    {
        require(
            manager != address(0),
            "AEGIS: ZERO_ADDRESS"
        );

        authorizedManagers[manager] =
            authorized;

        emit ManagerAuthorizationUpdated(
            manager,
            authorized
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

    function validateAndConsume(
        uint256 notionalAcnyMinor,
        uint256 riskScoreBps
    )
        external
        onlyAuthorizedManager
        returns (uint256)
    {
        require(
            !paused,
            "AEGIS: PORTFOLIO_PAUSED"
        );

        require(
            notionalAcnyMinor > 0,
            "AEGIS: ZERO_NOTIONAL"
        );

        require(
            notionalAcnyMinor
            <= maxTradeNotional,
            "AEGIS: PORTFOLIO_TRADE_LIMIT"
        );

        require(
            riskScoreBps
            <= maxRiskScoreBps,
            "AEGIS: PORTFOLIO_RISK_TOO_HIGH"
        );

        uint256 dayIndex =
            block.timestamp / 1 days;

        uint256 newSpent =
            spentByManagerDay[
                msg.sender
            ][dayIndex]
            + notionalAcnyMinor;

        require(
            newSpent
            <= maxDailyNotional,
            "AEGIS: PORTFOLIO_DAILY_LIMIT"
        );

        spentByManagerDay[
            msg.sender
        ][dayIndex] =
            newSpent;

        emit PortfolioRiskApproved(
            msg.sender,
            notionalAcnyMinor,
            riskScoreBps,
            newSpent
        );

        return newSpent;
    }

    function getSpentToday(
        address manager
    )
        external
        view
        returns (uint256)
    {
        uint256 dayIndex =
            block.timestamp / 1 days;

        return spentByManagerDay[
            manager
        ][dayIndex];
    }
}

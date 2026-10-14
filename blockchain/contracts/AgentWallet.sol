// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

interface IAegisToken {
    function transfer(
        address to,
        uint256 amount
    ) external returns (bool);

    function balanceOf(
        address account
    ) external view returns (uint256);
}

interface IPoIRegistry {
    function recordPoI(
        bytes32 poiHash,
        bytes32 intentId,
        bytes32 policyHash,
        address token,
        address recipient,
        uint256 amount,
        uint256 riskScoreBps
    ) external;
}

interface IRiskCircuitBreaker {
    function validateAndConsume(
        address token,
        address recipient,
        uint256 amount,
        uint256 riskScoreBps
    ) external returns (uint256);
}

/// @title Aegis Autonomous Agent Wallet
/// @notice Executes autonomous token transfers only after risk validation
/// and cryptographic Proof-of-Intent registration.
contract AgentWallet {
    address public owner;
    address public agentExecutor;

    IPoIRegistry public poiRegistry;
    IRiskCircuitBreaker public riskEngine;

    bool private locked;

    event AgentExecutorUpdated(
        address indexed previousExecutor,
        address indexed newExecutor
    );

    event AutonomousTransferExecuted(
        bytes32 indexed poiHash,
        bytes32 indexed intentId,
        address indexed recipient,
        address token,
        uint256 amount,
        uint256 riskScoreBps,
        bytes32 policyHash
    );

    modifier onlyOwner() {
        require(msg.sender == owner, "AEGIS: NOT_OWNER");
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
        require(!locked, "AEGIS: REENTRANCY");
        locked = true;
        _;
        locked = false;
    }

    constructor(
        address poiRegistryAddress,
        address riskEngineAddress
    ) {
        require(
            poiRegistryAddress != address(0),
            "AEGIS: INVALID_POI_REGISTRY"
        );
        require(
            riskEngineAddress != address(0),
            "AEGIS: INVALID_RISK_ENGINE"
        );

        owner = msg.sender;

        // During the blockchain-core prototype,
        // the deployer acts as the temporary agent executor.
        // A dedicated backend signing identity will replace it later.
        agentExecutor = msg.sender;

        poiRegistry = IPoIRegistry(poiRegistryAddress);
        riskEngine = IRiskCircuitBreaker(riskEngineAddress);
    }

    function setAgentExecutor(
        address newExecutor
    ) external onlyOwner {
        require(
            newExecutor != address(0),
            "AEGIS: ZERO_ADDRESS"
        );

        address previousExecutor = agentExecutor;
        agentExecutor = newExecutor;

        emit AgentExecutorUpdated(
            previousExecutor,
            newExecutor
        );
    }

    function executeTokenTransfer(
        address token,
        address recipient,
        uint256 amount,
        bytes32 poiHash,
        bytes32 intentId,
        bytes32 policyHash,
        uint256 riskScoreBps
    ) external onlyAgentExecutor nonReentrant returns (bool) {
        require(
            token != address(0),
            "AEGIS: INVALID_TOKEN"
        );
        require(
            recipient != address(0),
            "AEGIS: INVALID_RECIPIENT"
        );

        riskEngine.validateAndConsume(
            token,
            recipient,
            amount,
            riskScoreBps
        );

        poiRegistry.recordPoI(
            poiHash,
            intentId,
            policyHash,
            token,
            recipient,
            amount,
            riskScoreBps
        );

        bool transferred = IAegisToken(token).transfer(
            recipient,
            amount
        );

        require(
            transferred,
            "AEGIS: TRANSFER_FAILED"
        );

        emit AutonomousTransferExecuted(
            poiHash,
            intentId,
            recipient,
            token,
            amount,
            riskScoreBps,
            policyHash
        );

        return true;
    }

    function getTokenBalance(
        address token
    ) external view returns (uint256) {
        return IAegisToken(token).balanceOf(
            address(this)
        );
    }
}

// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

/// @title Aegis Proof-of-Intent Registry
/// @notice Stores tamper-evident references for autonomous financial actions.
contract PoIRegistry {
    address public owner;

    mapping(address => bool) public authorizedWallets;

    struct PoIRecord {
        bytes32 intentId;
        bytes32 policyHash;

        address wallet;
        address token;
        address recipient;

        uint256 amount;
        uint256 riskScoreBps;

        uint256 timestamp;
        uint256 blockNumber;

        bool exists;
    }

    mapping(bytes32 => PoIRecord) private records;

    event AuthorizedWalletUpdated(
        address indexed wallet,
        bool authorized
    );

    event PoIRecorded(
        bytes32 indexed poiHash,
        bytes32 indexed intentId,
        address indexed wallet,
        address token,
        address recipient,
        uint256 amount,
        uint256 riskScoreBps,
        bytes32 policyHash
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

    constructor() {
        owner = msg.sender;
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

    function recordPoI(
        bytes32 poiHash,
        bytes32 intentId,
        bytes32 policyHash,
        address token,
        address recipient,
        uint256 amount,
        uint256 riskScoreBps
    ) external onlyAuthorizedWallet {

        require(
            poiHash != bytes32(0),
            "AEGIS: INVALID_POI"
        );

        require(
            !records[poiHash].exists,
            "AEGIS: POI_ALREADY_EXISTS"
        );

        records[poiHash] = PoIRecord({
            intentId: intentId,
            policyHash: policyHash,
            wallet: msg.sender,
            token: token,
            recipient: recipient,
            amount: amount,
            riskScoreBps: riskScoreBps,
            timestamp: block.timestamp,
            blockNumber: block.number,
            exists: true
        });

        emit PoIRecorded(
            poiHash,
            intentId,
            msg.sender,
            token,
            recipient,
            amount,
            riskScoreBps,
            policyHash
        );
    }

    function exists(
        bytes32 poiHash
    ) external view returns (bool) {
        return records[poiHash].exists;
    }

    function getPoI(
        bytes32 poiHash
    )
        external
        view
        returns (
            bytes32 intentId,
            bytes32 policyHash,
            address wallet,
            address token,
            address recipient,
            uint256 amount,
            uint256 riskScoreBps,
            uint256 timestamp,
            uint256 blockNumber,
            bool recordExists
        )
    {
        PoIRecord memory record = records[poiHash];

        return (
            record.intentId,
            record.policyHash,
            record.wallet,
            record.token,
            record.recipient,
            record.amount,
            record.riskScoreBps,
            record.timestamp,
            record.blockNumber,
            record.exists
        );
    }
}

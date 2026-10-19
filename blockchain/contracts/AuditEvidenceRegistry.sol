// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

/// @title Aegis Audit Evidence Registry
/// @notice Stores tamper-evident hashes for compliance
/// and security evidence without storing raw
/// regulatory text, PII, or private reasoning.
contract AuditEvidenceRegistry {
    address public owner;
    mapping(address => bool) public authorizedWriters;

    struct EvidenceRecord {
        bytes32 policyVersionHash;
        bytes32 retrievalSetHash;
        bytes32 decisionHash;
        bytes32 relatedPoIHash;
        address recorder;
        uint256 timestamp;
        uint256 blockNumber;
        bool exists;
    }

    mapping(bytes32 => EvidenceRecord) private evidenceRecords;

    event AuthorizedWriterUpdated(
        address indexed writer,
        bool authorized
    );

    event EvidenceAnchored(
        bytes32 indexed evidenceHash,
        bytes32 indexed relatedPoIHash,
        bytes32 indexed decisionHash,
        bytes32 policyVersionHash,
        bytes32 retrievalSetHash,
        address recorder
    );

    modifier onlyOwner() {
        require(
            msg.sender == owner,
            "AEGIS: NOT_OWNER"
        );
        _;
    }

    modifier onlyAuthorizedWriter() {
        require(
            authorizedWriters[msg.sender],
            "AEGIS: WRITER_NOT_AUTHORIZED"
        );
        _;
    }

    constructor() {
        owner = msg.sender;
    }

    function setAuthorizedWriter(
        address writer,
        bool authorized
    ) external onlyOwner {
        require(
            writer != address(0),
            "AEGIS: ZERO_ADDRESS"
        );
        authorizedWriters[writer] = authorized;
        emit AuthorizedWriterUpdated(
            writer,
            authorized
        );
    }

    function recordEvidence(
        bytes32 evidenceHash,
        bytes32 policyVersionHash,
        bytes32 retrievalSetHash,
        bytes32 decisionHash,
        bytes32 relatedPoIHash
    ) external onlyAuthorizedWriter {
        require(
            evidenceHash != bytes32(0),
            "AEGIS: INVALID_EVIDENCE_HASH"
        );
        require(
            !evidenceRecords[evidenceHash].exists,
            "AEGIS: EVIDENCE_ALREADY_EXISTS"
        );

        evidenceRecords[evidenceHash] = EvidenceRecord({
            policyVersionHash: policyVersionHash,
            retrievalSetHash: retrievalSetHash,
            decisionHash: decisionHash,
            relatedPoIHash: relatedPoIHash,
            recorder: msg.sender,
            timestamp: block.timestamp,
            blockNumber: block.number,
            exists: true
        });

        emit EvidenceAnchored(
            evidenceHash,
            relatedPoIHash,
            decisionHash,
            policyVersionHash,
            retrievalSetHash,
            msg.sender
        );
    }

    function exists(
        bytes32 evidenceHash
    ) external view returns (bool) {
        return evidenceRecords[evidenceHash].exists;
    }

    function getEvidence(
        bytes32 evidenceHash
    ) external view returns (
        bytes32 policyVersionHash,
        bytes32 retrievalSetHash,
        bytes32 decisionHash,
        bytes32 relatedPoIHash,
        address recorder,
        uint256 timestamp,
        uint256 blockNumber,
        bool recordExists
    ) {
        EvidenceRecord memory record = evidenceRecords[
            evidenceHash
        ];
        return (
            record.policyVersionHash,
            record.retrievalSetHash,
            record.decisionHash,
            record.relatedPoIHash,
            record.recorder,
            record.timestamp,
            record.blockNumber,
            record.exists
        );
    }
}

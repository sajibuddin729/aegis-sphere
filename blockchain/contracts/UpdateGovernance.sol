// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

/// @title Aegis Update Governance
/// @notice Records independently verified candidate
///         update decisions. This contract does not
///         autonomously deploy production updates.
contract UpdateGovernance {

    address public owner;

    mapping(address => bool)
        public authorizedVerifiers;

    enum Decision {
        NONE,
        APPROVED,
        REJECTED
    }

    struct UpdateRecord {

        bytes32 candidateHash;

        bytes32 reportHash;

        bytes32 attestationHash;

        bytes32 verifierVersionHash;

        address verifier;

        Decision decision;

        uint256 timestamp;

        uint256 blockNumber;

        bool exists;
    }

    mapping(bytes32 => UpdateRecord)
        private updates;

    event VerifierAuthorizationUpdated(
        address indexed verifier,
        bool authorized
    );

    event UpdateDecisionRecorded(
        bytes32 indexed candidateHash,
        bytes32 indexed attestationHash,
        bytes32 indexed reportHash,
        bytes32 verifierVersionHash,
        address verifier,
        Decision decision
    );

    modifier onlyOwner() {

        require(
            msg.sender == owner,
            "AEGIS: NOT_OWNER"
        );

        _;
    }

    modifier onlyVerifier() {

        require(
            authorizedVerifiers[
                msg.sender
            ],
            "AEGIS: VERIFIER_NOT_AUTHORIZED"
        );

        _;
    }

    constructor() {

        owner = msg.sender;
    }

    function setAuthorizedVerifier(
        address verifier,
        bool authorized
    )
        external
        onlyOwner
    {

        require(
            verifier != address(0),
            "AEGIS: ZERO_ADDRESS"
        );

        authorizedVerifiers[
            verifier
        ] = authorized;

        emit VerifierAuthorizationUpdated(
            verifier,
            authorized
        );
    }

    function recordDecision(
        bytes32 candidateHash,
        bytes32 reportHash,
        bytes32 attestationHash,
        bytes32 verifierVersionHash,
        uint8 decisionValue
    )
        external
        onlyVerifier
    {

        require(
            candidateHash != bytes32(0),
            "AEGIS: INVALID_CANDIDATE_HASH"
        );

        require(
            !updates[
                candidateHash
            ].exists,
            "AEGIS: UPDATE_ALREADY_DECIDED"
        );

        require(
            decisionValue == 1
            || decisionValue == 2,
            "AEGIS: INVALID_DECISION"
        );

        Decision decision =
            Decision(
                decisionValue
            );

        updates[
            candidateHash
        ] = UpdateRecord({

            candidateHash:
                candidateHash,

            reportHash:
                reportHash,

            attestationHash:
                attestationHash,

            verifierVersionHash:
                verifierVersionHash,

            verifier:
                msg.sender,

            decision:
                decision,

            timestamp:
                block.timestamp,

            blockNumber:
                block.number,

            exists:
                true
        });

        emit UpdateDecisionRecorded(
            candidateHash,
            attestationHash,
            reportHash,
            verifierVersionHash,
            msg.sender,
            decision
        );
    }

    function exists(
        bytes32 candidateHash
    )
        external
        view
        returns (bool)
    {

        return updates[
            candidateHash
        ].exists;
    }

    function getDecision(
        bytes32 candidateHash
    )
        external
        view
        returns (
            bytes32 reportHash,
            bytes32 attestationHash,
            bytes32 verifierVersionHash,
            address verifier,
            uint8 decision,
            uint256 timestamp,
            uint256 blockNumber,
            bool recordExists
        )
    {

        UpdateRecord memory record =
            updates[
                candidateHash
            ];

        return (
            record.reportHash,
            record.attestationHash,
            record.verifierVersionHash,
            record.verifier,
            uint8(
                record.decision
            ),
            record.timestamp,
            record.blockNumber,
            record.exists
        );
    }
}

// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;


interface IAegisEligibilityVerifier {

    function verifyProof(
        uint256[2] calldata pA,
        uint256[2][2] calldata pB,
        uint256[2] calldata pC,
        uint256[4] calldata pubSignals
    )
        external
        view
        returns (bool);
}


contract ZKEligibilityGate {

    address public owner;

    IAegisEligibilityVerifier
        public immutable verifier;


    mapping(bytes32 => bool)
        public registeredSnapshots;

    mapping(bytes32 => bool)
        public verifiedProofs;


    event SnapshotRegistered(
        bytes32 indexed snapshotHash,
        uint256 balanceCommitment,
        uint256 riskCommitment
    );


    event EligibilityProofVerified(
        bytes32 indexed proofHash,
        bytes32 indexed snapshotHash,
        bytes32 indexed publicSignalsHash,
        uint256 amountMinor,
        uint256 maxRiskScoreBps
    );


    modifier onlyOwner() {

        require(
            msg.sender == owner,
            "AEGIS: NOT_OWNER"
        );

        _;
    }


    constructor(
        address verifierAddress
    ) {

        require(
            verifierAddress
                != address(0),
            "AEGIS: ZERO_VERIFIER"
        );

        owner = msg.sender;

        verifier =
            IAegisEligibilityVerifier(
                verifierAddress
            );
    }


    function registerSnapshot(
        uint256 balanceCommitment,
        uint256 riskCommitment
    )
        external
        onlyOwner
        returns (bytes32 snapshotHash)
    {

        snapshotHash =
            keccak256(
                abi.encode(
                    balanceCommitment,
                    riskCommitment
                )
            );

        registeredSnapshots[
            snapshotHash
        ] = true;

        emit SnapshotRegistered(
            snapshotHash,
            balanceCommitment,
            riskCommitment
        );
    }


    function verifyAndRecord(
        uint256[2] calldata pA,
        uint256[2][2] calldata pB,
        uint256[2] calldata pC,
        uint256[4] calldata pubSignals
    )
        external
        returns (bytes32 proofHash)
    {

        // Public signal order is frozen as:
        //
        // [0] balanceCommitment
        // [1] riskCommitment
        // [2] amountMinor
        // [3] maxRiskScoreBps

        bytes32 snapshotHash =
            keccak256(
                abi.encode(
                    pubSignals[0],
                    pubSignals[1]
                )
            );

        require(
            registeredSnapshots[
                snapshotHash
            ],
            "AEGIS: SNAPSHOT_NOT_REGISTERED"
        );


        bool valid =
            verifier.verifyProof(
                pA,
                pB,
                pC,
                pubSignals
            );

        require(
            valid,
            "AEGIS: INVALID_ZK_PROOF"
        );


        proofHash =
            keccak256(
                abi.encode(
                    pA,
                    pB,
                    pC,
                    pubSignals
                )
            );


        require(
            !verifiedProofs[
                proofHash
            ],
            "AEGIS: PROOF_ALREADY_RECORDED"
        );


        verifiedProofs[
            proofHash
        ] = true;


        bytes32 publicSignalsHash =
            keccak256(
                abi.encode(
                    pubSignals
                )
            );


        emit EligibilityProofVerified(
            proofHash,
            snapshotHash,
            publicSignalsHash,
            pubSignals[2],
            pubSignals[3]
        );
    }
}

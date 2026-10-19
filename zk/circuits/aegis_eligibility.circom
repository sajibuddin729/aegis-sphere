pragma circom 2.1.0;

include "circomlib/circuits/bitify.circom";
include "circomlib/circuits/comparators.circom";
include "circomlib/circuits/poseidon.circom";


template AegisEligibility() {

    // Private witness values unless explicitly
    // declared public in main.
    signal input balance;
    signal input balanceSalt;

    signal input riskScoreBps;
    signal input riskSalt;

    // Public transaction constraints.
    signal input amountMinor;
    signal input maxRiskScoreBps;

    // Public cryptographic commitments.
    signal output balanceCommitment;
    signal output riskCommitment;


    // -------------------------------
    // Range constraints
    // -------------------------------

    component balanceBits = Num2Bits(64);
    balanceBits.in <== balance;

    component amountBits = Num2Bits(64);
    amountBits.in <== amountMinor;

    component riskBits = Num2Bits(14);
    riskBits.in <== riskScoreBps;

    component maxRiskBits = Num2Bits(14);
    maxRiskBits.in <== maxRiskScoreBps;


    // -------------------------------
    // amount > 0
    // -------------------------------

    component amountZero = IsZero();
    amountZero.in <== amountMinor;
    amountZero.out === 0;


    // -------------------------------
    // risk values must remain BPS
    // -------------------------------

    component riskBound = LessEqThan(14);
    riskBound.in[0] <== riskScoreBps;
    riskBound.in[1] <== 10000;
    riskBound.out === 1;

    component maxRiskBound = LessEqThan(14);
    maxRiskBound.in[0] <== maxRiskScoreBps;
    maxRiskBound.in[1] <== 10000;
    maxRiskBound.out === 1;


    // -------------------------------
    // Eligibility constraints
    // -------------------------------

    component sufficientBalance = GreaterEqThan(64);

    sufficientBalance.in[0] <== balance;
    sufficientBalance.in[1] <== amountMinor;

    sufficientBalance.out === 1;


    component acceptableRisk = LessEqThan(14);

    acceptableRisk.in[0] <== riskScoreBps;
    acceptableRisk.in[1] <== maxRiskScoreBps;

    acceptableRisk.out === 1;


    // -------------------------------
    // Poseidon commitments
    // -------------------------------

    component balanceHasher = Poseidon(2);

    balanceHasher.inputs[0] <== balance;
    balanceHasher.inputs[1] <== balanceSalt;

    balanceCommitment <==
        balanceHasher.out;


    component riskHasher = Poseidon(2);

    riskHasher.inputs[0] <== riskScoreBps;
    riskHasher.inputs[1] <== riskSalt;

    riskCommitment <==
        riskHasher.out;
}


component main {
    public [
        amountMinor,
        maxRiskScoreBps
    ]
} = AegisEligibility();

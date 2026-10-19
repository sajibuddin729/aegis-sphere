CREATE TABLE IF NOT EXISTS university_governance_decisions (
    candidate_hash TEXT PRIMARY KEY,
    candidate_id TEXT NOT NULL,
    verification_id TEXT NOT NULL,
    source_status TEXT NOT NULL,
    report_hash TEXT NOT NULL,
    attestation_hash TEXT NOT NULL,
    verifier_version_hash TEXT NOT NULL,
    decision TEXT NOT NULL,
    governance_tx_hash TEXT NOT NULL,
    governance_block_number BIGINT NOT NULL,
    chain_verifier TEXT NOT NULL,
    staging_status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS
university_governance_candidate_idx
ON university_governance_decisions(
    candidate_id
);

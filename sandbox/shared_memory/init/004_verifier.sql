CREATE TABLE IF NOT EXISTS university_verification_runs (
    verification_id TEXT PRIMARY KEY,

    candidate_id TEXT NOT NULL,

    candidate_hash TEXT NOT NULL,

    status TEXT NOT NULL,

    schema_pass BOOLEAN NOT NULL DEFAULT FALSE,
    secret_scan_pass BOOLEAN NOT NULL DEFAULT FALSE,
    ast_pass BOOLEAN NOT NULL DEFAULT FALSE,
    import_pass BOOLEAN NOT NULL DEFAULT FALSE,
    capability_pass BOOLEAN NOT NULL DEFAULT FALSE,
    network_pass BOOLEAN NOT NULL DEFAULT FALSE,
    signer_boundary_pass BOOLEAN NOT NULL DEFAULT FALSE,
    static_security_pass BOOLEAN NOT NULL DEFAULT FALSE,
    regression_pass BOOLEAN NOT NULL DEFAULT FALSE,
    behavior_pass BOOLEAN NOT NULL DEFAULT FALSE,

    verification_score NUMERIC(6,2),

    report_hash TEXT,

    attestation_hash TEXT,

    rejection_reason TEXT,

    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);


CREATE TABLE IF NOT EXISTS university_verification_checks (
    id BIGSERIAL PRIMARY KEY,

    verification_id TEXT NOT NULL
        REFERENCES university_verification_runs(verification_id)
        ON DELETE CASCADE,

    check_name TEXT NOT NULL,

    passed BOOLEAN NOT NULL,

    severity TEXT NOT NULL,

    details JSONB NOT NULL,

    evidence_hash TEXT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE(
        verification_id,
        check_name
    )
);


CREATE TABLE IF NOT EXISTS university_verifier_attestations (
    attestation_id TEXT PRIMARY KEY,

    verification_id TEXT NOT NULL
        REFERENCES university_verification_runs(verification_id),

    candidate_id TEXT NOT NULL,

    candidate_hash TEXT NOT NULL,

    report_hash TEXT NOT NULL,

    decision TEXT NOT NULL,

    verifier_version TEXT NOT NULL,

    attestation_hash TEXT NOT NULL UNIQUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE INDEX IF NOT EXISTS
university_verification_candidate_idx
ON university_verification_runs(
    candidate_id,
    started_at DESC
);

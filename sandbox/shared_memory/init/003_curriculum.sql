ALTER TABLE university_memories
ADD COLUMN IF NOT EXISTS retrieval_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE university_memories
ADD COLUMN IF NOT EXISTS successful_reuse_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE university_memories
ADD COLUMN IF NOT EXISTS last_retrieved_at TIMESTAMPTZ;

ALTER TABLE university_memories
ADD COLUMN IF NOT EXISTS effective_utility_score NUMERIC(6,2) NOT NULL DEFAULT 0;

ALTER TABLE university_memories
ADD COLUMN IF NOT EXISTS quarantined BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE university_memories
ADD COLUMN IF NOT EXISTS quarantine_reason TEXT;


UPDATE university_memories
SET effective_utility_score = utility_score
WHERE effective_utility_score = 0;


CREATE TABLE IF NOT EXISTS university_curriculum_runs (
    run_id TEXT PRIMARY KEY,

    status TEXT NOT NULL,

    requested_steps INTEGER NOT NULL,

    completed_steps INTEGER NOT NULL DEFAULT 0,

    average_blue_score NUMERIC(6,2),

    critical_violations INTEGER NOT NULL DEFAULT 0,

    regression_count INTEGER NOT NULL DEFAULT 0,

    summary JSONB,

    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    completed_at TIMESTAMPTZ
);


CREATE TABLE IF NOT EXISTS university_scenario_variants (
    variant_id TEXT PRIMARY KEY,

    base_scenario_id TEXT NOT NULL,

    capability TEXT NOT NULL,

    mode TEXT NOT NULL,

    difficulty INTEGER NOT NULL,

    mutation_index INTEGER NOT NULL,

    variant_hash TEXT NOT NULL UNIQUE,

    scenario JSONB NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS university_capability_baselines (
    capability TEXT PRIMARY KEY,

    baseline_rating NUMERIC(8,2) NOT NULL,

    baseline_score NUMERIC(6,2) NOT NULL,

    baseline_critical_rate NUMERIC(6,4) NOT NULL DEFAULT 0,

    baseline_hash TEXT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS university_curriculum_steps (
    step_id TEXT PRIMARY KEY,

    run_id TEXT NOT NULL
        REFERENCES university_curriculum_runs(run_id)
        ON DELETE CASCADE,

    step_index INTEGER NOT NULL,

    capability TEXT NOT NULL,

    priority_score NUMERIC(8,4) NOT NULL,

    mode TEXT NOT NULL,

    difficulty INTEGER NOT NULL,

    variant_id TEXT NOT NULL
        REFERENCES university_scenario_variants(variant_id),

    match_id TEXT NOT NULL
        REFERENCES university_arena_matches(match_id),

    blue_score NUMERIC(6,2) NOT NULL,

    winner TEXT NOT NULL,

    critical_violation BOOLEAN NOT NULL,

    regression_detected BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE(run_id, step_index)
);


CREATE TABLE IF NOT EXISTS university_regression_events (
    event_id TEXT PRIMARY KEY,

    run_id TEXT NOT NULL
        REFERENCES university_curriculum_runs(run_id),

    step_id TEXT NOT NULL
        REFERENCES university_curriculum_steps(step_id),

    capability TEXT NOT NULL,

    baseline_score NUMERIC(6,2) NOT NULL,

    observed_score NUMERIC(6,2) NOT NULL,

    score_delta NUMERIC(6,2) NOT NULL,

    critical_violation BOOLEAN NOT NULL,

    severity TEXT NOT NULL,

    details JSONB NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS university_capability_snapshots (
    id BIGSERIAL PRIMARY KEY,

    run_id TEXT NOT NULL
        REFERENCES university_curriculum_runs(run_id),

    capability TEXT NOT NULL,

    rating NUMERIC(8,2) NOT NULL,

    recent_average_score NUMERIC(6,2) NOT NULL,

    critical_rate NUMERIC(6,4) NOT NULL,

    priority_score NUMERIC(8,4) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE(run_id, capability)
);


CREATE INDEX IF NOT EXISTS
university_curriculum_steps_capability_idx
ON university_curriculum_steps (
    capability,
    created_at DESC
);

CREATE INDEX IF NOT EXISTS
university_regression_capability_idx
ON university_regression_events (
    capability,
    created_at DESC
);

CREATE INDEX IF NOT EXISTS
university_memory_reuse_idx
ON university_memories (
    approved_for_reuse,
    quarantined,
    effective_utility_score DESC
);

CREATE TABLE IF NOT EXISTS university_arena_matches (
    match_id TEXT PRIMARY KEY,

    scenario_id TEXT NOT NULL,
    scenario_hash TEXT NOT NULL,

    capability TEXT NOT NULL,

    red_agent TEXT NOT NULL,
    blue_agent TEXT NOT NULL,

    red_output_hash TEXT NOT NULL,
    blue_output_hash TEXT NOT NULL,
    critic_output_hash TEXT NOT NULL,

    blue_score NUMERIC(6,2) NOT NULL,
    red_score NUMERIC(6,2) NOT NULL,

    winner TEXT NOT NULL,

    critical_violation BOOLEAN NOT NULL DEFAULT FALSE,

    transcript_hash TEXT NOT NULL,

    outcome JSONB NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS university_agent_ratings (
    agent_role TEXT NOT NULL,

    capability TEXT NOT NULL,

    rating NUMERIC(8,2) NOT NULL DEFAULT 1200,

    matches INTEGER NOT NULL DEFAULT 0,

    wins INTEGER NOT NULL DEFAULT 0,

    losses INTEGER NOT NULL DEFAULT 0,

    draws INTEGER NOT NULL DEFAULT 0,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    PRIMARY KEY (
        agent_role,
        capability
    )
);


CREATE TABLE IF NOT EXISTS university_memory_admissions (
    admission_id TEXT PRIMARY KEY,

    match_id TEXT NOT NULL
        REFERENCES university_arena_matches(match_id)
        ON DELETE CASCADE,

    content_hash TEXT NOT NULL,

    score NUMERIC(6,2) NOT NULL,

    critical_violation BOOLEAN NOT NULL,

    admitted BOOLEAN NOT NULL,

    rejection_reason TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


ALTER TABLE university_candidate_updates
ADD COLUMN IF NOT EXISTS source_match_id TEXT;


ALTER TABLE university_candidate_updates
ADD COLUMN IF NOT EXISTS policy_result TEXT;


ALTER TABLE university_candidate_updates
ADD COLUMN IF NOT EXISTS frozen_reason TEXT;


CREATE INDEX IF NOT EXISTS
university_match_capability_idx
ON university_arena_matches (
    capability,
    created_at DESC
);


CREATE INDEX IF NOT EXISTS
university_rating_idx
ON university_agent_ratings (
    capability,
    rating DESC
);


CREATE INDEX IF NOT EXISTS
university_candidate_status_idx
ON university_candidate_updates (
    status,
    created_at DESC
);

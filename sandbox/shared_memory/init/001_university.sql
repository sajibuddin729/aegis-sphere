CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS university_scenarios (
    scenario_id TEXT PRIMARY KEY,
    scenario_hash TEXT NOT NULL UNIQUE,
    category TEXT NOT NULL,
    mode TEXT NOT NULL,
    difficulty INTEGER NOT NULL,
    title TEXT NOT NULL,
    scenario JSONB NOT NULL,
    constraints JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS university_episodes (
    episode_id TEXT PRIMARY KEY,
    scenario_id TEXT NOT NULL REFERENCES university_scenarios(scenario_id),
    mode TEXT NOT NULL,
    status TEXT NOT NULL,
    final_decision TEXT,
    final_score NUMERIC(6,2),
    critical_violation BOOLEAN NOT NULL DEFAULT FALSE,
    transcript_hash TEXT,
    outcome JSONB,
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS university_agent_turns (
    id BIGSERIAL PRIMARY KEY,
    episode_id TEXT NOT NULL REFERENCES university_episodes(episode_id) ON DELETE CASCADE,
    turn_index INTEGER NOT NULL,
    agent_role TEXT NOT NULL,
    input_hash TEXT NOT NULL,
    output_hash TEXT NOT NULL,
    output JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(episode_id, turn_index, agent_role)
);

CREATE TABLE IF NOT EXISTS university_evaluations (
    episode_id TEXT PRIMARY KEY REFERENCES university_episodes(episode_id) ON DELETE CASCADE,
    correctness_score NUMERIC(6,2) NOT NULL,
    safety_score NUMERIC(6,2) NOT NULL,
    compliance_score NUMERIC(6,2) NOT NULL,
    evidence_score NUMERIC(6,2) NOT NULL,
    cooperation_score NUMERIC(6,2) NOT NULL,
    efficiency_score NUMERIC(6,2) NOT NULL,
    total_score NUMERIC(6,2) NOT NULL,
    critical_violation BOOLEAN NOT NULL,
    weaknesses JSONB NOT NULL,
    strengths JSONB NOT NULL,
    evaluator_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS university_memories (
    memory_id TEXT PRIMARY KEY,
    source_episode_id TEXT REFERENCES university_episodes(episode_id),
    memory_type TEXT NOT NULL,
    category TEXT NOT NULL,
    content TEXT NOT NULL,
    content_hash TEXT NOT NULL UNIQUE,
    utility_score NUMERIC(6,2) NOT NULL DEFAULT 0,
    approved_for_reuse BOOLEAN NOT NULL DEFAULT FALSE,
    embedding vector(1024),
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS university_candidate_updates (
    candidate_id TEXT PRIMARY KEY,
    source_episode_id TEXT REFERENCES university_episodes(episode_id),
    update_type TEXT NOT NULL,
    target_component TEXT NOT NULL,
    title TEXT NOT NULL,
    proposal JSONB NOT NULL,
    proposal_hash TEXT NOT NULL UNIQUE,
    status TEXT NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS university_memory_embedding_idx
ON university_memories USING hnsw (
    embedding vector_cosine_ops
);

CREATE INDEX IF NOT EXISTS university_episode_score_idx
ON university_episodes(final_score DESC);

CREATE INDEX IF NOT EXISTS university_memory_category_idx
ON university_memories(category);

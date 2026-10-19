from functools import lru_cache
import hashlib
from pathlib import Path

from aegis_api.clients.bridge import (
    FiscoBridgeClient,
)
from aegis_api.clients.guard import (
    GuardClient,
)
from aegis_api.clients.llm import (
    LocalQwenClient,
)
from aegis_api.core.settings import (
    get_settings,
)
from aegis_api.clients.rag import (
    RagClient,
)
from aegis_api.clients.embedding import (
    LocalEmbeddingClient,
)
from aegis_api.services.evolution_agent import (
    EvolutionAgent,
)
from aegis_api.services.university_evaluator import (
    UniversityEvaluator,
)
from aegis_api.services.university_memory import (
    UniversityMemoryService,
)
from aegis_api.services.university_orchestrator import (
    UniversityOrchestrator,
)
from aegis_api.services.university_repository import (
    UniversityRepository,
)
from aegis_api.services.university_scenarios import (
    UniversityScenarioRepository,
)
from aegis_api.services.arena_agents import (
    ArenaAgents,
)
from aegis_api.services.arena_judge import (
    ArenaJudge,
)
from aegis_api.services.arena_league import (
    ArenaLeague,
)
from aegis_api.services.arena_orchestrator import (
    ArenaOrchestrator,
)
from aegis_api.services.arena_repository import (
    ArenaRepository,
)
from aegis_api.services.arena_scenarios import (
    ArenaScenarioRepository,
)
from aegis_api.services.candidate_policy import (
    CandidateUpdatePolicy,
)
from aegis_api.services.curriculum_engine import (
    CurriculumEngine,
)
from aegis_api.services.curriculum_mutator import (
    CurriculumMutator,
)
from aegis_api.services.curriculum_repository import (
    CurriculumRepository,
)
from aegis_api.services.curriculum_scheduler import (
    CurriculumScheduler,
)
from aegis_api.services.curriculum_scenarios import (
    CurriculumScenarioRepository,
)
from aegis_api.services.memory_maintenance import (
    MemoryMaintenanceService,
)
from aegis_api.services.regression_detector import (
    RegressionDetector,
)
from aegis_api.services.candidate_behavior_verifier import (
    CandidateBehaviorVerifier,
)
from aegis_api.services.candidate_code_guard import (
    CandidateCodeGuard,
)
from aegis_api.services.candidate_intake import (
    CandidateIntakeService,
)
from aegis_api.services.candidate_regression_verifier import (
    CandidateRegressionVerifier,
)
from aegis_api.services.candidate_schema_verifier import (
    CandidateSchemaVerifier,
)
from aegis_api.services.candidate_static_verifier import (
    CandidateStaticVerifier,
)
from aegis_api.services.candidate_verifier import (
    CandidateVerifier,
)
from aegis_api.services.verification_report import (
    VerificationReportStore,
)
from aegis_api.services.verifier_attestation import (
    VerifierAttestationService,
)


from aegis_api.services.candidate_updates import (
    CandidateUpdateService,
)
from aegis_api.services.elo import (
    EloEngine,
)
from aegis_api.services.memory_admission import (
    ArenaMemoryAdmissionPolicy,
)
from aegis_api.services.audit_anchor import (
    AuditEvidenceAnchor,
)
from aegis_api.services.compliance_reasoner import (
    ComplianceReasoner,
)
from aegis_api.services.regulatory_compliance import (
    RegulatoryCompliancePipeline,
)
from aegis_api.services.china_compliance import (
    ChinaComplianceEngine,
)
from aegis_api.services.safety_gate import (
    IndependentSafetyGate,
)
from aegis_api.providers.actions import (
    AegisActionProvider,
)
from aegis_api.providers.wallet import (
    AegisWalletProvider,
)
from aegis_api.services.autonomous_risk import (
    AutonomousRiskService,
)
from aegis_api.services.compliance import (
    ComplianceService,
)
from aegis_api.services.leader_agent import (
    LeaderFinancialAgent,
)
from aegis_api.services.market_agent import (
    MarketAgent,
)
from aegis_api.services.market_data import (
    MarketDataService,
)
from aegis_api.services.orchestrator import (
    AutonomousFinancialOrchestrator,
)
from aegis_api.services.plan_validator import (
    PlanValidator,
)
from aegis_api.services.poi import (
    PoIService,
)
from aegis_api.services.prompt_security import (
    PromptSecurityGate,
)
from aegis_api.services.recipient import (
    RecipientResolver,
)
from aegis_api.services.risk import (
    RiskService,
)
from aegis_api.services.security_agent import (
    SecurityAgent,
)
from aegis_api.providers.portfolio import (
    AegisPortfolioProvider,
)
from aegis_api.providers.portfolio_actions import (
    PortfolioActionProvider,
)
from aegis_api.services.asset_management_agent import (
    AssetManagementAgent,
)
from aegis_api.services.portfolio_calculator import (
    PortfolioRebalanceCalculator,
)
from aegis_api.services.portfolio_compliance import (
    PortfolioComplianceService,
)
from aegis_api.services.portfolio_orchestrator import (
    AutonomousPortfolioOrchestrator,
)
from aegis_api.services.portfolio_plan_validator import (
    PortfolioPlanValidator,
)
from aegis_api.services.portfolio_poi import (
    PortfolioPoIService,
)
from aegis_api.services.portfolio_risk import (
    PortfolioRiskService,
)
from aegis_api.services.portfolio_security_agent import (
    PortfolioSecurityAgent,
)



@lru_cache
def get_bridge() -> FiscoBridgeClient:
    settings = get_settings()

    return FiscoBridgeClient(
        base_url=(
            settings.AEGIS_BRIDGE_BASE_URL
        ),
        bridge_key=(
            settings.AEGIS_BRIDGE_KEY
        ),
    )


@lru_cache
def get_wallet_provider() -> AegisWalletProvider:
    return AegisWalletProvider(
        get_bridge()
    )


@lru_cache
def get_action_provider() -> AegisActionProvider:
    settings = get_settings()
    root = Path(__file__).resolve().parents[5]
    policy_file = root / settings.AEGIS_EFFECTIVE_POLICY_PATH
    effective_policy_hash = (
        "0x" + hashlib.sha256(policy_file.read_bytes()).hexdigest()
        if policy_file.exists()
        else None
    )

    return AegisActionProvider(
        wallet=get_wallet_provider(),
        risk_service=RiskService(
            max_transaction_minor=(
                settings.AEGIS_MAX_TRANSACTION_MINOR
            ),
            max_daily_minor=(
                settings.AEGIS_MAX_DAILY_MINOR
            ),
            max_risk_bps=(
                settings.AEGIS_MAX_RISK_BPS
            ),
        ),
        poi_service=PoIService(
            policy_version=(
                settings.AEGIS_POLICY_VERSION
            ),
            policy_hash=effective_policy_hash,
        ),
        asset_symbol=(
            settings.AEGIS_TOKEN_SYMBOL
        ),
    )


@lru_cache
def get_llm() -> LocalQwenClient:
    settings = get_settings()
    return LocalQwenClient(
        base_url=settings.AEGIS_LLM_BASE_URL,
        model=settings.AEGIS_LLM_MODEL,
    )


@lru_cache
def get_rag_client() -> RagClient:
    settings = get_settings()
    return RagClient(
        base_url=(
            settings.AEGIS_RAG_BASE_URL
        ),
        rag_key=(
            settings.AEGIS_RAG_KEY
        ),
    )


@lru_cache
def get_regulatory_compliance() -> RegulatoryCompliancePipeline:
    settings = get_settings()
    root = Path(__file__).resolve().parents[5]
    policy_file = root / settings.AEGIS_EFFECTIVE_POLICY_PATH
    effective_policy_hash = (
        "0x" + hashlib.sha256(policy_file.read_bytes()).hexdigest()
        if policy_file.exists()
        else None
    )
    return RegulatoryCompliancePipeline(
        rag=(
            get_rag_client()
        ),
        reasoner=(
            ComplianceReasoner(
                get_llm()
            )
        ),
        evidence_dir=Path(
            settings.AEGIS_COMPLIANCE_EVIDENCE_DIR
        ),
        effective_policy_hash=effective_policy_hash,
    )


@lru_cache
def get_audit_anchor() -> AuditEvidenceAnchor:
    return AuditEvidenceAnchor(
        get_bridge()
    )


@lru_cache
def get_orchestrator() -> AutonomousFinancialOrchestrator:
    settings = get_settings()
    root = Path(__file__).resolve().parents[5]
    llm = get_llm()

    return AutonomousFinancialOrchestrator(
        leader=LeaderFinancialAgent(llm),
        market_agent=MarketAgent(llm),
        security_agent=SecurityAgent(llm),
        wallet=get_wallet_provider(),
        actions=get_action_provider(),
        risk=AutonomousRiskService(
            max_transaction_minor=(
                settings.AEGIS_MAX_TRANSACTION_MINOR
            )
        ),
        compliance=ComplianceService(),
        prompt_security=PromptSecurityGate(),
        validator=PlanValidator(),
        market_data=MarketDataService(
            root / "data" / "market" / "current.json"
        ),
        recipients=RecipientResolver(
            root / "data" / "mock" / "recipients.json"
        ),
        max_onchain_risk_bps=settings.AEGIS_MAX_RISK_BPS,
        leader_agent_id=settings.AEGIS_LEADER_AGENT_ID,
        model_version=settings.AEGIS_LEADER_MODEL_VERSION,
        safety_gate=get_safety_gate(),
        china_compliance=get_china_compliance(),
        regulatory_compliance=(
            get_regulatory_compliance()
        ),
        audit_anchor=(
            get_audit_anchor()
        ),
    )


@lru_cache
def get_guard_client() -> GuardClient:
    settings = get_settings()
    return GuardClient(
        base_url=(
            settings.AEGIS_GUARD_BASE_URL
        ),
        guard_key=(
            settings.AEGIS_GUARD_KEY
        ),
    )


@lru_cache
def get_safety_gate() -> IndependentSafetyGate:
    return IndependentSafetyGate(
        get_guard_client()
    )


@lru_cache
def get_china_compliance() -> ChinaComplianceEngine:
    settings = get_settings()
    root = Path(__file__).resolve().parents[5]
    return ChinaComplianceEngine(
        policy_path=root / settings.AEGIS_CN_POLICY_PATH,
        rules_path=root / settings.AEGIS_CN_RULES_PATH,
    )


@lru_cache
def get_portfolio_provider() -> AegisPortfolioProvider:
    return AegisPortfolioProvider(
        get_bridge()
    )


@lru_cache
def get_portfolio_orchestrator() -> AutonomousPortfolioOrchestrator:
    settings = get_settings()
    llm = get_llm()
    root = Path(__file__).resolve().parents[5]
    policy_file = root / settings.AEGIS_EFFECTIVE_POLICY_PATH
    effective_policy_hash = (
        "0x" + hashlib.sha256(policy_file.read_bytes()).hexdigest()
        if policy_file.exists()
        else None
    )

    portfolio = get_portfolio_provider()

    portfolio_actions = PortfolioActionProvider(
        portfolio=portfolio,
        poi_service=PortfolioPoIService(
            policy_version=(
                settings.AEGIS_POLICY_VERSION
            ),
            policy_hash=effective_policy_hash,
        ),
        leader_agent_id=(
            settings.AEGIS_LEADER_AGENT_ID
        ),
        model_version=(
            settings.AEGIS_LEADER_MODEL_VERSION
        ),
    )

    return AutonomousPortfolioOrchestrator(
        portfolio=portfolio,
        actions=portfolio_actions,
        asset_agent=AssetManagementAgent(llm),
        market_agent=MarketAgent(llm),
        security_agent=PortfolioSecurityAgent(llm),
        market_data=MarketDataService(
            root / "data" / "market" / "current.json"
        ),
        prompt_security=PromptSecurityGate(),
        validator=PortfolioPlanValidator(),
        calculator=PortfolioRebalanceCalculator(),
        risk=PortfolioRiskService(
            max_trade_notional=(
                settings.AEGIS_PORTFOLIO_MAX_TRADE_NOTIONAL
            )
        ),
        compliance=PortfolioComplianceService(),
        max_onchain_risk_bps=settings.AEGIS_PORTFOLIO_MAX_RISK_BPS,
        safety_gate=get_safety_gate(),
        china_compliance=get_china_compliance(),
        regulatory_compliance=(
            get_regulatory_compliance()
        ),
        audit_anchor=(
            get_audit_anchor()
        ),
    )


@lru_cache
def get_embedding_client() -> LocalEmbeddingClient:
    settings = get_settings()
    return LocalEmbeddingClient(
        base_url=(
            settings.AEGIS_RAG_BASE_URL
        ),
        rag_key=(
            settings.AEGIS_RAG_KEY
        ),
    )


@lru_cache
def get_university_repository() -> UniversityRepository:
    settings = get_settings()
    return UniversityRepository(
        settings.AEGIS_MEMORY_DATABASE_URL
    )


@lru_cache
def get_university_memory() -> UniversityMemoryService:
    settings = get_settings()
    return UniversityMemoryService(
        database_url=(
            settings.AEGIS_MEMORY_DATABASE_URL
        ),
        embedding=(
            get_embedding_client()
        ),
        top_k=(
            settings.AEGIS_UNIVERSITY_MEMORY_TOP_K
        ),
    )


@lru_cache
def get_university_orchestrator() -> UniversityOrchestrator:
    settings = get_settings()
    return UniversityOrchestrator(
        llm=get_llm(),
        scenarios=(
            UniversityScenarioRepository(
                Path.cwd()
                / "sandbox"
                / "simulations"
                / "university"
                / "scenarios-v1.json"
            )
        ),
        repository=(
            get_university_repository()
        ),
        memory=(
            get_university_memory()
        ),
        evaluator=(
            UniversityEvaluator()
        ),
        evolution_agent=(
            EvolutionAgent(
                get_llm()
            )
        ),
        pass_score=(
            settings.AEGIS_UNIVERSITY_PASS_SCORE
        ),
        memory_min_score=(
            settings.AEGIS_UNIVERSITY_MEMORY_MIN_SCORE
        ),
    )


@lru_cache
def get_arena_scenarios() -> ArenaScenarioRepository:
    return ArenaScenarioRepository(
        Path.cwd()
        / "sandbox"
        / "simulations"
        / "arena"
        / "arena-v1.json"
    )


@lru_cache
def get_arena_repository() -> ArenaRepository:
    settings = get_settings()
    return ArenaRepository(
        settings.AEGIS_MEMORY_DATABASE_URL
    )


@lru_cache
def get_candidate_update_service() -> CandidateUpdateService:
    return CandidateUpdateService(
        repository=get_arena_repository(),
        policy=CandidateUpdatePolicy(),
        proposed_dir=(
            Path.cwd()
            / "sandbox"
            / "candidate_updates"
            / "university"
        ),
        frozen_dir=(
            Path.cwd()
            / "sandbox"
            / "frozen_updates"
            / "university"
        ),
    )


@lru_cache
def get_arena_orchestrator() -> ArenaOrchestrator:
    return ArenaOrchestrator(
        scenarios=get_arena_scenarios(),
        agents=ArenaAgents(get_llm()),
        judge=ArenaJudge(),
        elo=EloEngine(k_factor=32.0),
        repository=get_arena_repository(),
        memory=get_university_memory(),
        memory_policy=ArenaMemoryAdmissionPolicy(),
        evolution_agent=EvolutionAgent(get_llm()),
        candidates=get_candidate_update_service(),
    )


@lru_cache
def get_arena_league() -> ArenaLeague:
    return ArenaLeague(
        scenarios=get_arena_scenarios(),
        orchestrator=get_arena_orchestrator(),
    )


@lru_cache
def get_curriculum_repository() -> CurriculumRepository:
    settings = get_settings()
    return CurriculumRepository(
        settings.AEGIS_MEMORY_DATABASE_URL
    )


@lru_cache
def get_memory_maintenance() -> MemoryMaintenanceService:
    return MemoryMaintenanceService(
        memory_service=(
            get_university_memory()
        ),
        policy=(
            MemoryUtilityPolicy()
        ),
    )


@lru_cache
def get_curriculum_engine() -> CurriculumEngine:
    return CurriculumEngine(
        seeds=(
            CurriculumScenarioRepository(
                Path.cwd()
                / "sandbox"
                / "simulations"
                / "curriculum"
                / "curriculum-seeds-v1.json"
            )
        ),
        scheduler=(
            CurriculumScheduler()
        ),
        mutator=(
            CurriculumMutator()
        ),
        repository=(
            get_curriculum_repository()
        ),
        arena=(
            get_arena_orchestrator()
        ),
        regression=(
            RegressionDetector()
        ),
        memory_maintenance=(
            get_memory_maintenance()
        ),
    )


@lru_cache
def get_evolution_agent() -> EvolutionAgent:
    return EvolutionAgent(get_llm())


@lru_cache
def get_candidate_verifier() -> CandidateVerifier:
    root = Path.cwd()
    return CandidateVerifier(
        intake=(
            CandidateIntakeService(
                candidate_dirs=[
                    root
                    / "sandbox"
                    / "candidate_updates"
                    / "university-v2",
                    root
                    / "sandbox"
                    / "candidate_updates"
                    / "university",
                    root
                    / "sandbox"
                    / "frozen_updates"
                    / "university",
                    root
                    / "sandbox"
                    / "verification"
                    / "reference",
                ]
            )
        ),
        schema=(
            CandidateSchemaVerifier()
        ),
        static=(
            CandidateStaticVerifier()
        ),
        code_guard=(
            CandidateCodeGuard()
        ),
        behavior=(
            CandidateBehaviorVerifier()
        ),
        regression=(
            CandidateRegressionVerifier()
        ),
        report_store=(
            VerificationReportStore(
                root
                / "sandbox"
                / "verification"
                / "reports"
            )
        ),
        attestation=(
            VerifierAttestationService()
        ),
        project_root=root,
    )







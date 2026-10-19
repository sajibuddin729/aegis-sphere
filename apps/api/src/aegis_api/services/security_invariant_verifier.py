import json
from pathlib import Path
from typing import Any


class SecurityInvariantVerifier:

    def __init__(self, invariants_file: Path | None = None):
        if invariants_file is None:
            invariants_file = Path("sandbox/verification/reference/security-invariants-v1.json")
        self.invariants_file = Path(invariants_file)

    def load_invariants(self) -> tuple[list[str], dict[str, str]]:
        if not self.invariants_file.exists():
            return [], {}
        data = json.loads(self.invariants_file.read_text(encoding="utf-8"))
        return data.get("invariants", []), data.get("invariant_ids", {})

    def verify(self, proposal: Any) -> tuple[bool, list[str]]:
        invariants_list, invariant_ids = self.load_invariants()
        candidate_invariants = getattr(proposal, "security_invariants", None)

        # For V1 proposals that don't have security_invariants attribute
        if candidate_invariants is None:
            # V1 legacy proposals pass with info
            return True, []

        if len(candidate_invariants) < 3:
            return False, ["INSUFFICIENT_SECURITY_INVARIANTS"]

        valid_ids = set(invariant_ids.keys())
        valid_texts = {inv.lower() for inv in invariants_list}

        invalid = []
        matched_count = 0
        for item in candidate_invariants:
            item_str = str(item).strip()
            # Check if recognized invariant ID (e.g. INV-01 or INV-01: description)
            id_prefix = item_str.split(":")[0].strip().upper()
            if id_prefix in valid_ids or item_str.upper() in valid_ids:
                matched_count += 1
                continue
            # Check if text matches known invariant statement
            item_lower = item_str.lower()
            if any(item_lower in vt or vt in item_lower for vt in valid_texts):
                matched_count += 1
                continue
            invalid.append(item_str)

        if invalid:
            return False, [f"UNRECOGNIZED_INVARIANT:{x}" for x in invalid]

        if matched_count < 3:
            return False, ["INSUFFICIENT_MATCHED_INVARIANTS"]

        return True, []

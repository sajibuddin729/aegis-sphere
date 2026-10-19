import re
import torch
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
)


class QwenGuardEngine:

    SAFETY_PATTERN = re.compile(
        r"Safety:\s*"
        r"(Safe|Unsafe|Controversial)",
        re.IGNORECASE,
    )

    CATEGORY_PATTERN = re.compile(
        r"Categories:\s*(.+)",
        re.IGNORECASE,
    )

    REFUSAL_PATTERN = re.compile(
        r"Refusal:\s*(Yes|No)",
        re.IGNORECASE,
    )

    def __init__(
        self,
        model_name: str,
    ):
        self.model_name = model_name

        self.tokenizer = (
            AutoTokenizer
            .from_pretrained(
                model_name
            )
        )

        self.model = (
            AutoModelForCausalLM
            .from_pretrained(
                model_name,
                torch_dtype="auto",
            )
        )

        if torch.backends.mps.is_available():
            self.device = torch.device(
                "mps"
            )
        else:
            self.device = torch.device(
                "cpu"
            )

        self.model.to(
            self.device
        )
        self.model.eval()

    @torch.inference_mode()
    def classify(
        self,
        content: str,
    ) -> dict:

        messages = [
            {
                "role": "user",
                "content": content,
            }
        ]

        text = (
            self.tokenizer
            .apply_chat_template(
                messages,
                tokenize=False,
                add_generation_prompt=True,
            )
        )

        inputs = (
            self.tokenizer(
                text,
                return_tensors="pt",
            )
        )

        inputs = {
            key: value.to(
                self.device
            )
            for key, value in inputs.items()
        }

        output_ids = (
            self.model.generate(
                **inputs,
                max_new_tokens=100,
                do_sample=False,
            )
        )

        generated = output_ids[
            :, inputs[
                "input_ids"
            ].shape[1]:
        ]

        output = (
            self.tokenizer
            .batch_decode(
                generated,
                skip_special_tokens=True,
            )[0]
            .strip()
        )

        return self._parse(
            output
        )

    def _parse(
        self,
        output: str,
    ) -> dict:

        safety_match = (
            self.SAFETY_PATTERN
            .search(output)
        )

        if not safety_match:
            raise RuntimeError(
                "GUARD_OUTPUT_UNPARSEABLE"
            )

        safety = (
            safety_match
            .group(1)
            .capitalize()
        )

        category_match = (
            self.CATEGORY_PATTERN
            .search(output)
        )

        categories = []

        if category_match:
            categories = [
                value.strip()
                for value in category_match
                .group(1)
                .split(",")
                if value.strip()
                and value.strip().lower()
                != "none"
            ]

        refusal_match = (
            self.REFUSAL_PATTERN
            .search(output)
        )

        refusal = (
            refusal_match
            .group(1)
            if refusal_match
            else None
        )

        blocked = (
            safety == "Unsafe"
        )

        return {
            "safety": safety,
            "categories": categories,
            "refusal": refusal,
            "blocked": blocked,
            "raw_label": output,
        }

import asyncio
import json
from typing import TypeVar
import httpx
from pydantic import BaseModel

T = TypeVar(
    "T",
    bound=BaseModel,
)


class LLMError(RuntimeError):
    pass


class LocalQwenClient:
    def __init__(
        self,
        *,
        base_url: str,
        model: str,
        timeout_seconds: float = 120.0,
    ):
        self.base_url = (
            base_url.rstrip("/")
        )
        self.model = model
        self.timeout = timeout_seconds

    async def structured(
        self,
        *,
        system_prompt: str,
        user_prompt: str,
        response_model: type[T],
        temperature: float = 0.1,
    ) -> T:
        schema = (
            response_model.model_json_schema()
        )
        payload = {
            "model": self.model,
            "messages": [
                {
                    "role": "system",
                    "content": system_prompt,
                },
                {
                    "role": "user",
                    "content": (
                        user_prompt + "\n\n/no_think"
                    ),
                },
            ],
            "temperature": temperature,
            "max_tokens": 1000,
            "response_format": {
                "type": "json_schema",
                "json_schema": {
                    "name": response_model.__name__,
                    "schema": schema,
                },
            },
        }

        last_error = None
        for attempt in range(3):
            try:
                async with httpx.AsyncClient(
                    timeout=self.timeout
                ) as client:
                    response = await client.post(
                        (
                            self.base_url + "/chat/completions"
                        ),
                        json=payload,
                    )
                if response.status_code >= 400:
                    raise LLMError(
                        "Local Qwen returned "
                        f"{response.status_code}: "
                        f"{response.text}"
                    )
                body = response.json()
                content = (
                    body["choices"][0]
                    ["message"]["content"]
                )
                parsed = json.loads(content)
                return response_model(**parsed)
            except Exception as exc:
                last_error = exc
                if attempt < 2:
                    await asyncio.sleep(1.0 * (attempt + 1))

        raise LLMError(
            f"Local Qwen call failed after 3 attempts: {last_error}"
        ) from last_error


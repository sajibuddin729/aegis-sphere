import asyncio


class CandidateRegressionVerifier:

    async def run(
        self,
        *,
        project_root: str,
    ) -> dict:

        process = await asyncio.create_subprocess_exec(
            ".venv/bin/pytest",
            "-q",
            "tests/unit",

            cwd=project_root,

            stdout=(
                asyncio
                .subprocess
                .PIPE
            ),

            stderr=(
                asyncio
                .subprocess
                .STDOUT
            ),
        )

        stdout, _ = (
            await process
            .communicate()
        )

        output = stdout.decode(
            "utf-8",
            errors="replace",
        )

        return {
            "passed":
                process.returncode
                == 0,

            "returncode":
                process.returncode,

            "output_tail":
                output[-4000:],
        }

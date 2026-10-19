import ast


class CandidateCodeGuard:

    def inspect_data_only(
        self,
        text: str,
    ) -> tuple[bool, list[str]]:

        findings = []

        try:
            tree = ast.parse(
                text
            )

        except SyntaxError:
            # Ordinary prose/policy text
            # is not executable Python.
            return True, []

        prohibited_nodes = (
            ast.Import,
            ast.ImportFrom,
            ast.FunctionDef,
            ast.AsyncFunctionDef,
            ast.ClassDef,
            ast.Call,
            ast.Lambda,
            ast.With,
            ast.AsyncWith,
        )

        for node in ast.walk(
            tree
        ):

            if isinstance(
                node,
                prohibited_nodes,
            ):
                findings.append(
                    node.__class__.__name__
                )

        return (
            len(findings) == 0,
            sorted(
                set(findings)
            ),
        )

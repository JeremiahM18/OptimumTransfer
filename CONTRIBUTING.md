# Contributing

## Local expectations

Before opening a pull request, run:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/compile.ps1
powershell -ExecutionPolicy Bypass -File scripts/test.ps1
```

## Change guidelines

- Keep commits focused and descriptive.
- Add or update tests for behavior changes.
- Document user-facing workflow changes in `README.md`.
- Prefer small refactors over broad rewrites unless the change is explicitly planned.

## Review checklist

- The project compiles locally.
- Tests pass locally.
- Generated artifacts are not committed.
- Documentation matches the current behavior.
- New solver behavior is covered by at least one regression test.

## Suggested commit style

Use concise, imperative commit messages such as:

- `Restructure project layout and add build scripts`
- `Add automated tests and harden solver behavior`
- `Refresh project documentation and roadmap`

## Future improvements

As the project grows, consider adding a formal branching strategy, release process, and code ownership rules.

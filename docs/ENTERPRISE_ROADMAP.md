# Enterprise Roadmap

## What enterprise-level means here

For this project, “enterprise-level” should mean more than style cleanup. It should become easy to run repeatedly, test automatically, extend safely, and operate with confidence as the codebase grows.

## Priority 1: Delivery discipline

- Add CI that compiles and runs tests on every push and pull request.
- Require small, descriptive commits and change review.
- Add a contribution guide and issue templates.
- Publish a clear support matrix for Java version and operating systems.

## Priority 2: Architecture hardening

- Introduce namespaced packages such as `com.optimumtransfer.core`.
- Split CLI orchestration from the solver engine.
- Add request/response models so the engine can be reused by a UI, API, or batch job.
- Replace ad hoc lambdas in `Main` with named policies or factories.

## Priority 3: Reliability and observability

- Add structured logging instead of `System.out.println` for operational paths.
- Record timing, explored states, and branch counts for solver runs.
- Standardize error handling and user-facing failure messages.
- Add deterministic fixtures for benchmark and regression scenarios.

## Priority 4: Testing depth

- Keep the lightweight regression suite and expand coverage around solver edge cases.
- Add scenario tests for the interactive flow.
- Add GUI smoke tests if the Swing interface remains part of the product.
- Add mutation or property-based testing for state transitions and parser logic.

## Priority 5: Packaging and distribution

- Introduce a standard build tool and dependency management when the environment allows it.
- Produce a distributable artifact or runnable package.
- Add release notes and versioning conventions.
- Provide sample configuration files and canned demo scenarios.

## Suggested near-term backlog

1. Add CI and contributor standards.
2. Extract a reusable solver service from `app.Main`.
3. Namespace the packages.
4. Add configuration-file input.
5. Expand tests around solver metrics and edge cases.

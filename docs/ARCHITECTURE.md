# Architecture Overview

## Runtime options

### Interactive mode

1. `com.optimumtransfer.app.Main` gathers input from the user.
2. The CLI builds a `SolverRequest`.
3. `com.optimumtransfer.application.SolverService` executes the request.
4. The CLI prints, visualizes, or exports the result.

### Configuration-driven mode

1. `com.optimumtransfer.app.ConfigMain` loads a `.properties` file.
2. `com.optimumtransfer.application.config.SolverRequestPropertiesLoader` builds a `SolverRequest`.
3. `SolverService` executes the request.
4. Results are printed in a deterministic batch-friendly format.

## Layering

### Application layer

- `SolverRequest` is the input contract for a solver run.
- `SolverResult` is the output contract.
- `SolverService` is the reusable orchestration boundary.
- `SolverRequestPropertiesLoader` adapts `.properties` files into requests.

### Domain layer

- `model.Container` stores mutable container data for direct object usage.
- `model.State` stores immutable search-state snapshots.
- `model.Transfer` and `model.MoveResult` capture transitions.

### Search layer

- `search.AStar` generates legal neighbor states and searches them.
- `search.Node` carries cumulative path and cost metadata.
- Heuristics live behind the `heuristics.Heuristic` interface.

### Policy layer

- `goals.GoalCondition` abstracts success criteria.
- `constraints.TransferConstraint` abstracts allowed or forbidden moves.
- `goals.SimpleGoalParser` converts lightweight expressions into goal conditions.

### Presentation layer

- `app.Main` is the interactive shell.
- `app.ConfigMain` is the batch entry point.
- `visualization.Visualizer` renders step-by-step console output.
- `visualization.TransferGUI` plays back a chosen solution in Swing.

## Design strengths

- Core search state is immutable, which keeps equality and hashing safe.
- Goals, heuristics, and constraints are pluggable interfaces.
- The solver can now be reused outside the interactive CLI.
- Reproducible `.properties` scenarios support repeatable runs.
- The codebase uses a conventional source/test layout and a stable namespace.

## Current architectural risks

- Logging is still print-based instead of structured.
- Export and visualization behavior is not yet abstracted behind ports or adapters.
- The config format is intentionally lightweight and not yet schema-validated.
- GUI coverage is still manual.

## Recommended next refactors

1. Introduce structured logging and solver metrics.
2. Extract export and visualization behind adapter interfaces.
3. Add schema validation for scenario files.
4. Package the application for easier internal distribution.
5. Expand scenario-driven tests around CLI and config entry points.

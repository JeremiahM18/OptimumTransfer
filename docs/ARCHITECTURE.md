# Architecture Overview

## Runtime flow

1. `app.Main` gathers problem input from the user.
2. A goal strategy and optional heuristic are selected.
3. Any transfer constraints are collected.
4. `search.AStar` explores the state space.
5. The resulting path is printed, visualized, or exported.

## Layering

### Domain layer

- `model.Container` stores mutable container data for direct object usage.
- `model.State` stores immutable search-state snapshots.
- `model.Transfer` and `model.MoveResult` capture transitions.

### Search layer

- `search.AStar` generates legal neighbor states.
- `search.Node` carries cumulative path and cost metadata.
- Heuristics live behind the `heuristics.Heuristic` interface.

### Policy layer

- `goals.GoalCondition` abstracts success criteria.
- `constraints.TransferConstraint` abstracts allowed or forbidden moves.
- `goals.SimpleGoalParser` converts lightweight expressions into goal conditions.

### Presentation layer

- `app.Main` is the interactive shell.
- `visualization.Visualizer` renders step-by-step console output.
- `visualization.TransferGUI` plays back a chosen solution in Swing.

## Design strengths

- Core search state is immutable, which keeps equality and hashing safe.
- Goals, heuristics, and constraints are pluggable interfaces.
- The solver can be exercised without the GUI.
- The codebase is now organized in a conventional source/test layout.

## Current architectural risks

- Package names are still very generic and should be namespaced.
- UI, orchestration, and solver configuration are tightly coupled in `app.Main`.
- There is no service boundary for non-interactive use such as APIs or batch jobs.
- Export and visualization behavior is not yet abstracted behind ports or adapters.
- Logging is print-based instead of structured.

## Recommended refactor path

1. Introduce an application service that accepts a request object and returns a solver result.
2. Move interactive prompting into dedicated CLI classes.
3. Rename packages under a stable company or product namespace.
4. Add a configuration file format for reproducible runs.
5. Treat GUI and file export as optional adapters over the same solver service.

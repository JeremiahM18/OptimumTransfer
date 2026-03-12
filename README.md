# Optimum Transfer

Optimum Transfer is a Java 21 search engine for solving constrained container-transfer problems. It models container volumes as immutable states and uses A* search to find valid transfer sequences that satisfy a chosen goal condition.

## Why this project exists

The project explores state-space search for liquid transfer puzzles and related optimization problems. It supports configurable goal conditions, optional heuristics, transfer constraints, console playback, and a Swing-based visualization mode.

## Current capabilities

- Solve for the shortest transfer sequence with A* search
- Enumerate valid solutions up to a depth limit
- Support exact-match, single-container, even-distribution, and expression-driven goals
- Add custom transfer constraints at runtime
- Visualize solutions in the console or Swing UI
- Export solution steps to `transfer_log.txt`
- Run automated tests with the included JDK-only harness

## Project structure

```text
src/main/java/app             Interactive CLI entry point
src/main/java/constraints     Transfer rule interfaces
src/main/java/goals           Goal-condition implementations and parser
src/main/java/heuristics      Heuristic strategies for A*
src/main/java/model           Core domain objects
src/main/java/search          Search engine implementation
src/main/java/visualization   Console and Swing visualizers
src/test/java/tests           Automated regression tests
scripts/compile.ps1           Local compile script
scripts/test.ps1              Local test script
```

## Prerequisites

- Java 21 or newer
- PowerShell

## Build and test

Compile the application:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/compile.ps1
```

Run the automated test suite:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/test.ps1
```

Run the CLI after compiling:

```powershell
java -cp build/classes/main app.Main
```

## Architecture overview

- `model.State` is an immutable snapshot of container volumes.
- `model.Transfer` describes a move and its cost.
- `goals.GoalCondition` defines whether a state satisfies the target.
- `heuristics.Heuristic` estimates remaining work for A*.
- `constraints.TransferConstraint` blocks invalid or restricted moves.
- `search.AStar` owns shortest-path search and bounded solution enumeration.
- `visualization` contains output helpers for console and GUI playback.

More detail is available in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Quality status

The repository now includes repeatable local build and test scripts plus regression coverage for:

- model immutability and validation
- expression parsing behavior
- constrained solving behavior
- distinct-solution enumeration

## Enterprise-level next steps

The biggest gaps between the current codebase and a production-ready internal tool are documented in [docs/ENTERPRISE_ROADMAP.md](docs/ENTERPRISE_ROADMAP.md). The short version is:

- introduce configuration-driven execution instead of interactive-only CLI input
- adopt package namespacing such as `com.optimumtransfer.*`
- add standardized logging, metrics, and error reporting
- separate the solver engine from UI concerns more aggressively
- add CI quality gates and contribution standards

## Known limitations

- The application is currently driven through an interactive console flow.
- There is no packaged distribution artifact yet.
- GUI coverage is still manual.
- The custom test harness is intentionally lightweight and JDK-only.

## License

No license file is currently included in the repository.

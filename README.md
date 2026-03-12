# Optimum Transfer

Optimum Transfer is a Java 21 search engine for solving constrained container-transfer problems. It models container volumes as immutable states, uses A* search to find valid transfer sequences, and now supports both interactive and configuration-driven execution.

## Current capabilities

- Solve for the shortest transfer sequence with A* search
- Enumerate valid solutions up to a depth limit or without a depth cap
- Support exact-match, single-container, even-distribution, and expression-driven goals
- Add transfer constraints at runtime or through configuration
- Run through an interactive CLI or a `.properties` scenario file
- Visualize solutions in the console or Swing UI
- Export interactive shortest-path results to `transfer_log.txt`
- Run automated regression tests with the included JDK-only harness

## Project structure

```text
src/main/java/com/optimumtransfer/app                 Interactive and config entry points
src/main/java/com/optimumtransfer/application         Service-layer request/response models
src/main/java/com/optimumtransfer/application/config  Properties-based request loading
src/main/java/com/optimumtransfer/constraints         Transfer rule interfaces
src/main/java/com/optimumtransfer/goals               Goal-condition implementations and parser
src/main/java/com/optimumtransfer/heuristics          Heuristic strategies for A*
src/main/java/com/optimumtransfer/model               Core domain objects
src/main/java/com/optimumtransfer/search              Search engine implementation
src/main/java/com/optimumtransfer/visualization       Console and Swing visualizers
src/test/java/com/optimumtransfer/tests               Automated regression tests
config/examples                                       Sample reproducible scenarios
scripts/compile.ps1                                   Local compile script
scripts/test.ps1                                      Local test script
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

## Run the interactive CLI

```powershell
java -cp build/classes/main com.optimumtransfer.app.Main
```

## Run a reproducible scenario file

Example scenario: [config/examples/exact-match.properties](config/examples/exact-match.properties)

```powershell
java -cp build/classes/main com.optimumtransfer.app.ConfigMain config/examples/exact-match.properties
```

Supported configuration keys include:

- `capacities`
- `startVolumes`
- `solveMode`
- `maxDepth`
- `goal.type`
- `goal.targetVolumes`
- `goal.containerIndex`
- `goal.desiredVolume`
- `goal.expression`
- `heuristic.type`
- `heuristic.targetIndex`
- `heuristic.targetVolume`
- `heuristic.goalSum`
- `constraints.blockRoutes`
- `constraints.maxTransfer`
- `constraints.minTransfer`
- `constraints.blockReceiving`
- `constraints.onlyEvenSenders`

## Architecture overview

- `com.optimumtransfer.application.SolverService` is the application boundary for solving requests.
- `com.optimumtransfer.application.SolverRequest` captures a reproducible solver run.
- `com.optimumtransfer.model.State` is an immutable snapshot of container volumes.
- `com.optimumtransfer.goals.GoalCondition` defines whether a state satisfies the target.
- `com.optimumtransfer.heuristics.Heuristic` estimates remaining work for A*.
- `com.optimumtransfer.constraints.TransferConstraint` blocks invalid or restricted moves.
- `com.optimumtransfer.search.AStar` owns shortest-path search and bounded solution enumeration.
- `com.optimumtransfer.visualization` contains console and GUI playback helpers.

More detail is available in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Quality status

The repository now includes:

- standard namespaced packages
- repeatable local build and test scripts
- regression coverage for model rules, parser behavior, search behavior, service orchestration, and config loading
- CI for compile-and-test validation
- team-facing contribution and architecture docs

## Enterprise-level next steps

The highest-value follow-ups from here are:

- formalize logging and solver metrics instead of console-only output
- separate visualization and export behind adapter interfaces
- add packaged releases and versioning conventions
- expand scenario coverage for CLI and GUI flows
- consider a standard dependency/build tool when the environment allows it

## License

No license file is currently included in the repository.

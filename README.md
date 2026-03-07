# model.Container model.Transfer Optimization System

A configurable Java optimization engine for solving constrained container transfer problems using A* search.

## Overview
This project models a set of containers with capacities and starting volumes, then searches for a valid sequence of 
transfers that satisfies a selected goal condition. The solver supports custom heuristics, configurable transfer 
constraints, multiple search modes, and step-by-step visualization.

## Features
- A* search for shortest-cost transfer sequences
- Multiple goal conditions:
  - Exact match for all containers
  - Target volume for a single container
  - Even distribution across containers
  - Custom lambda-style goals
  - Advanced expression-based goals
- Pluggable heuristics
- model.Transfer constraints
- Find the shortest solution or enumerate all valid solutions
- Console-based visualization
- Swing GUI visualization
- Export solution steps to file

## Architecture
- `app.Main` - interactive CLI for configuring and running problems
- `search.AStar` - search engine for shortest-path and solution enumeration
- `model.State` - immutable or state-model representation of container volumes
- `model.Transfer` - action between containers with associated cost
- `goals.GoalCondition` -interface for target-state logic
- `heuristics.Heuristic` - interface for search guidance
- `constraints.TransferConstraint` - interface for move restrictions
- `visualization.Visualizer` - console step-by-step output
- `visualization.TransferGUI` - Swing-based visualization

## Example Use Case
Given a set of containers with user-defined capacities and starting volumes, the solver can:
- reach an exact target state
- fill a single container to a desired amount
- evenly distribute liquid among containers
- enforce custom restrictions on valid transfers

## How to Run
1. Compile the Java source files
2. Run `app.Main`
3. Enter:
   - number of containers
   - container capacities
   - starting volumes
   - goal type
   - heuristic selection
   - optional transfer constraints
   - solving strategy

## Example Output

## Visualization
The project supports:
- terminal-based step-by-step state visualization
- Swing GUI playback of the selected solution

## Future Improvements
- package-based source organization
- unit tests
- benchmark mode
- richer GUI visualization
- configurable move cost models
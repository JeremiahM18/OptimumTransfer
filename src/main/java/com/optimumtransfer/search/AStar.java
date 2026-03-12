package com.optimumtransfer.search;

import com.optimumtransfer.constraints.TransferConstraint;
import com.optimumtransfer.goals.GoalCondition;
import com.optimumtransfer.heuristics.Heuristic;
import com.optimumtransfer.model.MoveResult;
import com.optimumtransfer.model.State;
import com.optimumtransfer.model.Transfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Search.AStar
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Implements the A* search algorithm to solve the container transfer optimization problem.
 * Searches for the shortest sequence of transfers to reach the target container volumes.
 */
public class AStar {
    private static final Heuristic ZERO_HEURISTIC = state -> 0;
    private static final int UNREACHABLE_COST = Integer.MAX_VALUE;

    private final int[] capacities;
    private final List<TransferConstraint> constraints;
    private final Heuristic heuristic;

    /**
     * Constructs the Search.AStar solver with container capacities.
     *
     * @param capacity The maximum volume of each container.
     */
    public AStar(int[] capacity) {
        this(capacity, List.of(), ZERO_HEURISTIC);
    }

    public AStar(int[] capacity, List<TransferConstraint> constList) {
        this(capacity, constList, ZERO_HEURISTIC);
    }

    public AStar(int[] capacity, List<TransferConstraint> constList, Heuristic heur) {
        capacities = capacity.clone();
        constraints = List.copyOf(constList);
        heuristic = heur;
    }

    /**
     * Solves the container transfer problem using A* search.
     *
     * @param start The starting model.State.
     * @param goal The target container volumes.
     * @return A list of Transfers to reach the goal, or null if no solution exists.
     */
    public List<Transfer> solve(State start, GoalCondition goal) {
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(this::score));
        Map<State, Integer> bestCosts = new HashMap<>();

        frontier.add(new Node(start, new ArrayList<>(), 0));
        bestCosts.put(start, 0);

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();
            State currentState = current.getState();

            if (current.getCost() > bestCosts.getOrDefault(currentState, UNREACHABLE_COST)) {
                continue;
            }

            if (goal.isSatisfied(currentState)) {
                return current.getPath();
            }

            for (MoveResult neighbor : generateNextStates(currentState)) {
                State nextState = neighbor.getNewState();
                Transfer action = neighbor.getAction();
                int newCost = current.getCost() + action.getWeight();
                int bestKnownCost = bestCosts.getOrDefault(nextState, UNREACHABLE_COST);

                if (newCost < bestKnownCost) {
                    List<Transfer> newPath = new ArrayList<>(current.getPath());
                    newPath.add(action);
                    bestCosts.put(nextState, newCost);
                    frontier.add(new Node(nextState, newPath, newCost));
                }
            }
        }

        return null;
    }

    /**
     * Generates all valid next states from the current state by trying all possible transfers.
     *
     * @param current The current state.
     * @return A list of model.MoveResult objects representing all possible next moves.
     */
    private List<MoveResult> generateNextStates(State current) {
        List<MoveResult> results = new ArrayList<>();
        int[] volumes = current.getVolumes();
        int numContainers = volumes.length;

        for (int from = 0; from < numContainers; from++) {
            if (volumes[from] == 0) {
                continue;
            }

            for (int to = 0; to < numContainers; to++) {
                if (from == to) {
                    continue;
                }

                int transferAmount = Math.min(volumes[from], capacities[to] - volumes[to]);
                if (transferAmount <= 0 || !isAllowed(current, from, to, transferAmount)) {
                    continue;
                }

                results.add(new MoveResult(applyTransfer(volumes, from, to, transferAmount),
                        new Transfer(from, to, transferAmount, transferAmount)));
            }
        }

        return results;
    }

    /**
     * Finds all valid paths from start to goal within a given max depth.
     *
     * @param start The starting state.
     * @param goal The goal condition to satisfy.
     * @param maxDepth The maximum number of steps to allow.
     * @return A list of all valid transfer paths (each as a list of Transfers).
     */
    public List<List<Transfer>> findAllSolutions(State start, GoalCondition goal, int maxDepth) {
        List<List<Transfer>> allSolutions = new ArrayList<>();
        Set<State> pathStates = new HashSet<>();
        pathStates.add(start);

        collectSolutions(start, goal, maxDepth, new ArrayList<>(), pathStates, allSolutions);
        return allSolutions;
    }

    private void collectSolutions(State current,
                                  GoalCondition goal,
                                  int maxDepth,
                                  List<Transfer> path,
                                  Set<State> pathStates,
                                  List<List<Transfer>> allSolutions) {
        if (goal.isSatisfied(current)) {
            allSolutions.add(new ArrayList<>(path));
        }

        if (path.size() >= maxDepth) {
            return;
        }

        for (MoveResult next : generateNextStates(current)) {
            State nextState = next.getNewState();
            if (pathStates.contains(nextState)) {
                continue;
            }

            path.add(next.getAction());
            pathStates.add(nextState);
            collectSolutions(nextState, goal, maxDepth, path, pathStates, allSolutions);
            pathStates.remove(nextState);
            path.remove(path.size() - 1);
        }
    }

    /**
     * Finds all valid paths from the start to a goal state without any depth limit.
     * Output over 50 will show total count only.
     *
     * @param start The starting state.
     * @param goal The goal condition
     * @return All valid paths as lists of Transfers.
     */
    public List<List<Transfer>> findAllPaths(State start, GoalCondition goal) {
        return findAllSolutions(start, goal, Integer.MAX_VALUE);
    }

    private int score(Node node) {
        return node.getCost() + heuristic.estimate(node.getState());
    }

    private boolean isAllowed(State current, int from, int to, int transferAmount) {
        for (TransferConstraint constraint : constraints) {
            if (!constraint.isAllowed(current, from, to, transferAmount)) {
                return false;
            }
        }
        return true;
    }

    private static State applyTransfer(int[] volumes, int from, int to, int transferAmount) {
        int[] newVolumes = volumes.clone();
        newVolumes[from] -= transferAmount;
        newVolumes[to] += transferAmount;
        return new State(newVolumes);
    }
}

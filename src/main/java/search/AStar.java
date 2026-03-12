package search;

import constraints.TransferConstraint;
import goals.GoalCondition;
import heuristics.Heuristic;
import model.MoveResult;
import model.State;
import model.Transfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;

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

    private final int[] capacities;
    private final List<TransferConstraint> constraints;
    private final Heuristic heuristic;

    /**
     * Constructs the Search.AStar solver with container capacities.
     *
     * @param capacity The maximum volume of each container.
     */
    public AStar(int[] capacity) {
        this(capacity, new ArrayList<>(), state -> 0);
    }

    public AStar(int[] capacity, List<TransferConstraint> constList ){
        this(capacity, constList, state -> 0);
    }

    public AStar(int[] capacity, List<TransferConstraint> constList, Heuristic heur) {
        capacities = capacity.clone();
        constraints = new ArrayList<>(constList);
        heuristic = heur;
    }

    /**
     * Solves the container transfer problem using A* search.
     *
     * @param start The starting model.State.
     * @param goal The target container volumes.
     * @return A list of Transfers to reach the goal, or null if no solution exists.
     */
    public List<Transfer> solve(State start, GoalCondition goal){
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(n -> n.getCost() + heuristic.estimate(n.getState())));
        Map<State, Integer> bestCosts = new HashMap<>();

        frontier.add(new Node(start, new ArrayList<>(), 0));
        bestCosts.put(start, 0);

        while(!frontier.isEmpty()){
            Node current = frontier.poll();
            State currentState = current.getState();

            if(current.getCost() > bestCosts.getOrDefault(currentState, Integer.MAX_VALUE)) {
                continue;
            }

            if(goal.isSatisfied(currentState)){
                return current.getPath();
            }

            for(MoveResult neighbor : generateNextStates(currentState)){
                State nextState = neighbor.getNewState();
                Transfer action = neighbor.getAction();

                List<Transfer> newPath = new ArrayList<>(current.getPath());
                newPath.add(action);

                int newCost = current.getCost() + action.getWeight();
                int bestKnownCost = bestCosts.getOrDefault(nextState, Integer.MAX_VALUE);

                if(newCost < bestKnownCost){
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
    private List<MoveResult> generateNextStates(State current){
        List<MoveResult> results = new ArrayList<>();
        int[] volumes = current.getVolumes();
        int numContainers = volumes.length;

        for(int i = 0; i < numContainers; i++){
            for(int j = 0; j < numContainers; j++){
                if(i != j && volumes[i] > 0){
                    int transferAmount = Math.min(volumes[i], capacities[j] - volumes[j]);
                    if(transferAmount > 0) {

                        boolean allowed = true;
                        for (TransferConstraint constraint : constraints) {
                            if (!constraint.isAllowed(current, i, j, transferAmount)) {
                                allowed = false;
                                break;
                            }
                        }

                        if (allowed) {
                            int[] newVolumes = volumes.clone();
                            newVolumes[i] -= transferAmount;
                            newVolumes[j] += transferAmount;

                            State newState = new State(newVolumes);
                            Transfer transfer = new Transfer(i, j, transferAmount, transferAmount);
                            results.add(new MoveResult(newState, transfer));
                        }
                    }
                }
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
    public List<List<Transfer>> findAllSolutions(State start, GoalCondition goal, int maxDepth){
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
        if(goal.isSatisfied(current)) {
            allSolutions.add(new ArrayList<>(path));
        }

        if(path.size() >= maxDepth) {
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
    public List<List<Transfer>> findAllPaths(State start, GoalCondition goal){
        return findAllSolutions(start, goal, Integer.MAX_VALUE);
    }
}

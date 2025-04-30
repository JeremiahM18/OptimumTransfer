import java.util.*;

/**
 * AStar
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

    /**
     * Constructs the AStar solver with container capacities.
     *
     * @param capacity The maximum volume of each container.
     */
    public AStar(int[] capacity) {
        this(capacity, new ArrayList<>());
    }

    public AStar(int[] capacity, List<TransferConstraint> constList) {
        capacities = capacity.clone();
        constraints = new ArrayList<>(constList);
    }

    /**
     * Solves the container transfer problem using A* search.
     *
     * @param start The starting State.
     * @param goal The target container volumes.
     * @return A list of Transfers to reach the goal, or null if no solution exists.
     */
    public List<Transfer> solve(State start, GoalCondition goal){
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Set<State> visited = new HashSet<>();

        frontier.add(new Node(start, new ArrayList<>(), 0));
        visited.add(start);

        while(!frontier.isEmpty()){
            Node current = frontier.poll();
            State currentState = current.getState();

            if(goal.isSatisfied(currentState)){
                return current.getPath();
            }

            for(MoveResult neighbor : generateNextStates(currentState)){
                State nextState = neighbor.getNewState();
                Transfer action = neighbor.getAction();

                if(!visited.contains(nextState)){
                    visited.add(nextState);

                    List<Transfer> newPath = new ArrayList<>(current.getPath());
                    newPath.add(action);

                    int newCost = current.getCost() + action.getWeight();

                    frontier.add(new Node(nextState, newPath, newCost));
                }
            }
        }

        // No solution found
        return null;
    }

    /**
     * Generates all valid next states from the current state by trying all possible transfers.
     *
     * @param current The current state.
     * @return A list of MoveResult objects representing all possible next moves.
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

                            int weight = transferAmount;

                            Transfer transfer = new Transfer(i, j, transferAmount, weight);
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
        Queue<Node> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();

        queue.add(new Node(start, new ArrayList<>(), 0));
        visited.add(start);

        while(!queue.isEmpty()){
            Node current = queue.poll();

            if(goal.isSatisfied(current.getState())){
                allSolutions.add(current.getPath());
            }

            if(current.getPath().size() >= maxDepth){
                continue;
            }

            for(MoveResult next : generateNextStates(current.getState())){
                State nextState = next.getNewState();
                Transfer action = next.getAction();

                if(!visited.contains(nextState)){
                    visited.add(nextState);

                    List<Transfer> newPath = new ArrayList<>(current.getPath());
                    newPath.add(action);

                    queue.add(new Node(nextState, newPath, current.getCost() + action.getWeight()));
                }
            }
        }
        return allSolutions;
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


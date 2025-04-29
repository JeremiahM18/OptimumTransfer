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

    /**
     * Constructs the AStar solver with container capacities.
     *
     * @param capacity The maximum volume of each container.
     */
    public AStar(int[] capacity) {
        //Defensive copy
        capacities = capacity.clone();
    }

    /**
     * Solves the container transfer problem using A* search.
     *
     * @param start The starting State.
     * @param goal The target container volumes.
     * @return A list of Transfers to reach the goal, or null if no solution exists.
     */
    public List<Transfer> solve(State start, int[] goal){
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Set<State> visited = new HashSet<>();

        frontier.add(new Node(start, new ArrayList<>(), 0));
        visited.add(start);

        while(!frontier.isEmpty()){
            Node current = frontier.poll();
            State currentState = current.getState();

            if(currentState.matchesGoal(goal)){
                return current.getPath();
            }

            for(MoveResult neighbor : generateNextStates(currentState)){
                State nextState = neighbor.getNewState();
                Transfer action = neighbor.getAction();

                if(!visited.contains(nextState)){
                    visited.add(nextState);

                    List<Transfer> newPath = new ArrayList<>(current.getPath());
                    newPath.add(action);

                    frontier.add(new Node(nextState, newPath, current.getCost() + 1));
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
        int numConainers = volumes.length;

        for(int i = 0; i < numConainers; i++){
            for(int j = 0; j < numConainers; j++){
                if(i != j && volumes[i] > 0){
                    int transferAmount = Math.min(volumes[i], capacities[j] - volumes[j]);
                    if(transferAmount > 0){
                        int[] newVolumes = volumes.clone();
                        newVolumes[i] -= transferAmount;
                        newVolumes[j] += transferAmount;

                        State newState = new State(newVolumes);
                        Transfer transfer = new Transfer(i, j, transferAmount);
                        results.add(new MoveResult(newState, transfer));
                    }
                }
            }
        }

        return results;
    }

}


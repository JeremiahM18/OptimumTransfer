import java.util.List;

/**
 * Node
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Represents a node in the A* search algorithm.
 * Each node contains a State, the path of Transfers to reach it,
 * and the total cost (number of moves so far).
 */

public class Node implements Comparable<Node> {
    private final State state;
    private final List<Transfer> path;
    private final int cost;

    /**
     * Constructs a Node for A* search.
     * @param s The current state at this node.
     * @param p The list of Transfers taken to reach this state.
     * @param c The cumulative cost (number of moves) so far.
     */
    Node(State s, List<Transfer> p, int c){
        state = s;
        path = p;
        cost = c;
    }

    /**
     * Returns the current State of this Node.
     */
    public State getState(){
        return state;
    }

    /**
     * Returns the path of Transfers taken to reach this Node.
     */
    public List<Transfer> getPath(){
        return path;
    }

    /**
     * Returns the total cost (number of moves) to reach this Node.
     */
    public int getCost(){
        return cost;
    }

    /**
     * Compares two nodes based on their cost, for use in PriorityQueue.
     */
    public int compareTo(Node n){
        return Integer.compare(cost, n.getCost());
    }
}

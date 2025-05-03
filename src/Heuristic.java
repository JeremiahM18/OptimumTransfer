import java.util.Arrays;
/**
 * Heuristic
 *
 * Author: Jeremiah McDonald
 * Date: 2 May 2025
 *
 * Description:
 * Heuristic Interface for A* search algorithm.
 * Used to estimate the cost from a given state to the goal
 */

public interface Heuristic {
    int estimate (State s);
}

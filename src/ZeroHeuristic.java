/**
 * ZeroHeuristic
 *
 * Author: Jeremiah McDonald
 * Date: 3 May 2025
 *
 * Description:
 * Dijkstra's Algorithm
 */

public class ZeroHeuristic implements Heuristic{
    @Override
    public int estimate(State state){
        return 0;
    }
}

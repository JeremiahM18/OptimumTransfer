/**
 * TotalVolumeHeuristic
 *
 * Author: Jeremiah McDonald
 * Date: 3 May 2025
 *
 * Description:
 * Heuristic estimated the difference from a goal total volume.
 * Returns absolute difference between current sum and target sum.
 */

public class TotalVolumeHeuristic implements Heuristic{
    private final int goalTotal;

    public TotalVolumeHeuristic(int goal) {
        goalTotal = goal;
    }

    @Override
    public int estimate(State state){
        int sum = 0;
        for(int V : state.getVolumes()){
            sum += V;
        }
        return Math.abs(sum - goalTotal);
    }
}

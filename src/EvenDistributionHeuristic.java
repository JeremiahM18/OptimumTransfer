/**
 * EvenDistributionHeuristic
 *
 * Author: Jeremiah McDonald
 * Date: 3 May 2025
 *
 * Description:
 * Heuristic that estimates to an even distribution among all containers.
 * Returns the total imbalance compared to the average.
 */

public class EvenDistributionHeuristic  implements Heuristic {
    @Override
    public int estimate(State state){
        int[] volumes = state.getVolumes();
        int total = 0;
        for(int v : volumes){
            total += v;
        }
        int average = total / volumes.length;
        int sumDev = 0;
        for(int v : volumes){
            sumDev += Math.abs(v - average);
        }
        return sumDev/2;
    }
}

/**
 * SingleContainerHeuristic
 *
 * Author: Jeremiah McDonald
 * Date: 3 May 2025
 *
 * Description:
 * Heuristic targeting a specific container to reach a target volume.
 * Returns the absolute difference from the goal.
 */

public class SingleContainerHeuristic implements Heuristic{
    private final int targetIndex;
    private final int targetVolume;

    public SingleContainerHeuristic(int targetInd, int targetVol) {
        targetIndex = targetInd;
        targetVolume = targetVol;
    }

    @Override
    public int estimate(State state){
        return Math.abs(state.getVolumes()[targetIndex] - targetVolume);
    }
}

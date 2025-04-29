/**
 * SingleContainerGoal
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * A goal condition where a specific container must reach a specified volume.
 */


public class SingleContainerGoal implements GoalCondition{
    private final int containerIndex;
    private final int desiredVolume;

    /**
     * Constructs a SingleContainerGoal
     *
     * @param conIndex The index of the container to check.
     * @param desiredVol The required volume for that container.
     */
    public SingleContainerGoal(int conIndex, int desiredVol) {
        containerIndex = conIndex;
        desiredVolume = desiredVol;
    }

    @Override
    public boolean isSatisfied(State state) {
        return state.getVolumes()[containerIndex] == desiredVolume;
    }
}

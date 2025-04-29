import java.util.Arrays;

/**
 * ExactMatchGoal
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * A goal condition where the target volumes must match exactly for each container.
 */

public class ExactMatchGoal implements  GoalCondition{
    private final int[] targetVolumes;

    /**
     * Constructs an ExactMatchGoal with the given target volumes.
     *
     * @param targetVol The desired final volumes for each container.
     */
    public ExactMatchGoal(int[] targetVol) {
        targetVolumes = targetVol.clone();
    }

    @Override
    public boolean isSatisfied(State state){
        return Arrays.equals(state.getVolumes(), targetVolumes);
    }
}

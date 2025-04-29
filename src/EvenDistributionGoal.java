/**
 * EvenDistributionGoal
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * a goal condition where all containers must have the same volume.
 */

public class EvenDistributionGoal implements GoalCondition{

    @Override
    public boolean isSatisfied(State state){
        int[] volume = state.getVolumes();
        int firstVol = volume[0];
        for(int vol : volume){
            if(vol != firstVol){
                return false;
            }
        }
        return true;
    }
}

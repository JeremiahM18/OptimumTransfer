import java.util.Arrays;
import java.util.Objects;

/**
 * State
 *
 * Author: Jeremiah McDonald
 * Date: 23 April 2025
 *
 * Description:
 * Represents a snapshot of the system's container volumes at a specific point in time.
 * Supports equality, hashing, and comparison for use in graph search algorithms.
 */

public class State {
    private final int[] volumes;

    /**
     * Constructs a State from an array of container volumes.
     *
     * @param vol The array representing the current volume in each container.
      */
    public State(int[] vol){
        //Copy to protect internal state
        volumes = vol.clone();
    }

    /**
     * Returns a copy of the volumes array.
     *
     * @return A copy of the container volumes.
     */
    public int[] getVolumes(){
        return volumes.clone();
    }

    /**
     * Checks if this state matches the target goal configuration.
     *
     * @param target The target volumes to match.
     * @return true if the current volumes match the target exactly, false otherwise.
     */
    public boolean matchesGoal(int[] target){
        return Arrays.equals(volumes, target);
    }

    /**
     * Returns a string representation of the state.
     */
    @Override
    public String toString(){
        return Arrays.toString(volumes);
    }

    /**
     * Checks for equality between two states based on container volumes.
     */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof State)) return false;
        State state = (State)o;
        return Arrays.equals(volumes, state.volumes);
    }

    /**
     * Generates a hash code based on the container volumes.
     */
    @Override
    public int hashCode(){
        return Arrays.hashCode(volumes);
    }
}

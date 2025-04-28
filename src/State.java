import java.util.Arrays;
import java.util.Objects;

public class State {
    private final int[] volumes;

    // Create a state from an array of volumes
    public State(int[] vol){
        volumes = vol.clone();
    }

    // Copy of volumes array
    public int[] getVolumes(){
        return volumes.clone();
    }

    // Check if this state matches a target state
    public boolean matchesGoal(int[] target){
        return Arrays.equals(volumes, target);
    }

    public String toString(){
        return Arrays.toString(volumes);
    }

    // Required for use in HashSet/ Map
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof State)) return false;
        State state = (State)o;
        return Arrays.equals(volumes, state.volumes);
    }

    public int hashCode(){
        return Arrays.hashCode(volumes);
    }
}

/**
 * Container
 *
 * Author: Jeremiah McDonald
 * Date: 23 April 2025
 *
 * Description:
 * Represents a container with a fixed capacity and a modifiable current volume.
 * Includes methods for safely updated and retrieving volume information.
 */
public class Container {
    private final int capacity;
    private int currentVol;

    /**
     * Constructs a container with a given capacity and starting volume.
     *
     * @param cap The maximum capacity of the container.
     * @param vol the initial volume of the container.
     * @throws IllegalArgumentException if volume > capacity or if either is negative.
     */
    public Container(int cap, int vol) {
        if(vol > cap) {
            throw new IllegalArgumentException("Volume cannot exceed capacity");
        }
        if(cap < 0 || vol < 0) {
            throw new IllegalArgumentException("Capacity and Volume cannot be negative");
        }
        capacity = cap;
        currentVol = vol;
    }

    /**
     * Returns the current volume in the container.
     */
    public int getCurrentVol() {
        return currentVol;
    }

    /**
     *
     * Returns the maximum capacity of the container.
     */
    public int getCapacity(){
        return capacity;
    }

    /**
     * Updates the current volume of the container.
     *
     * @param vol The new volume to set.
     * @throws IllegalArgumentException if volume is not between 0 and capacity.
     */
    public void setCurrentVol(int vol){
        if(vol > capacity || vol < 0) {
            throw new IllegalArgumentException("Volume must be between 0 and capacity");
        }
        currentVol = vol;
    }

    /**
     * Returns a string representation of the container.
     */
    @Override
    public String toString(){
        return "(" + currentVol + "/" + capacity + ")";
    }
}

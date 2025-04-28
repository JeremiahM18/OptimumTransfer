
public class Container {
    private final int capacity;
    private int currentVol;

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

    public int getCurrentVol() {
        return currentVol;
    }

    public int getCapacity(){
        return capacity;
    }

    public void setCurrentVol(int vol){
        if(vol > capacity || vol < 0) {
            throw new IllegalArgumentException("Volume must be between 0 and capacity");
        }
        currentVol = vol;
    }

    public String toString(){
        return "(" + currentVol + "/" + capacity + ")";
    }
}

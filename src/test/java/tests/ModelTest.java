package tests;

import model.Container;
import model.State;
import model.Transfer;

public class ModelTest implements TestCase {
    @Override
    public String name() {
        return "Model objects protect state and validate invariants";
    }

    @Override
    public void run() {
        int[] volumes = {3, 1, 0};
        State state = new State(volumes);
        volumes[0] = 99;

        TestSupport.assertArrayEquals(new int[]{3, 1, 0}, state.getVolumes(), "State should clone constructor input.");

        int[] returned = state.getVolumes();
        returned[1] = 88;
        TestSupport.assertArrayEquals(new int[]{3, 1, 0}, state.getVolumes(), "State should not expose internal array.");

        Container container = new Container(5, 2);
        container.setCurrentVol(4);
        TestSupport.assertEquals(4, container.getCurrentVol(), "Container should update to a valid volume.");

        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Container(3, 4), "Container should reject starting volume above capacity.");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> container.setCurrentVol(6), "Container should reject updates above capacity.");

        Transfer transfer = new Transfer(0, 2, 3, 3);
        TestSupport.assertEquals(0, transfer.getFromContainer(), "Transfer should expose source container.");
        TestSupport.assertEquals(2, transfer.getToContainer(), "Transfer should expose destination container.");
        TestSupport.assertEquals(3, transfer.getAmount(), "Transfer should expose amount.");
        TestSupport.assertEquals(3, transfer.getWeight(), "Transfer should expose weight.");
    }
}

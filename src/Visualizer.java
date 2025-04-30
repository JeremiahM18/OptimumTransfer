import java.util.List;

/**
 * Visualizer
 *
 * Author: Jeremiah McDonald
 * Date: 30 April 2025
 *
 * Description:
 * Displays the transfer steps and the evolving state of containers over time.
 */

public class Visualizer {

    /**
     * Displays each transfer step and the resulting state.
     *
     * @param transfers The list of transfer actions to apply.
     * @param startVol The initial volumes of the containers.
     * @param capacities The max capacities for each container.
     */
    public static void show(List<Transfer> transfers, int[] startVol, int[] capacities) {
        int[] volumes = startVol.clone();
        int step = 0;

        System.out.println("\n=== Transfer Visualization ===");
        printState(step, volumes, capacities);

        for(Transfer t : transfers) {
            step++;
            System.out.println("\nStep " + step + ": " + t);

            // Perform the transfer
            volumes[t.getFromContainer()] -= t.getAmount();
            volumes[t.getToContainer()] += t.getAmount();

            printState(step, volumes, capacities);
        }
    }

    private static void printState(int step, int[] volumes, int[] capacities) {
        System.out.print("Container States: ");
        for (int i = 0; i < volumes.length; i++) {
            int filled = volumes[i];
            int cap = capacities[i];
            String bar = "=".repeat(filled) + " ".repeat(cap - filled);
            System.out.printf("C%d [%s] %d%d\n", i, bar, filled, cap);
        }
        System.out.println();
    }
}

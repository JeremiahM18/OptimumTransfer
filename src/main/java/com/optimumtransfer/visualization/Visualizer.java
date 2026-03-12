package com.optimumtransfer.visualization;

import com.optimumtransfer.model.Transfer;

import java.util.List;

/**
 * visualization.Visualizer
 *
 * Author: Jeremiah McDonald
 * Date: 30 April 2025
 *
 * Description:
 * Displays the transfer steps and the evolving state of containers over time.
 */
public class Visualizer {
    public static void show(List<Transfer> transfers, int[] startVol, int[] capacities) {
        int[] volumes = startVol.clone();
        int step = 0;

        System.out.println("\n=== Transfer Visualization ===");
        printState(step, volumes, capacities);

        for (Transfer transfer : transfers) {
            step++;
            System.out.println("\nStep " + step + ": " + transfer);
            volumes[transfer.getFromContainer()] -= transfer.getAmount();
            volumes[transfer.getToContainer()] += transfer.getAmount();
            printState(step, volumes, capacities);
        }
    }

    private static void printState(int step, int[] volumes, int[] capacities) {
        System.out.println("Container States after step " + step + ":");
        for (int i = 0; i < volumes.length; i++) {
            int filled = volumes[i];
            int cap = capacities[i];
            String bar = "=".repeat(filled) + " ".repeat(cap - filled);
            System.out.printf("C%d [%s] %d/%d%n", i, bar, filled, cap);
        }
        System.out.println();
    }
}

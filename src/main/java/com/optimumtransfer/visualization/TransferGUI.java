package com.optimumtransfer.visualization;

import com.optimumtransfer.model.Transfer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

/**
 * visualization.TransferGUI
 *
 * Author: Jeremiah McDonald
 * Date: 30 April 2025
 *
 * Description:
 * A simple Swing-based GUI to visualize the shortest(fastest) solution
 * to the container transfer optimization problem.
 */
public class TransferGUI extends JFrame {
    private final JTextArea outputArea;

    public TransferGUI(List<Transfer> steps, int[] startVol, int[] capacity) {
        setTitle("Container Transfer Visualization");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        outputArea = new JTextArea();
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton startButton = new JButton("Run Visualization");
        add(startButton, BorderLayout.SOUTH);
        startButton.addActionListener(event -> visualizeSteps(steps, startVol, capacity));
    }

    private void visualizeSteps(List<Transfer> steps, int[] startVol, int[] capacity) {
        outputArea.setText("");
        int[] volumes = startVol.clone();
        outputArea.append("Initial State: " + formatState(volumes, capacity) + "\n\n");

        int step = 1;
        for (Transfer transfer : steps) {
            outputArea.append("Step " + step++ + ": " + transfer + "\n");
            volumes[transfer.getFromContainer()] -= transfer.getAmount();
            volumes[transfer.getToContainer()] += transfer.getAmount();
            outputArea.append("State: " + formatState(volumes, capacity) + "\n\n");
        }
    }

    private String formatState(int[] volumes, int[] capacity) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < volumes.length; i++) {
            sb.append("[C")
                    .append(i)
                    .append(": ")
                    .append(volumes[i])
                    .append('/')
                    .append(capacity[i])
                    .append("] ");
        }
        return sb.toString().trim();
    }
}

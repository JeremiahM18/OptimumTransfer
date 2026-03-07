package visualization;

import model.Transfer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JTextArea outputArea;

    public TransferGUI(List<Transfer> steps, int[] startVol, int[] capacity) {
        setTitle("model.Container model.Transfer Visualization");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton startButton = new JButton("Run Visualization");
        add(startButton, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizeSteps(steps, startVol, capacity);
            }
        });
    }

    private void visualizeSteps(List<Transfer> steps, int[] startVol, int[] capacity) {
        outputArea.setText("");
        int[] volumes = startVol.clone();
        outputArea.append("Initial model.State: " + formatState(volumes, capacity) + "\n\n");

        int step = 1;
        for(Transfer t : steps) {
            outputArea.append("Step " + step++ + ": " + t + "\n");
            volumes[t.getFromContainer()] -= t.getAmount();
            volumes[t.getToContainer()] += t.getAmount();
            outputArea.append("model.State: " + formatState(volumes, capacity) + "\n\n");
        }
    }

    private String formatState(int[] volumes, int[] capacity) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < volumes.length; i++) {
            sb.append("[C" + i + ": " + volumes[i] + "/" + capacity[i] + "] ");
        }
        return sb.toString().trim();
    }
}

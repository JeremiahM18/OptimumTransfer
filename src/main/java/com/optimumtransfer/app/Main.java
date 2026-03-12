package com.optimumtransfer.app;

import com.optimumtransfer.application.SolveMode;
import com.optimumtransfer.application.SolverRequest;
import com.optimumtransfer.application.SolverResult;
import com.optimumtransfer.application.SolverService;
import com.optimumtransfer.constraints.TransferConstraint;
import com.optimumtransfer.constraints.TransferConstraints;
import com.optimumtransfer.goals.EvenDistributionGoal;
import com.optimumtransfer.goals.ExactMatchGoal;
import com.optimumtransfer.goals.GoalCondition;
import com.optimumtransfer.goals.SimpleGoalParser;
import com.optimumtransfer.goals.SingleContainerGoal;
import com.optimumtransfer.heuristics.EvenDistributionHeuristic;
import com.optimumtransfer.heuristics.Heuristic;
import com.optimumtransfer.heuristics.SingleContainerHeuristic;
import com.optimumtransfer.heuristics.TotalVolumeHeuristic;
import com.optimumtransfer.heuristics.ZeroHeuristic;
import com.optimumtransfer.model.Transfer;
import com.optimumtransfer.visualization.TransferGUI;
import com.optimumtransfer.visualization.Visualizer;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final String YES = "yes";
    private static final int DISPLAY_LIMIT = 50;
    private static final Path TRANSFER_LOG_PATH = Path.of("transfer_log.txt");

    private final SolverService solverService;

    public Main(SolverService solverService) {
        this.solverService = solverService;
    }

    public static void main(String[] args) {
        new Main(new SolverService()).run();
    }

    public void run() {
        boolean running = true;
        Scanner sc = new Scanner(System.in);

        while (running) {
            System.out.println("=== Container Transfer Optimization ===");

            int numContainers = readContainerCount(sc);
            int[] capacities = readCapacities(sc, numContainers);
            int[] startVolumes = readStartVolumes(sc, capacities);
            GoalCondition goal = readGoal(sc, numContainers, capacities);
            Heuristic heuristic = readHeuristic(sc, goal, numContainers, capacities);
            List<TransferConstraint> constraints = readConstraints(sc, numContainers);
            SolverRequest request = buildRequest(sc, capacities, startVolumes, goal, heuristic, constraints);
            SolverResult result = solverService.solve(request);

            handleResult(sc, result);

            System.out.print("\nWould you like to restart the program? (yes/no): ");
            running = isYes(sc.nextLine());
            if (!running) {
                System.out.println("Goodbye!");
            }
        }

        sc.close();
    }

    private int readContainerCount(Scanner sc) {
        int numContainers = 0;
        while (numContainers <= 0) {
            System.out.println("Enter number of containers: ");
            try {
                numContainers = sc.nextInt();
                if (numContainers <= 0) {
                    System.out.println("You must enter a positive number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please try again.");
                sc.nextLine();
            }
        }
        return numContainers;
    }

    private int[] readCapacities(Scanner sc, int numContainers) {
        int[] capacities = new int[numContainers];
        System.out.println("\nEnter the capacities for each container:");
        for (int i = 0; i < numContainers; i++) {
            capacities[i] = getValidInt(sc, "Capacity for container " + i + ": ", 0, Integer.MAX_VALUE);
        }
        return capacities;
    }

    private int[] readStartVolumes(Scanner sc, int[] capacities) {
        int[] startVolumes = new int[capacities.length];
        System.out.println("\nEnter the starting volume for each container:");
        for (int i = 0; i < capacities.length; i++) {
            startVolumes[i] = getValidInt(sc, "Starting volume for container " + i + ": ", 0, capacities[i]);
        }
        return startVolumes;
    }

    private GoalCondition readGoal(Scanner sc, int numContainers, int[] capacities) {
        GoalCondition goal = null;
        int[] goalVolumes = new int[numContainers];

        while (goal == null) {
            System.out.println("\nSelect Goal Type:");
            System.out.println("1. Exact match for all containers");
            System.out.println("2. Target volume for a single container");
            System.out.println("3. Even distribution among all containers");
            System.out.println("4. Custom goal (Lambda-style input)");
            System.out.println("5. Custom expression (Advanced)");
            System.out.print("Enter your choice: ");

            int choice = getValidInt(sc, "", 1, 5);

            switch (choice) {
                case 1 -> {
                    System.out.println("\nEnter the target volumes for each container:");
                    for (int i = 0; i < numContainers; i++) {
                        goalVolumes[i] = getValidInt(sc, "Target volume for container " + i + ": ", 0, capacities[i]);
                    }
                    goal = new ExactMatchGoal(goalVolumes);
                }
                case 2 -> {
                    int index = getValidInt(sc, "Enter container index to target: ", 0, numContainers - 1);
                    int volume = getValidInt(sc, "Enter desired volume for container " + index + ": ", 0, capacities[index]);
                    goal = new SingleContainerGoal(index, volume);
                }
                case 3 -> goal = new EvenDistributionGoal();
                case 4 -> goal = readCustomGoal(sc, numContainers, capacities);
                case 5 -> {
                    sc.nextLine();
                    System.out.println("\nEnter custom goal expression:");
                    String expr = sc.nextLine();
                    try {
                        goal = SimpleGoalParser.parse(expr, numContainers);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid expression: " + e.getMessage());
                    }
                }
                default -> {
                }
            }

            if (goal == null) {
                System.out.println("Failed to create goal. Please try again.\n");
            }
        }

        return goal;
    }

    private GoalCondition readCustomGoal(Scanner sc, int numContainers, int[] capacities) {
        System.out.println("\nCustom Goal Builder");
        System.out.println("1. Total volume of all containers equals X");
        System.out.println("2. Container i has at least X units");
        System.out.println("3. Sum of two containers equals X");

        int customType = getValidInt(sc, "Enter custom goal type: ", 1, 3);

        return switch (customType) {
            case 1 -> {
                int total = getValidInt(sc, "Enter total volume required: ", 0, Integer.MAX_VALUE);
                yield state -> Arrays.stream(state.getVolumes()).sum() == total;
            }
            case 2 -> {
                int index = getValidInt(sc, "Enter container index: ", 0, numContainers - 1);
                int min = getValidInt(sc, "Enter minimum volume: ", 0, capacities[index]);
                yield state -> state.getVolumes()[index] >= min;
            }
            case 3 -> {
                int a = getValidInt(sc, "Enter first container index: ", 0, numContainers - 1);
                int b = getValidInt(sc, "Enter second container index: ", 0, numContainers - 1);
                int sum = getValidInt(sc, "Enter desired combined volume: ", 0, capacities[a] + capacities[b]);
                yield state -> state.getVolumes()[a] + state.getVolumes()[b] == sum;
            }
            default -> throw new IllegalStateException("Unexpected custom goal option: " + customType);
        };
    }

    private Heuristic readHeuristic(Scanner sc, GoalCondition goal, int numContainers, int[] capacities) {
        Heuristic selectedHeuristic = new ZeroHeuristic();
        sc.nextLine();

        System.out.println("\nWould you like to add a heuristic? (yes/no):");
        if (!isYes(sc.nextLine())) {
            return selectedHeuristic;
        }

        System.out.println("\nSelect a heuristic strategy:");
        System.out.println("1. Even Distribution Heuristic");
        System.out.println("2. Single Container Heuristic (for use with goal type 2)");
        System.out.println("3. Total Volume Heuristic");

        int choice = getValidInt(sc, "Enter heuristic strategy: ", 1, 3);

        return switch (choice) {
            case 1 -> new EvenDistributionHeuristic();
            case 2 -> buildSingleContainerHeuristic(goal, selectedHeuristic);
            case 3 -> new TotalVolumeHeuristic(getValidInt(sc, "Enter desired total volume: ", 0, Arrays.stream(capacities).sum()));
            default -> selectedHeuristic;
        };
    }

    private Heuristic buildSingleContainerHeuristic(GoalCondition goal, Heuristic fallback) {
        if (goal instanceof SingleContainerGoal singleGoal) {
            return new SingleContainerHeuristic(singleGoal.getIndex(), singleGoal.getDesiredVolume());
        }

        System.out.println("Warning: Goal is not compatible with Single Container Heuristic. Using default instead.");
        return fallback;
    }

    private List<TransferConstraint> readConstraints(Scanner sc, int numContainers) {
        List<TransferConstraint> constraints = new ArrayList<>();

        System.out.print("\nWould you like to add any constraints? (yes/no): ");
        if (!isYes(sc.nextLine())) {
            return constraints;
        }

        boolean addedConstraint = true;
        while (addedConstraint) {
            System.out.println("\nSelect a constraint to add:");
            System.out.println("1. Block transfers from container A to B");
            System.out.println("2. Block transfers greater than X units");
            System.out.println("3. Block transfers to container C");
            System.out.println("4. Only allow even-numbered containers to send");
            System.out.println("5. Block transfers less than X units");
            System.out.print("6. Done adding constraints");
            int option = getValidInt(sc, "Choice: ", 1, 6);

            switch (option) {
                case 1 -> {
                    int from = getValidInt(sc, "Enter container A index: ", 0, numContainers - 1);
                    int to = getValidInt(sc, "Enter container B index: ", 0, numContainers - 1);
                    constraints.add(TransferConstraints.blockRoute(from, to));
                    System.out.println("\nConstraint added: Block transfer from " + from + " to " + to);
                }
                case 2 -> {
                    int max = getValidInt(sc, "Enter maximum transfer amount: ", 1, Integer.MAX_VALUE);
                    constraints.add(TransferConstraints.maxTransfer(max));
                    System.out.println("\nConstraint added: Max transfer amount = " + max);
                }
                case 3 -> {
                    int to = getValidInt(sc, "Enter container index to block receiving: ", 0, numContainers - 1);
                    constraints.add(TransferConstraints.blockReceiver(to));
                    System.out.println("\nConstraint added: Block transfers to container = " + to);
                }
                case 4 -> {
                    constraints.add(TransferConstraints.onlyEvenSenders());
                    System.out.println("\nConstraint added: Only even-numbered containers can send.");
                }
                case 5 -> {
                    int min = getValidInt(sc, "Enter minimum transfer amount: ", 1, Integer.MAX_VALUE);
                    constraints.add(TransferConstraints.minTransfer(min));
                    System.out.println("\nConstraint added: Block transfers less than " + min);
                }
                case 6 -> {
                    addedConstraint = false;
                    System.out.println("Done adding constraints");
                }
                default -> {
                }
            }
        }

        return constraints;
    }

    private SolverRequest buildRequest(Scanner sc,
                                       int[] capacities,
                                       int[] startVolumes,
                                       GoalCondition goal,
                                       Heuristic heuristic,
                                       List<TransferConstraint> constraints) {
        System.out.println("\nChoose solving strategy: ");
        System.out.println("1. Find the shortest (fastest) solution");
        System.out.println("2. Find all valid solutions up to a depth");
        System.out.println("3. Find all valid solutions (Anything over 50 will simply print total)");
        int solveChoice = getValidInt(sc, "Choice: ", 1, 3);
        sc.nextLine();

        SolveMode solveMode = SolveMode.SHORTEST_PATH;
        int maxDepth = SolverRequest.UNBOUNDED_DEPTH;

        if (solveChoice == 2) {
            solveMode = SolveMode.ALL_SOLUTIONS_WITH_DEPTH;
            maxDepth = getValidInt(sc, "Enter max depth: ", 1, Integer.MAX_VALUE);
            sc.nextLine();
        } else if (solveChoice == 3) {
            solveMode = SolveMode.ALL_SOLUTIONS_UNBOUNDED;
        }

        return new SolverRequest(capacities, startVolumes, goal, constraints, heuristic, solveMode, maxDepth);
    }

    private void handleResult(Scanner sc, SolverResult result) {
        if (result.getSolveMode() == SolveMode.SHORTEST_PATH) {
            handleShortestPathResult(sc, result);
            return;
        }

        List<List<Transfer>> allSolutions = result.getAllSolutions();
        printAllSolutions(allSolutions);
        visualizeSolutions(sc, allSolutions, result.getStartVolumes(), result.getCapacities());
    }

    private void handleShortestPathResult(Scanner sc, SolverResult result) {
        List<Transfer> shortestSolution = result.getShortestSolution();
        if (shortestSolution == null) {
            System.out.println("\nNo solution found.");
            return;
        }

        System.out.println("\n=== Shortest Solution Found ====");
        System.out.println("Steps: " + shortestSolution.size());
        int totalCost = shortestSolution.stream().mapToInt(Transfer::getWeight).sum();
        System.out.println("Total Cost: " + totalCost);
        for (Transfer move : shortestSolution) {
            System.out.println(move);
        }

        System.out.print("\nWould you like to visualize the steps? (yes/no): ");
        if (isYes(sc.nextLine())) {
            System.out.print("Use fancy GUI? (yes/no): ");
            if (isYes(sc.nextLine())) {
                SwingUtilities.invokeLater(() ->
                        new TransferGUI(shortestSolution, result.getStartVolumes(), result.getCapacities()).setVisible(true));
            } else {
                Visualizer.show(shortestSolution, result.getStartVolumes(), result.getCapacities());
            }
        }

        System.out.print("\nWould you like to export the steps to a file? (yes/no): ");
        if (isYes(sc.nextLine())) {
            exportToFile(shortestSolution, result.getStartVolumes());
        }
    }

    private static int getValidInt(Scanner sc, String message, int min, int max) {
        while (true) {
            System.out.print(message);
            try {
                int value = sc.nextInt();
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (InputMismatchException e) {
                sc.nextLine();
            }
            System.out.println("Invalid input. Please try again.");
        }
    }

    private static void printAllSolutions(List<List<Transfer>> allSolutions) {
        int total = allSolutions.size();

        if (total == 0) {
            System.out.println("No solution found.");
            return;
        }

        System.out.println("\nTotal solutions: " + total);

        if (total > DISPLAY_LIMIT) {
            System.out.println("Too many solutions to display (>" + DISPLAY_LIMIT + "). Showing total only.");
            return;
        }

        int count = 1;
        for (List<Transfer> solution : allSolutions) {
            int totalCost = solution.stream().mapToInt(Transfer::getWeight).sum();
            System.out.println("\nSolution " + count++ + " (Steps: " + solution.size() + ", Total Cost: " + totalCost + ")");
            for (Transfer transfer : solution) {
                System.out.println(transfer);
            }
        }
    }

    private static void visualizeSolutions(Scanner sc, List<List<Transfer>> allSolutions, int[] startVol, int[] capacities) {
        if (allSolutions.size() > DISPLAY_LIMIT) {
            System.out.println("Too many solutions to visualize. Showing total only.");
            return;
        }

        System.out.print("\nWould you like to visualize a solution? (yes/no): ");
        if (!isYes(sc.nextLine())) {
            return;
        }

        System.out.println("1. View a specific solution");
        System.out.println("2. View all solutions in order");
        int viewMode = getValidInt(sc, "Choice: ", 1, 2);
        sc.nextLine();

        if (viewMode == 1) {
            int index = getValidInt(sc, "Enter solution number (1 to " + allSolutions.size() + "): ", 1, allSolutions.size());
            sc.nextLine();
            List<Transfer> selectedPath = allSolutions.get(index - 1);

            System.out.print("Use a fancy GUI? (yes/no): ");
            if (isYes(sc.nextLine())) {
                SwingUtilities.invokeLater(() -> new TransferGUI(selectedPath, startVol, capacities).setVisible(true));
            } else {
                Visualizer.show(selectedPath, startVol, capacities);
            }
            return;
        }

        for (int i = 0; i < allSolutions.size(); i++) {
            System.out.println("\n--- Solution " + (i + 1) + " ---");
            Visualizer.show(allSolutions.get(i), startVol, capacities);
            System.out.print("Press Enter to continue to the next solution...");
            sc.nextLine();
        }
    }

    private static void exportToFile(List<Transfer> result, int[] startVol) {
        try (PrintWriter out = new PrintWriter(TRANSFER_LOG_PATH.toFile())) {
            out.println("Initial State: " + Arrays.toString(startVol));
            int[] volumes = startVol.clone();
            int step = 1;
            for (Transfer transfer : result) {
                out.println("Step " + step++ + ": " + transfer);
                volumes[transfer.getFromContainer()] -= transfer.getAmount();
                volumes[transfer.getToContainer()] += transfer.getAmount();
                out.println("State: " + Arrays.toString(volumes));
            }
            System.out.println("Transfer log saved to " + TRANSFER_LOG_PATH);
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + e.getMessage());
        }
    }

    private static boolean isYes(String response) {
        return YES.equals(response.trim().toLowerCase(Locale.ROOT));
    }
}

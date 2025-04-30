import java.util.*;

/**
 * Main
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Entry point to test the AStar container transfer solver.
 * Allows user to input container capacities, starting volumes and select a goal condition.
 * Supports predefined goals and custom lambda-style goal building.
 */

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Container Transfer Optimization ===");

        // Input number of containers
        int numContainers = 0;
        while(numContainers <= 0) {
            System.out.println("Enter number of containers: ");
            try {
                numContainers = sc.nextInt();
                if(numContainers <= 0) {
                    System.out.println("You must enter a positive number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input Please try again.");
                sc.nextLine();
            }
        }

        int[] capacity = new int[numContainers];
        int[] startVol = new int[numContainers];
        int[] goalVol = new int[numContainers];
        List<TransferConstraint> constraints = new ArrayList<>();

        // Input capacities
        System.out.println("\nEnter the capacities for each container:");
        for(int i = 0; i < numContainers; i++) {
            capacity[i] = getValidInt(sc, "Capacity for container " + i + ": ", 0, Integer.MAX_VALUE);
        }

        // Input starting volumes
        System.out.println("\nEnter the starting volume for each container:");
        for(int i = 0; i < numContainers; i++) {
            startVol[i] = getValidInt(sc, "Starting volume for container " + i + ": ", 0, capacity[i]);
        }

        // Setup Goal
        GoalCondition goal = null;

        while(goal == null){
            System.out.println("\nSelect Goal Type:");
            System.out.println("1. Exact match for all containers");
            System.out.println("2. Target volume for a single container");
            System.out.println("3. Even distribution among all containers");
            System.out.println("4. Custom goal (Lambda-style input)");
            System.out.println("5. Custom expression (Advanced)");
            System.out.print("Enter your choice: ");

            int choice = getValidInt(sc, "", 1,5);

            switch(choice) {
                case 1 -> {
                    System.out.println("\nEnter the target volumes for each container:");
                    for (int i = 0; i < numContainers; i++) {
                        goalVol[i] = getValidInt(sc, "Target volume for container " + i + ": ", 0, capacity[i]);
                    }
                    goal = new ExactMatchGoal(goalVol);
                }
                case 2 -> {
                    int index = getValidInt(sc, "Enter container index to target: ", 0, numContainers - 1);
                    int volume = getValidInt(sc, "Enter desired volume for container " + index + ": ", 0, capacity[index]);
                    goal = new SingleContainerGoal(index, volume);
                }
                case 3 -> {
                    goal = new EvenDistributionGoal();
                }
                case 4 -> {
                    System.out.println("\nCustom Goal Builder");
                    System.out.println("1. Total volume of all containers equals X");
                    System.out.println("2. Container[i] >= X");
                    System.out.println("3. Sum of two containers equals X");

                    int customType = getValidInt(sc, "Enter custom goal type: ", 1, 3);

                    switch (customType) {
                        case 1 -> {
                            int total = getValidInt(sc, "Enter total volume required: ", 0, Integer.MAX_VALUE);
                            goal = state -> Arrays.stream(state.getVolumes()).sum() == total;
                        }
                        case 2 -> {
                            int index = getValidInt(sc, "Enter container index: ", 0, numContainers - 1);
                            int min = getValidInt(sc, "Enter minimum volume: ", 0, capacity[index]);
                            goal = state -> state.getVolumes()[index] >= min;
                        }
                        case 3 -> {
                            int a = getValidInt(sc, "Enter first container index: ", 0, numContainers - 1);
                            int b = getValidInt(sc, "Enter second container index: ", 0, numContainers - 1);
                            int sum = getValidInt(sc, "Enter desired combined volume: ", 0, capacity[a] + capacity[b]);
                            goal = state -> state.getVolumes()[a] + state.getVolumes()[b] == sum;
                        }
                        default ->
                                System.out.println("Invalid option. Please try again.");
                    }
                }
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
            }

            if(goal == null){
                System.out.println("Failed to create goal. Please try again.\n");
            }
        }

        System.out.print("\nWould you like to add any constraints? (yes/no): ");
        sc.nextLine();
        String response = sc.nextLine().trim().toLowerCase();
        if(response.equals("yes")){
            boolean addedConstraint = true;

            while(addedConstraint){
                System.out.println("\nSelect a constraint to add:");
                System.out.println("1. Block transfers from container A to B");
                System.out.println("2. Block transfers greater than X units");
                System.out.println("3. Block transfers to container C");
                System.out.println("4. Only allow even-numbered containers to send");
                System.out.println("5. Block transfers less than X units");
                System.out.print("6. Done adding constraints");
                int option = getValidInt(sc, "Choice: ", 1,4);

                switch (option) {
                    case 1 -> {
                        int from = getValidInt(sc, "Enter container A index: ", 0, numContainers - 1);
                        int to = getValidInt(sc, "Enter container B index: ", 0, numContainers - 1);
                        constraints.add((state, f, t, amt) -> !(f == from && t == to));
                        System.out.println("\nConstraint added: Block transfer from " + from + " to " + to);
                    }
                    case 2 -> {
                        int max = getValidInt(sc, "Enter maximum transfer amount: ", 1, Integer.MAX_VALUE);
                        constraints.add((state, f, t, amt) -> amt <= max);
                        System.out.println("\nConstraint added: Max transfer amount = " + max);
                    }
                    case 3 -> {
                        int to = getValidInt(sc, "Enter container index to block receiving: ", 0, numContainers - 1);
                        constraints.add((state, f, t, amt) -> t != to);
                        System.out.println("\nConstraint added: Block transfers to container = " + to);
                    }
                    case 4 -> {
                        constraints.add((state, f, t, amt) -> f % 2 == 0);
                        System.out.println("\nConstraint added: Only even-numbered containers can send.");
                    }
                    case 5 -> {
                        int min = getValidInt(sc, "Enter minimum transfer amount: ", 1, Integer.MAX_VALUE);
                        constraints.add((state, f, t, amt) -> amt >= min);
                        System.out.println("\nConstraint added: Block transfers less than " + min);
                    }
                    case 6 -> {
                        addedConstraint = false;
                        System.out.println("Done adding constraints");
                    }

                }
            }
        }

        // Solve and print result
        State start = new State(startVol);
        AStar solver = new AStar(capacity, constraints);

        System.out.println("\nChoose solving strategy: ");
        System.out.println("1. Find the shortest (fastest) solution");
        System.out.println("2. Find all valid solutions up to a depth");
        System.out.println("3. Find all valid solutions (Anything over 50 will simply print total)");
        int solveChoice = getValidInt(sc, "Choice: ", 1, 3);

        if(solveChoice == 2){
            int maxDepth = getValidInt(sc, "Enter max depth: ", 1, Integer.MAX_VALUE);
            List<List<Transfer>> allSolutions = solver.findAllSolutions(start, goal, maxDepth);
            printAllSolutions(allSolutions);
        } else if(solveChoice == 3){
            List<List<Transfer>> allSolutions = solver.findAllPaths(start, goal);
            printAllSolutions(allSolutions);
        } else {

            List<Transfer> result = solver.solve(start, goal);

            if (result != null) {
                System.out.println("\n=== Solution Found ====");
                System.out.println("Steps: " + result.size());
                for (Transfer move : result) {
                    System.out.println(move);
                }
            } else {
                System.out.println("\nNo solution found.");
            }
        }
        sc.close();
    }

    /**
     * Method to get valid integer input
     */
    private static int getValidInt(Scanner sc, String message, int min, int max) {
        int value = Integer.MIN_VALUE;
        boolean valid = false;

        while(!valid){
            System.out.print(message);
            try{
                value = sc.nextInt();
                if(value >= min && value <= max){
                    valid = true;
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            } catch(InputMismatchException e){
                System.out.println("Invalid input. Please try again.");
                sc.nextLine();
            }
        }
        return value;
    }

    private static void printAllSolutions(List<List<Transfer>> allSolutions) {
        int total = allSolutions.size();

        if(total == 0){
            System.out.println("No solution found.");
            return;
        }

        System.out.println("\nTotal solutions: " + total);

        if(total > 50) {
            System.out.println("Too many solutions to display (>50). Showing total only.");
            return;
        }

        int count = 1;
        for(List<Transfer> solution : allSolutions){
            System.out.println("\nSolution " + count++ + " (Steps: " + solution.size() + ")");
            for(Transfer t : solution){
                System.out.println(t);
            }
        }
    }
}

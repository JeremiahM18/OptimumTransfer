package com.optimumtransfer.app;

import com.optimumtransfer.application.SolveMode;
import com.optimumtransfer.application.SolverResult;
import com.optimumtransfer.application.SolverService;
import com.optimumtransfer.application.config.SolverRequestPropertiesLoader;
import com.optimumtransfer.model.Transfer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ConfigMain {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java -cp build/classes/main com.optimumtransfer.app.ConfigMain <config-file>");
            System.exit(1);
        }

        SolverRequestPropertiesLoader loader = new SolverRequestPropertiesLoader();
        SolverService solverService = new SolverService();
        SolverResult result = solverService.solve(loader.load(Path.of(args[0])));
        printResult(result);
    }

    private static void printResult(SolverResult result) {
        if (result.getSolveMode() == SolveMode.SHORTEST_PATH) {
            List<Transfer> shortestSolution = result.getShortestSolution();
            if (shortestSolution == null) {
                System.out.println("No solution found.");
                return;
            }

            System.out.println("Shortest solution length: " + shortestSolution.size());
            int totalCost = shortestSolution.stream().mapToInt(Transfer::getWeight).sum();
            System.out.println("Total cost: " + totalCost);
            for (Transfer transfer : shortestSolution) {
                System.out.println(transfer);
            }
            return;
        }

        List<List<Transfer>> allSolutions = result.getAllSolutions();
        System.out.println("Solutions found: " + allSolutions.size());
        for (int i = 0; i < Math.min(allSolutions.size(), 10); i++) {
            List<Transfer> solution = allSolutions.get(i);
            int totalCost = solution.stream().mapToInt(Transfer::getWeight).sum();
            System.out.println("Solution " + (i + 1) + ": steps=" + solution.size() + ", totalCost=" + totalCost);
        }
    }
}

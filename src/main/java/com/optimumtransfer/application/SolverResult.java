package com.optimumtransfer.application;

import com.optimumtransfer.model.Transfer;

import java.util.ArrayList;
import java.util.List;

public class SolverResult {
    private final SolveMode solveMode;
    private final int[] capacities;
    private final int[] startVolumes;
    private final List<Transfer> shortestSolution;
    private final List<List<Transfer>> allSolutions;

    public SolverResult(SolveMode solveMode,
                        int[] capacities,
                        int[] startVolumes,
                        List<Transfer> shortestSolution,
                        List<List<Transfer>> allSolutions) {
        this.solveMode = solveMode;
        this.capacities = capacities.clone();
        this.startVolumes = startVolumes.clone();
        this.shortestSolution = shortestSolution == null ? null : new ArrayList<>(shortestSolution);
        this.allSolutions = copySolutions(allSolutions);
    }

    public SolveMode getSolveMode() {
        return solveMode;
    }

    public int[] getCapacities() {
        return capacities.clone();
    }

    public int[] getStartVolumes() {
        return startVolumes.clone();
    }

    public List<Transfer> getShortestSolution() {
        return shortestSolution == null ? null : new ArrayList<>(shortestSolution);
    }

    public List<List<Transfer>> getAllSolutions() {
        return copySolutions(allSolutions);
    }

    public boolean hasShortestSolution() {
        return shortestSolution != null;
    }

    private static List<List<Transfer>> copySolutions(List<List<Transfer>> solutions) {
        List<List<Transfer>> copy = new ArrayList<>();
        if (solutions == null) {
            return copy;
        }

        for (List<Transfer> solution : solutions) {
            copy.add(new ArrayList<>(solution));
        }
        return copy;
    }
}

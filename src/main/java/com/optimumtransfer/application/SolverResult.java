package com.optimumtransfer.application;

import com.optimumtransfer.model.Transfer;

import java.util.List;
import java.util.Objects;

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
        this.solveMode = Objects.requireNonNull(solveMode, "solveMode cannot be null");
        this.capacities = Objects.requireNonNull(capacities, "capacities cannot be null").clone();
        this.startVolumes = Objects.requireNonNull(startVolumes, "startVolumes cannot be null").clone();
        this.shortestSolution = shortestSolution == null ? null : List.copyOf(shortestSolution);
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
        return shortestSolution == null ? null : List.copyOf(shortestSolution);
    }

    public List<List<Transfer>> getAllSolutions() {
        return copySolutions(allSolutions);
    }

    public boolean hasShortestSolution() {
        return shortestSolution != null;
    }

    private static List<List<Transfer>> copySolutions(List<List<Transfer>> solutions) {
        if (solutions == null) {
            return List.of();
        }

        return solutions.stream()
                .map(List::copyOf)
                .toList();
    }
}

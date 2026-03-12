package com.optimumtransfer.application;

import com.optimumtransfer.constraints.TransferConstraint;
import com.optimumtransfer.goals.GoalCondition;
import com.optimumtransfer.heuristics.Heuristic;

import java.util.List;
import java.util.Objects;

public class SolverRequest {
    public static final int UNBOUNDED_DEPTH = Integer.MAX_VALUE;
    public static final int DEFAULT_MAX_SOLUTIONS = 1000;

    private final int[] capacities;
    private final int[] startVolumes;
    private final GoalCondition goal;
    private final List<TransferConstraint> constraints;
    private final Heuristic heuristic;
    private final SolveMode solveMode;
    private final int maxDepth;
    private final int maxSolutions;

    public SolverRequest(int[] capacities,
                         int[] startVolumes,
                         GoalCondition goal,
                         List<TransferConstraint> constraints,
                         Heuristic heuristic,
                         SolveMode solveMode,
                         int maxDepth) {
        this(capacities, startVolumes, goal, constraints, heuristic, solveMode, maxDepth, DEFAULT_MAX_SOLUTIONS);
    }

    public SolverRequest(int[] capacities,
                         int[] startVolumes,
                         GoalCondition goal,
                         List<TransferConstraint> constraints,
                         Heuristic heuristic,
                         SolveMode solveMode,
                         int maxDepth,
                         int maxSolutions) {
        this.capacities = requireArray(capacities, "capacities");
        this.startVolumes = requireArray(startVolumes, "startVolumes");
        this.goal = Objects.requireNonNull(goal, "goal cannot be null");
        this.constraints = List.copyOf(Objects.requireNonNull(constraints, "constraints cannot be null"));
        this.heuristic = Objects.requireNonNull(heuristic, "heuristic cannot be null");
        this.solveMode = Objects.requireNonNull(solveMode, "solveMode cannot be null");
        this.maxDepth = validateMaxDepth(maxDepth);
        this.maxSolutions = validateMaxSolutions(maxSolutions);

        if (this.capacities.length != this.startVolumes.length) {
            throw new IllegalArgumentException("capacities and startVolumes must have the same length.");
        }
    }

    public int[] getCapacities() {
        return capacities.clone();
    }

    public int[] getStartVolumes() {
        return startVolumes.clone();
    }

    public GoalCondition getGoal() {
        return goal;
    }

    public List<TransferConstraint> getConstraints() {
        return constraints;
    }

    public Heuristic getHeuristic() {
        return heuristic;
    }

    public SolveMode getSolveMode() {
        return solveMode;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxSolutions() {
        return maxSolutions;
    }

    private static int[] requireArray(int[] values, String name) {
        Objects.requireNonNull(values, name + " cannot be null");
        return values.clone();
    }

    private static int validateMaxDepth(int maxDepth) {
        if (maxDepth <= 0 && maxDepth != UNBOUNDED_DEPTH) {
            throw new IllegalArgumentException("maxDepth must be positive or UNBOUNDED_DEPTH.");
        }
        return maxDepth;
    }

    private static int validateMaxSolutions(int maxSolutions) {
        if (maxSolutions <= 0) {
            throw new IllegalArgumentException("maxSolutions must be positive.");
        }
        return maxSolutions;
    }
}

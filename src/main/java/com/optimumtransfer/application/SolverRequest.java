package com.optimumtransfer.application;

import com.optimumtransfer.constraints.TransferConstraint;
import com.optimumtransfer.goals.GoalCondition;
import com.optimumtransfer.heuristics.Heuristic;

import java.util.ArrayList;
import java.util.List;

public class SolverRequest {
    private final int[] capacities;
    private final int[] startVolumes;
    private final GoalCondition goal;
    private final List<TransferConstraint> constraints;
    private final Heuristic heuristic;
    private final SolveMode solveMode;
    private final int maxDepth;

    public SolverRequest(int[] capacities,
                         int[] startVolumes,
                         GoalCondition goal,
                         List<TransferConstraint> constraints,
                         Heuristic heuristic,
                         SolveMode solveMode,
                         int maxDepth) {
        this.capacities = capacities.clone();
        this.startVolumes = startVolumes.clone();
        this.goal = goal;
        this.constraints = new ArrayList<>(constraints);
        this.heuristic = heuristic;
        this.solveMode = solveMode;
        this.maxDepth = maxDepth;
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
        return new ArrayList<>(constraints);
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
}

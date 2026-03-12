package com.optimumtransfer.application;

import com.optimumtransfer.model.State;
import com.optimumtransfer.model.Transfer;
import com.optimumtransfer.search.AStar;

import java.util.List;

public class SolverService {
    public SolverResult solve(SolverRequest request) {
        AStar solver = new AStar(request.getCapacities(), request.getConstraints(), request.getHeuristic());
        State start = new State(request.getStartVolumes());

        return switch (request.getSolveMode()) {
            case SHORTEST_PATH -> buildShortestResult(request, solver, start);
            case ALL_SOLUTIONS_WITH_DEPTH -> buildAllSolutionsResult(
                    request,
                    solver.findAllSolutions(start, request.getGoal(), request.getMaxDepth())
            );
            case ALL_SOLUTIONS_UNBOUNDED -> buildAllSolutionsResult(
                    request,
                    solver.findAllPaths(start, request.getGoal())
            );
        };
    }

    private SolverResult buildShortestResult(SolverRequest request, AStar solver, State start) {
        List<Transfer> shortestSolution = solver.solve(start, request.getGoal());
        return new SolverResult(
                request.getSolveMode(),
                request.getCapacities(),
                request.getStartVolumes(),
                shortestSolution,
                List.of()
        );
    }

    private SolverResult buildAllSolutionsResult(SolverRequest request, List<List<Transfer>> solutions) {
        return new SolverResult(
                request.getSolveMode(),
                request.getCapacities(),
                request.getStartVolumes(),
                null,
                solutions
        );
    }
}

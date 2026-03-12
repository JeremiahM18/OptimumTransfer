package com.optimumtransfer.tests;

import com.optimumtransfer.application.SolveMode;
import com.optimumtransfer.application.SolverRequest;
import com.optimumtransfer.application.SolverResult;
import com.optimumtransfer.application.SolverService;
import com.optimumtransfer.goals.ExactMatchGoal;
import com.optimumtransfer.goals.GoalCondition;
import com.optimumtransfer.heuristics.ZeroHeuristic;

import java.util.List;

public class SolverServiceTest implements TestCase {
    @Override
    public String name() {
        return "SolverService exposes a reusable application boundary";
    }

    @Override
    public void run() {
        SolverService service = new SolverService();
        GoalCondition goal = new ExactMatchGoal(new int[]{2, 0, 2});

        SolverRequest shortestRequest = new SolverRequest(
                new int[]{4, 2, 2},
                new int[]{4, 0, 0},
                goal,
                List.of(),
                new ZeroHeuristic(),
                SolveMode.SHORTEST_PATH,
                Integer.MAX_VALUE
        );

        SolverResult shortestResult = service.solve(shortestRequest);
        TestSupport.assertTrue(shortestResult.hasShortestSolution(), "Shortest-path mode should return a single solution.");
        TestSupport.assertEquals(1, shortestResult.getShortestSolution().size(), "Shortest-path mode should preserve the optimal transfer count.");

        SolverRequest enumerationRequest = new SolverRequest(
                new int[]{4, 2, 2},
                new int[]{4, 0, 0},
                goal,
                List.of(),
                new ZeroHeuristic(),
                SolveMode.ALL_SOLUTIONS_WITH_DEPTH,
                2
        );

        SolverResult enumerationResult = service.solve(enumerationRequest);
        TestSupport.assertEquals(2, enumerationResult.getAllSolutions().size(), "Enumeration mode should return distinct solutions through the service layer.");
    }
}

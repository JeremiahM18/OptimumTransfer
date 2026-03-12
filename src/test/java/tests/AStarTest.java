package tests;

import constraints.TransferConstraint;
import goals.ExactMatchGoal;
import goals.GoalCondition;
import model.State;
import model.Transfer;
import search.AStar;

import java.util.List;

public class AStarTest implements TestCase {
    @Override
    public String name() {
        return "AStar solves constrained problems and enumerates distinct paths";
    }

    @Override
    public void run() {
        int[] capacities = {4, 2, 2};
        State start = new State(new int[]{4, 0, 0});
        GoalCondition goal = new ExactMatchGoal(new int[]{2, 0, 2});

        AStar solver = new AStar(capacities);
        List<Transfer> shortestPath = solver.solve(start, goal);

        TestSupport.assertTrue(shortestPath != null, "Solver should find a valid path.");
        TestSupport.assertEquals(1, shortestPath.size(), "Solver should return the direct shortest path.");
        TestSupport.assertEquals(0, shortestPath.get(0).getFromContainer(), "Shortest path should start from container 0.");
        TestSupport.assertEquals(2, shortestPath.get(0).getToContainer(), "Shortest path should target container 2 directly.");

        TransferConstraint blockDirectTransfer = (state, from, to, amount) -> !(from == 0 && to == 2);
        AStar constrainedSolver = new AStar(capacities, List.of(blockDirectTransfer));
        List<Transfer> constrainedPath = constrainedSolver.solve(start, goal);

        TestSupport.assertTrue(constrainedPath != null, "Solver should still find an alternative path when the direct move is blocked.");
        TestSupport.assertEquals(2, constrainedPath.size(), "Constraint should force the two-step path.");

        List<List<Transfer>> allSolutions = solver.findAllSolutions(start, goal, 2);
        TestSupport.assertEquals(2, allSolutions.size(), "All-solution search should keep distinct valid paths instead of pruning them globally.");
    }
}

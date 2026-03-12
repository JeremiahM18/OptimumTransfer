package com.optimumtransfer.tests;

import com.optimumtransfer.extras.GridPathSolver;

public class GridPathSolverTest implements TestCase {
    @Override
    public String name() {
        return "GridPathSolver matches the posted floor-is-lava problem";
    }

    @Override
    public void run() throws Exception {
        String arianeOutput = TestSupport.captureStdOut(() -> GridPathSolver.findShortestPath("Ariane", 7, false));
        TestSupport.assertContains("Shortest Cost to Fridge: 20", arianeOutput,
                "Ariane's shortest path should match the assignment graph.");
        TestSupport.assertContains("Path: Door -> C -> F -> H -> K -> Fridge", arianeOutput,
                "Ariane's route should match the shortest feasible path in the posted table.");

        String blakeOutput = TestSupport.captureStdOut(() -> GridPathSolver.findShortestPath("Blake", 9, true));
        TestSupport.assertContains("Shortest Cost to Fridge: 19", blakeOutput,
                "Blake's shortest path should be computed by distance, not by adding a risk penalty.");
        TestSupport.assertContains("Path: Door -> D -> H -> K -> Fridge", blakeOutput,
                "Blake's shortest route should match the shortest feasible path in the posted table.");
        TestSupport.assertContains("Probability Blake falls on this path: 0.360000", blakeOutput,
                "Blake's fall probability should be based on the risky jumps along the shortest path.");

        String unreachableOutput = TestSupport.captureStdOut(() -> GridPathSolver.findShortestPath("Unreachable", 1, true));
        TestSupport.assertContains("No path found to Fridge.", unreachableOutput,
                "Unreachable path output should fail gracefully instead of throwing.");
    }
}

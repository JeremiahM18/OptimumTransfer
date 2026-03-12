package com.optimumtransfer.tests;

import com.optimumtransfer.extras.GridPathSolver;

public class GridPathSolverTest implements TestCase {
    @Override
    public String name() {
        return "GridPathSolver handles reachable and unreachable destinations";
    }

    @Override
    public void run() throws Exception {
        String reachableOutput = TestSupport.captureStdOut(() -> GridPathSolver.findShortestPath("Reachable", 7, false));
        TestSupport.assertContains("Shortest Cost to Fridge:", reachableOutput,
                "Reachable path output should report a shortest cost.");
        TestSupport.assertContains("Path: Door", reachableOutput,
                "Reachable path output should include a route from the start node.");
        TestSupport.assertTrue(!reachableOutput.contains("No path found"),
                "Reachable path output should not report the destination as unreachable.");

        String unreachableOutput = TestSupport.captureStdOut(() -> GridPathSolver.findShortestPath("Unreachable", 1, true));
        TestSupport.assertContains("No path found to Fridge.", unreachableOutput,
                "Unreachable path output should fail gracefully instead of throwing.");
    }
}

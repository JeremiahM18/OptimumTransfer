package com.optimumtransfer.tests;

import com.optimumtransfer.application.SolveMode;
import com.optimumtransfer.application.SolverRequest;
import com.optimumtransfer.application.config.SolverRequestPropertiesLoader;
import com.optimumtransfer.goals.ExactMatchGoal;
import com.optimumtransfer.heuristics.ZeroHeuristic;

import java.util.Properties;

public class SolverRequestPropertiesLoaderTest implements TestCase {
    @Override
    public String name() {
        return "Properties loader builds reproducible solver requests";
    }

    @Override
    public void run() {
        Properties properties = new Properties();
        properties.setProperty("capacities", "4,2,2");
        properties.setProperty("startVolumes", "4,0,0");
        properties.setProperty("solveMode", "ALL_SOLUTIONS_WITH_DEPTH");
        properties.setProperty("maxDepth", "2");
        properties.setProperty("goal.type", "EXACT_MATCH");
        properties.setProperty("goal.targetVolumes", "2,0,2");
        properties.setProperty("heuristic.type", "ZERO");
        properties.setProperty("constraints.blockRoutes", "0>1");

        SolverRequest request = new SolverRequestPropertiesLoader().fromProperties(properties);

        TestSupport.assertArrayEquals(new int[]{4, 2, 2}, request.getCapacities(), "Loader should parse capacities.");
        TestSupport.assertArrayEquals(new int[]{4, 0, 0}, request.getStartVolumes(), "Loader should parse starting volumes.");
        TestSupport.assertEquals(2, request.getMaxDepth(), "Loader should parse max depth.");
        TestSupport.assertTrue(request.getSolveMode() == SolveMode.ALL_SOLUTIONS_WITH_DEPTH, "Loader should parse solve mode.");
        TestSupport.assertTrue(request.getGoal() instanceof ExactMatchGoal, "Loader should build the requested goal type.");
        TestSupport.assertTrue(request.getHeuristic() instanceof ZeroHeuristic, "Loader should build the requested heuristic type.");
        TestSupport.assertEquals(1, request.getConstraints().size(), "Loader should build configured constraints.");
    }
}

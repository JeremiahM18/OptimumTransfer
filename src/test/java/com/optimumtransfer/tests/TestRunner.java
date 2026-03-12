package com.optimumtransfer.tests;

import java.util.List;

public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) throws Exception {
        List<TestCase> tests = List.of(
                new ModelTest(),
                new SimpleGoalParserTest(),
                new AStarTest(),
                new SolverServiceTest(),
                new SolverRequestPropertiesLoaderTest(),
                new GridPathSolverTest(),
                new RuntimeDefaultsTest()
        );

        int passed = 0;
        for (TestCase test : tests) {
            try {
                test.run();
                passed++;
                System.out.println("[PASS] " + test.name());
            } catch (Throwable throwable) {
                System.err.println("[FAIL] " + test.name());
                throwable.printStackTrace(System.err);
            }
        }

        if (passed != tests.size()) {
            throw new IllegalStateException("Test run failed. Passed " + passed + " of " + tests.size() + " tests.");
        }

        System.out.println("Passed " + passed + " tests.");
    }
}

package com.optimumtransfer.tests;

import com.optimumtransfer.goals.GoalCondition;
import com.optimumtransfer.goals.SimpleGoalParser;
import com.optimumtransfer.model.State;

public class SimpleGoalParserTest implements TestCase {
    @Override
    public String name() {
        return "SimpleGoalParser supports expressions and conjunctions";
    }

    @Override
    public void run() {
        State state = new State(new int[]{4, 2, 0});

        GoalCondition sumGoal = SimpleGoalParser.parse("sum == 6", 3);
        TestSupport.assertTrue(sumGoal.isSatisfied(state), "Parser should support sum comparisons.");

        GoalCondition arithmeticGoal = SimpleGoalParser.parse("v[0] - v[1] == 2", 3);
        TestSupport.assertTrue(arithmeticGoal.isSatisfied(state), "Parser should support arithmetic on container references.");

        GoalCondition conjunctionGoal = SimpleGoalParser.parse("v[0] == 4 && v[1] >= 2 && sum == 6", 3);
        TestSupport.assertTrue(conjunctionGoal.isSatisfied(state), "Parser should evaluate all conjunction parts, not just the first two.");

        TestSupport.assertThrows(IllegalArgumentException.class, () -> SimpleGoalParser.parse("v[4] == 1", 3).isSatisfied(state), "Parser should reject invalid container indexes.");
    }
}


package com.optimumtransfer.goals;

import com.optimumtransfer.model.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleGoalParser {
    public static GoalCondition parse(String expr, int numContainers){
        expr = expr.replaceAll("\\s+", "");

        if(expr.contains("&&")){
            String[] parts = expr.split("&&");
            List<GoalCondition> conditions = new ArrayList<GoalCondition>();
            for (String part : parts) {
                conditions.add(parseSingle(part, numContainers));
            }
            return state -> conditions.stream().allMatch(condition -> condition.isSatisfied(state));
        }

        return parseSingle(expr, numContainers);
    }

    private static GoalCondition parseSingle(String expr, int numContainers){
        String[] operators = {"==", ">=", "<="};
        String selectedOp = null;

        for(String op : operators){
            if(expr.contains(op)){
                selectedOp = op;
                break;
            }
        }

        if(selectedOp == null){
            throw new IllegalArgumentException("Unsupported operation. Use ==, >=, or <=.");
        }

        String[] sides = expr.split(selectedOp);
        if(sides.length != 2){
            throw new IllegalArgumentException("Invalid expression format.");
        }

        final String op = selectedOp;
        final String left = sides[0];
        final String right = sides[1];

        return state -> {
            int leftValue = evaluateSide(left, state, numContainers);
            int rightValue = evaluateSide(right, state, numContainers);

            if ("==".equals(op)) {
                return leftValue == rightValue;
            }
            if (">=".equals(op)) {
                return leftValue >= rightValue;
            }
            if ("<=".equals(op)) {
                return leftValue <= rightValue;
            }
            throw new IllegalArgumentException("Unexpected operator: " + op);
        };
    }

    private static int evaluateSide(String side, State state, int numContainers){
        if(side.equals("sum")){
            return Arrays.stream(state.getVolumes()).sum();
        }

        int result = 0;
        String[] tokens = side.split("(?=[+-])");

        for(String token : tokens){
            token = token.trim();
            if(token.isEmpty()) {
                continue;
            }

            int sign = 1;
            if(token.startsWith("+")){
                token = token.substring(1);
            } else if(token.startsWith("-")){
                token = token.substring(1);
                sign = -1;
            }

            if(token.startsWith("v[")){
                int idx = Integer.parseInt(token.substring(2, token.length()-1));
                if(idx < 0 || idx >= numContainers){
                    throw new IllegalArgumentException("Invalid container index: " + idx);
                }

                result += sign * state.getVolumes()[idx];
            } else {
                result += sign * Integer.parseInt(token);
            }
        }
        return result;
    }
}

import java.util.Arrays;

/**
 * SimpleGoalParser
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Parses basic custom goal expressions into GoalCondition objects.
 * Supports simple arithmetic, container references, and comparisons.
 */

public class SimpleGoalParser {

    /**
     * Parses a string expression into a GoalCondition
     *
     * @param expr The user-provided expression.
     * @param numContainers The number of containers.
     * @return A GoalCondition representing the parsed expression.
     */
    public static GoalCondition parse(String expr, int numContainers){
        expr = expr.replaceAll("\\s+", ""); // Remove all spaces

        if(expr.contains("&&")){

            String[] parts = expr.split("&&");
            GoalCondition left = parseSingle(parts[0], numContainers);
            GoalCondition right = parseSingle(parts[1], numContainers);
            return state -> left.isSatisfied(state) && right.isSatisfied(state);
        } else {
            // Single condition
            return parseSingle(expr, numContainers);
        }
    }

    // Handles parsing a single condition
    private static GoalCondition parseSingle(String expr, int numContainers){
        String[] operatiors = {"==", ">=", "<="};
        String selecedOp = null;

        for(String op : operatiors){
            if(expr.contains(op)){
                selecedOp = op;
                break;
            }
        }

        if(selecedOp == null){
            throw new IllegalArgumentException("Unsupported operation. Use ==, >=, or <=.");
        }

        String[] sides = expr.split(selecedOp);
        if(sides.length != 2){
            throw new IllegalArgumentException("Invalid expression format.");
        }

        final String op = selecedOp;
        String left = sides[0];
        String right = sides[1];

        return state -> {
            int leftValue = evaluateSide(left, state, numContainers);
            int rightValue = evaluateSide(right, state, numContainers);

            switch(op){
                case "==" ->{
                    return leftValue == rightValue;
                }
                case ">=" -> {
                    return leftValue >= rightValue;
                }
                case "<=" -> {
                    return leftValue <= rightValue;
                }
                default -> throw new IllegalArgumentException("Unexpected operator: " + op);
            }
        };
    }

    // Evaluates one side of the expression
    private static int evaluateSide(String side,State state, int numContainers){
        if(side.equals("sum")){
            return Arrays.stream(state.getVolumes()).sum();
        }

        int result = 0;
        // Split keeping plus or minus as token
        String[] tokens = side.split("(?=[+-])");

        for(String token : tokens){
            token = token.trim();
            if(token.isEmpty()) continue;

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

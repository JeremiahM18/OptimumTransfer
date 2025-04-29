/**
 * GoalCondition
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Represents a flexible condition to determine whether a given state satisfies the goal.
 *
 */

public interface GoalCondition {

    /**
     * Checks if the provided state satisfies this goal condition.
     *
     * @param state The current state to evaluate.
     * @return true if the state meets the goal condition, false otherwise.
     */
    boolean isSatisfied(State state);
}

/**
 * MoveResult
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Represents the results of applying a transfer action.
 * - The resulting new State after the move.
 * - The Transfer action that caused the new State.
 */

public class MoveResult {
    private final State newState;
    private final Transfer action;

    /**
     * Constructs a MoveResult containing the resulting state and the action that produced it.
     * @param st The new State after performing the transfer.
     * @param act The Transfer action that led to the new state.
     */
    MoveResult(State st, Transfer act) {
        newState = st;
        action = act;
    }

    /**
     * Returns the new State resulting from the transfer
     */
    public State getNewState() {
        return newState;
    }

    /**
     * Returns the Transfer action that caused the new State
     */
    public Transfer getAction() {
        return action;
    }
}

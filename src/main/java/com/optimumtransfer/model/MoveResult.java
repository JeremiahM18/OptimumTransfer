package com.optimumtransfer.model;

/**
 * MoveResult
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Represents the results of applying a transfer action.
 * - The resulting new model.State after the move.
 * - The model.Transfer action (with cost) that caused it.
 */

public class MoveResult {
    private final State newState;
    private final Transfer action;

    /**
     * Constructs a model.MoveResult containing the resulting state and the action that produced it.
     * @param st The new model.State after performing the transfer.
     * @param act The model.Transfer action that led to the new state.
     */
    public MoveResult(State st, Transfer act) {
        newState = st;
        action = act;
    }

    /**
     * Returns the new model.State resulting from the transfer
     */
    public State getNewState() {

        return newState;
    }

    /**
     * Returns the model.Transfer action that caused the new model.State
     */
    public Transfer getAction() {

        return action;
    }
}


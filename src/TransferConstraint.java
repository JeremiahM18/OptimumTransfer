/**
 * TransferConstraint
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Represents a constraint that determines whether a proposed transfer is allowed.
 * Used during state generation to prune invalid or restricted moves.
 */

public interface TransferConstraint {

    /**
     * Determines whether a transfer is allowed under the current constraint.
     *
     * @param state The current State before the transfer.
     * @param from Index of the source container.
     * @param to Index of the target container.
     * @param amount Amount of volume being transferred.
     * @return true if the transfer is allowed, false otherwise.
     */
    boolean isAllowed(State state, int from, int to, int amount);
}

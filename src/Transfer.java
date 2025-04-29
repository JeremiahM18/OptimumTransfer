/**
 * Transfer
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Represents a single transfer operation between two containers,
 * including the source container, destination container, and amount transferred.
 */

public class Transfer {
    private final int fromContainer;
    private final int toContainer;
    private final int amount;

    /**
     * Constructs a Transfer object representing a move between two containers
     *
     * @param fromCon Index of the source container.
     * @param toCon Index of the destination container.
     * @param amo Amount of volume transferred.
     */
    public Transfer(int fromCon, int toCon, int amo) {
        fromContainer = fromCon;
        toContainer = toCon;
        amount = amo;
    }

    /**
     * Returns the index of the source container.
     */
    public int getFromContainer() {
        return fromContainer;
    }

    /**
     * Returns the index of the destination container.
     */
    public int getToContainer() {
        return toContainer;
    }

    /**
     * Returns the amount transferred.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Returns a readable description of the transfer.
     */
    @Override
    public String toString() {
        return "Transfer " + amount + " units from container " + fromContainer + " to container " + toContainer;
    }
}

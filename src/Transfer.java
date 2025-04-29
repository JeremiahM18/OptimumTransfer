/**
 * Transfer
 *
 * Author: Jeremiah McDonald
 * Date: 29 April 2025
 *
 * Description:
 * Represents a single transfer operation between two containers,
 * including the source, destination, and amount transferred, and the cost (weight) of the transfer.
 */

public class Transfer {
    private final int fromContainer;
    private final int toContainer;
    private final int amount;
    private final int weight;

    /**
     * Constructs a Transfer object with source, target, amount, and weight.
     *
     * @param fromCon Index of the source container.
     * @param toCon Index of the destination container.
     * @param amo Amount of volume transferred.
     * @param cost The cost associated with the transfer.
     */
    public Transfer(int fromCon, int toCon, int amo, int cost) {
        fromContainer = fromCon;
        toContainer = toCon;
        amount = amo;
        weight = cost;
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
     * Returns the weight of the transfer.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Returns a readable description of the transfer.
     */
    @Override
    public String toString() {
        return "Transfer " + amount + " units from container " + fromContainer + " to container " +
                toContainer + " (Cost: " + weight + " )";
    }
}

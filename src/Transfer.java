public class Transfer {
    private final int fromContainer;
    private final int toContainer;
    private final int amount;

    public Transfer(int fromCon, int toCon, int amo) {
        fromContainer = fromCon;
        toContainer = toCon;
        amount = amo;
    }

    public int getFromContainer() {
        return fromContainer;
    }

    public int getToContainer() {
        return toContainer;
    }

    public int getAmount() {
        return amount;
    }

    public String toString() {
        return "Transfer " + amount + " units from container " + fromContainer + " to container " + toContainer;
    }
}

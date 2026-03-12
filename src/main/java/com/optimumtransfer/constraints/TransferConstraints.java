package com.optimumtransfer.constraints;

public final class TransferConstraints {
    private TransferConstraints() {
    }

    public static TransferConstraint blockRoute(int from, int to) {
        return (state, source, target, amount) -> !(source == from && target == to);
    }

    public static TransferConstraint maxTransfer(int maxAmount) {
        return (state, source, target, amount) -> amount <= maxAmount;
    }

    public static TransferConstraint minTransfer(int minAmount) {
        return (state, source, target, amount) -> amount >= minAmount;
    }

    public static TransferConstraint blockReceiver(int blockedReceiver) {
        return (state, source, target, amount) -> target != blockedReceiver;
    }

    public static TransferConstraint onlyEvenSenders() {
        return (state, source, target, amount) -> source % 2 == 0;
    }
}

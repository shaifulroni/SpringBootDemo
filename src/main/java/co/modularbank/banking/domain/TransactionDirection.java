package co.modularbank.banking.domain;

public enum TransactionDirection {
    IN("IN"),
    OUT("OUT");

    private final String value;

    TransactionDirection(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TransactionDirection{" +
                "value='" + value + '\'' +
                '}';
    }
}

package co.modularbank.banking.controller.model;

public class SaveTransactionResponse extends TransactionResponse {
    private double balance;

    public SaveTransactionResponse() {}

    public SaveTransactionResponse(long accountId, long transactionId, double amount, String currency, String direction, String description, double balance) {
        super(accountId, transactionId, amount, currency, direction, description);
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "SaveTransactionResponse{" +
                "balance=" + balance +
                ", accountId=" + accountId +
                ", transactionId=" + transactionId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", direction='" + direction + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

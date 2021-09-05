package co.modularbank.banking.controller.model;

import java.math.BigDecimal;

public class SaveTransactionResponse extends TransactionResponse {
    private BigDecimal balance;

    public SaveTransactionResponse() {}

    public SaveTransactionResponse(long accountId, long transactionId, BigDecimal amount, String currency, String direction, String description, BigDecimal balance) {
        super(accountId, transactionId, amount, currency, direction, description);
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
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

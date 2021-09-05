package co.modularbank.banking.controller.model;

import java.math.BigDecimal;

public class TransactionResponse {
    protected long accountId;
    protected long transactionId;
    protected BigDecimal amount;
    protected String currency;
    protected String direction;
    protected String description;

    public TransactionResponse(){}

    public TransactionResponse(long accountId, long transactionId, BigDecimal amount, String currency, String direction, String description) {
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.direction = direction;
        this.description = description;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "GetTransactionResponse{" +
                "accountId=" + accountId +
                ", transactionId=" + transactionId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", direction='" + direction + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

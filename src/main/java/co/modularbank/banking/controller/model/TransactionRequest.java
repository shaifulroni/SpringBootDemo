package co.modularbank.banking.controller.model;

import co.modularbank.banking.domain.TransactionDirection;
import co.modularbank.banking.validator.EnumValidator;

import javax.validation.constraints.*;

public class TransactionRequest {
    @Min(value = 1, message = "Invalid account number")
    private long accountId;

    @Positive(message = "Invalid amount")
    private double amount;

    @Size(min = 3, max = 3, message = "Invalid currency")
    private String currency;

    @NotNull
    @EnumValidator(enumClass = TransactionDirection.class, message = "Invalid direction")
    private String direction;

    @NotNull
    @NotEmpty(message = "Description missing")
    private String description;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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
        return "TransactionRequest{" +
                "accountId=" + accountId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", direction=" + direction +
                ", description='" + description + '\'' +
                '}';
    }
}

package co.modularbank.banking.controller.model;

import java.math.BigDecimal;

public class BalanceResponse {
    private BigDecimal amount;
    private String currency;

    public BalanceResponse(){}

    public BalanceResponse(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
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

    @Override
    public String toString() {
        return "BalanceResponse{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}

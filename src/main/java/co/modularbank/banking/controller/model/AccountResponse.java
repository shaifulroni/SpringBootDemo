package co.modularbank.banking.controller.model;

import java.util.List;

public class AccountResponse {
    private long accountId;
    private long customerId;
    private List<BalanceResponse> balance;

    public AccountResponse(long accountId, long customerId, List<BalanceResponse> balance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = balance;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public List<BalanceResponse> getBalance() {
        return balance;
    }

    public void setBalance(List<BalanceResponse> balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "AccountResponse{" +
                "accountId=" + accountId +
                ", customerId=" + customerId +
                ", balance=" + balance +
                '}';
    }
}

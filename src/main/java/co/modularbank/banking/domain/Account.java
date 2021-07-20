package co.modularbank.banking.domain;

import co.modularbank.banking.controller.model.AccountResponse;
import co.modularbank.banking.controller.model.BalanceResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Account {
    private long id;
    private Customer customer;
    private Country country;
    private Set<Balance> balanceList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<Balance> getBalanceList() {
        return balanceList;
    }

    public void setBalanceList(Set<Balance> balanceList) {
        this.balanceList = balanceList;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", customer=" + customer +
                ", country=" + country +
                ", balanceList=" + balanceList +
                '}';
    }

    public AccountResponse toAccountResponse() {
        List<BalanceResponse> balanceRespList = balanceList.stream().map( balance ->
                new BalanceResponse(
                        balance.getAmount(),
                        balance.getCurrency().getShortName()
                )
            ).collect(Collectors.toList());
        return new AccountResponse(id, customer.getId(), balanceRespList);
    }
}

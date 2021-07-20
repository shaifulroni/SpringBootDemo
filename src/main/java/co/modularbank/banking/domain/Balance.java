package co.modularbank.banking.domain;

public class Balance {
    private long id;
    private Currency currency;
    private double amount;

//    public Balance(Currency currency, double amount) {
//        this.currency = currency;
//        this.amount = amount;
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "\nid=" + id +
                "\n, currency=" + currency +
                "\n, amount=" + amount +
                "\n}";
    }
}

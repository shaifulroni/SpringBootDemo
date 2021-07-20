package co.modularbank.banking.controller.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public class AccountRequest {
    @Min(value = 1, message = "Invalid customer id")
    private long customerId;

    @NotNull(message = "Country code can not be null or empty")
    @Size(min = 2, max = 2, message = "Country code must be 2 character")
    private String countryCode;

    @NotEmpty(message = "Invalid currency codes")
    private Set<String> currencyCodes;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Set<String> getCurrencyCodes() {
        return currencyCodes;
    }

    public void setCurrencyCodes(Set<String> currencyCodes) {
        this.currencyCodes = currencyCodes;
    }

    @Override
    public String toString() {
        return "AccountRequest{" +
                "customerId=" + customerId +
                ", countryCode='" + countryCode + '\'' +
                ", currencyCodes=" + currencyCodes +
                '}';
    }
}

package tsoy.alexander.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@JsonDeserialize(using = AccountDeserializer.class)
public class Account {

    private static final AtomicLong COUNTER = new AtomicLong();

    private final Long id;
    private String username;
    private BigDecimal amount;
    private Currency currency;

    public Account(String username, BigDecimal amount, Currency currency) {
        this.id = COUNTER.getAndIncrement();
        this.username = username;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}

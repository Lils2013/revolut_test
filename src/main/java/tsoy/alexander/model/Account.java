package tsoy.alexander.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private Object lock = new Object();

    public Account(String username, BigDecimal amount) {
        this.id = COUNTER.getAndIncrement();
        this.username = username;
        this.amount = amount;
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

    public Object getLock() {
        return lock;
    }

    public void withdraw(BigDecimal sum) {
        amount = amount.subtract(sum);
    }

    public void deposit(BigDecimal sum) {
        amount = amount.add(sum);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", amount=" + amount +
                '}';
    }
}

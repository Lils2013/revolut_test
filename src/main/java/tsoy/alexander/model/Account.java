package tsoy.alexander.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class Account {

    private static final AtomicLong COUNTER = new AtomicLong();

    private Long id;
    private String username;
    private BigDecimal balance;

    public Account(String username, BigDecimal balance) {
        this.id = COUNTER.getAndIncrement();
        this.username = username;
        this.balance = balance;
    }

    public Account() {
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void withdraw(BigDecimal sum) {
        balance = balance.subtract(sum);
    }

    public void deposit(BigDecimal sum) {
        balance = balance.add(sum);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                '}';
    }
}

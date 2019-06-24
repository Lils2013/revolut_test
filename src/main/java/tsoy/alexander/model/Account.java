package tsoy.alexander.model;

import java.math.BigDecimal;

public class Account {

    private Long id;
    private String username;
    private volatile BigDecimal balance;

    public Account(Long id, String username, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    public Account(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
    }

    public Account() {
    }

    public void setId(Long id) {
        this.id = id;
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

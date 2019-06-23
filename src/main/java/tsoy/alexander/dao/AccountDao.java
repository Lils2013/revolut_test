package tsoy.alexander.dao;

import tsoy.alexander.model.Account;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AccountDao implements Dao<Account> {

    private final Map<Long, Account> accountMap = new ConcurrentHashMap<>();
    private final AtomicLong COUNTER = new AtomicLong();

    public AccountDao() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(COUNTER.getAndIncrement(), "John", new BigDecimal("100.00")));
        accounts.add(new Account(COUNTER.getAndIncrement(), "Susan", new BigDecimal("200.00")));
        accounts.forEach(account -> accountMap.put(account.getId(), account));
    }

    @Override
    public Optional<Account> get(long id) {
        return Optional.ofNullable(accountMap.get(id));
    }

    @Override
    public List<Account> getAll() {
        return new ArrayList<>(accountMap.values());
    }

    @Override
    public void save(Account account) {
        account.setId(COUNTER.getAndIncrement());
        accountMap.put(account.getId(), account);
    }

    @Override
    public void update(Account account) {
        accountMap.put(account.getId(), account);
    }

    @Override
    public void delete(Account account) {
        accountMap.remove(account.getId());
    }
}
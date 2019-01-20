package tsoy.alexander.dao;

import tsoy.alexander.model.Account;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AccountDao implements Dao<Account> {

    private Map<Long, Account> accountMap = new ConcurrentHashMap<>();

    public AccountDao() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("John", new BigDecimal("100.00"), Currency.getInstance("EUR")));
        accounts.add(new Account("Susan", new BigDecimal("200.00"), Currency.getInstance("EUR")));
        accounts.forEach(account -> {
            accountMap.put(account.getId(), account);
        });
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
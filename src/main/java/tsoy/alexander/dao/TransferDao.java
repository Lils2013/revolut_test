package tsoy.alexander.dao;

import tsoy.alexander.model.Account;
import tsoy.alexander.model.Transfer;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransferDao implements Dao<Transfer> {

    private Map<Long, Transfer> transferMap = new ConcurrentHashMap<>();
    private final static TransferDao INSTANCE = new TransferDao();
    private Dao<Account> accountDao = AccountDao.getInstance();

    private TransferDao() {
    }

    public static TransferDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Transfer> get(long id) {
        return Optional.ofNullable(transferMap.get(id));
    }

    @Override
    public List<Transfer> getAll() {
        return new ArrayList<>(transferMap.values());
    }

    @Override
    public void save(Transfer transfer) {
        transferMap.put(transfer.getId(), transfer);
        transfer(accountDao.get(transfer.getSourceAccountId()).get(), accountDao.get(transfer.getDestinationAccountId()).get(),
                transfer.getAmount());
    }

    @Override
    public void update(Transfer transfer) {
        transferMap.put(transfer.getId(), transfer);
    }

    @Override
    public void delete(Transfer transfer) {
        transferMap.remove(transfer.getId());
    }

    private void transfer(Account acc1, Account acc2, BigDecimal value) {
        Object lock1 = acc1.getId() < acc2.getId() ? acc1.getLock() : acc2.getLock();
        Object lock2 = acc1.getId() < acc2.getId() ? acc2.getLock() : acc1.getLock();
        synchronized (lock1) {
            synchronized (lock2) {
                acc1.withdraw(value);
                acc2.deposit(value);
            }
        }
    }
}
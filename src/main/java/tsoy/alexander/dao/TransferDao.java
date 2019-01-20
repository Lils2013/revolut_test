package tsoy.alexander.dao;

import tsoy.alexander.model.Transfer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransferDao implements Dao<Transfer> {

    private Map<Long, Transfer> transferMap = new ConcurrentHashMap<>();

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
    }

    @Override
    public void update(Transfer transfer) {
        transferMap.put(transfer.getId(), transfer);
    }

    @Override
    public void delete(Transfer transfer) {
        transferMap.remove(transfer.getId());
    }
}
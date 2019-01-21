package tsoy.alexander.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class Transfer {

    private static final AtomicLong COUNTER = new AtomicLong();

    private Long id;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private BigDecimal amount;
    private LocalDateTime localDateTime;
    private TransferStatus result;

    public Transfer(Long sourceAccountId, Long destinationAccountId, BigDecimal amount) {
        this.id = COUNTER.getAndIncrement();
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.localDateTime = LocalDateTime.now();
    }

    public Transfer() {
    }

    public Long getId() {
        return id;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public TransferStatus getResult() {
        return result;
    }

    public void setResult(TransferStatus result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", sourceAccountId=" + sourceAccountId +
                ", destinationAccountId=" + destinationAccountId +
                ", amount=" + amount +
                ", localDateTime=" + localDateTime +
                '}';
    }

    public enum TransferStatus {
        SUCCESSFUL,
        FAILED
    }
}

package domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 거래 엔티티
 */
public class Transaction {
    private Long transactionId;
    private final TransactionType type;
    private final Money amount;
    private final Money balanceAfter;
    private final LocalDateTime transactionAt;
    private final String description;

    public Transaction(TransactionType type, Money amount, Money balanceAfter, String description) {
        this.type = validateTransactionType(type);
        this.amount = validateAmount(amount);
        this.balanceAfter = validateBalanceAfter(balanceAfter);
        this.transactionAt = LocalDateTime.now();
        this.description = description;
    }

    // 생성자 - 기존 거래 (DB에서 조회)
    public Transaction(Long transactionId, TransactionType type, Money amount,
                       Money balanceAfter, LocalDateTime transactionAt, String description) {
        this.transactionId = transactionId;
        this.type = validateTransactionType(type);
        this.amount = validateAmount(amount);
        this.balanceAfter = validateBalanceAfter(balanceAfter);
        this.transactionAt = transactionAt;
        this.description = description;
    }

    private TransactionType validateTransactionType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("거래 유형은 필수입니다.");
        }
        return type;
    }

    private Money validateAmount(Money amount) {
        if (amount == null || amount.isZero()) {
            throw new IllegalArgumentException("거래 금액은 0보다 커야 합니다.");
        }
        return amount;
    }

    private Money validateBalanceAfter(Money balanceAfter) {
        if (balanceAfter == null) {
            throw new IllegalArgumentException("거래 후 잔액은 필수입니다.");
        }
        return balanceAfter;
    }

    // Getters
    public Long getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public Money getAmount() { return amount; }
    public Money getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getTransactionAt() { return transactionAt; }
    public String getDescription() { return description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}

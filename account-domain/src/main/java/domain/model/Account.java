package domain.model;

import domain.exception.InactiveAccountException;
import domain.exception.InsufficientBalanceException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 거래 애그리거트 루트
 */
public class Account {
    private Long accountId;
    private final AccountNumber accountNumber;
    private Money balance;
    private AccountStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version; // 낙관적 락용

    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * 생성자 - 새 계좌
     * @param accountNumber
     * @param initialBalance
     */
    public Account(AccountNumber accountNumber, Money initialBalance) {
        this.accountNumber = validateAccountNumber(accountNumber);
        this.balance = validateInitialBalance(initialBalance);
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0L;
    }

    /**
     * 생성자 - 기존 계좌 (DB에서 조회)
     * @param accountId
     * @param accountNumber
     * @param balance
     * @param status
     * @param createdAt
     * @param updatedAt
     * @param version
     */
    public Account(Long accountId, AccountNumber accountNumber, Money balance,
                   AccountStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, Long version) {
        this.accountId = accountId;
        this.accountNumber = validateAccountNumber(accountNumber);
        this.balance = validateBalance(balance);
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    // ============== 비즈니스 메서드 ==============

    /**
     * 출금 메서드
     * @param amount
     * @param description
     * @return
     */
    public Transaction withdraw(Money amount, String description) {
        validateAccountActive();
        validateWithdrawAmount(amount);
        validateSufficientBalance(amount);

        // 잔액 차감
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();

        // 거래 기록 생성
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, amount, this.balance, description);
        this.transactions.add(transaction);
        return transaction;
    }

    /**
     * 입금 메서드
     * @param amount
     * @param description
     * @return
     */
    public Transaction deposit(Money amount, String description) {
        validateAccountActive();
        validateDepositAmount(amount);

        // 잔액 증가
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();

        // 거래 기록 생성
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, this.balance, description);
        this.transactions.add(transaction);
        return transaction;
    }

    /**
     * 계좌 비활성화
     */
    public void deactivate() {
        this.status = AccountStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 계좌 활성화
     */
    public void activate() {
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    // ============== 검증 메서드 ==============

    private AccountNumber validateAccountNumber(AccountNumber accountNumber) {
        if (accountNumber == null) {
            throw new IllegalArgumentException("계좌번호는 필수입니다.");
        }
        return accountNumber;
    }

    private Money validateInitialBalance(Money balance) {
        if (balance == null) {
            throw new IllegalArgumentException("초기 잔액은 필수입니다.");
        }
        return balance;
    }

    private Money validateBalance(Money balance) {
        if (balance == null) {
            throw new IllegalArgumentException("잔액은 필수입니다.");
        }
        return balance;
    }

    private void validateAccountActive() {
        if (!status.isActive()) {
            throw new InactiveAccountException("비활성 상태의 계좌는 거래할 수 없습니다.");
        }
    }

    private void validateWithdrawAmount(Money amount) {
        if (amount == null || amount.isZero()) {
            throw new IllegalArgumentException("출금 금액은 0보다 커야 합니다.");
        }
    }

    private void validateDepositAmount(Money amount) {
        if (amount == null || amount.isZero()) {
            throw new IllegalArgumentException("입금 금액은 0보다 커야 합니다.");
        }
    }

    private void validateSufficientBalance(Money amount) {
        if (!balance.isGreaterThanOrEqual(amount)) {
            throw new InsufficientBalanceException(String.format("잔액이 부족합니다. 현재 잔액: %s, 출금 요청: %s", balance.toString(), amount.toString()));
        }
    }
}

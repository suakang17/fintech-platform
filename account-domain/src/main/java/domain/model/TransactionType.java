package domain.model;

public enum TransactionType {
    DEPOSIT("입금"),
    WITHDRAW("출금"),
    TRANSFER_OUT("이체출금"),
    TRANSFER_IN("이체입금");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isWithdrawal() {
        return this == WITHDRAW || this == TRANSFER_OUT;
    }
}

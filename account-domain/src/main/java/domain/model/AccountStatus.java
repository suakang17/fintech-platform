package domain.model;

/**
 * 계좌 상태 enum
 */
public enum AccountStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    FROZEN("동결");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}

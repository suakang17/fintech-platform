package domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 계좌번호 값 객체
 */
public class AccountNumber {
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{10,20}$");

    private final String value;

    public AccountNumber(String value) {
        validateAccountNumber(value);
        this.value = value;
    }

    private void validateAccountNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("계좌번호는 필수입니다.");
        }

        if (!ACCOUNT_NUMBER_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("계좌번호는 10-20자리 숫자여야 합니다.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountNumber that = (AccountNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

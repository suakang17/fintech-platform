package adapter.web.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 잔액 조회 응답 DTO
 * 
 * @author Fintech Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "실시간 잔액 정보")
public class BalanceResponse {

    @Schema(description = "계좌번호", example = "1001234567890")
    @JsonProperty("account_number")
    private String accountNumber;

    @Schema(description = "현재 잔액", example = "1500000.00")
    @JsonProperty("balance")
    private BigDecimal balance;

    @Schema(description = "사용 가능 금액", example = "1500000.00")
    @JsonProperty("available_balance")
    private BigDecimal availableBalance;

    @Schema(description = "보류 금액", example = "0.00")
    @JsonProperty("hold_amount")
    private BigDecimal holdAmount;

    @Schema(description = "마지막 거래 후 잔액 업데이트 시간", example = "2025-07-12T14:25:30.123")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonProperty("last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @Schema(description = "조회 시점", example = "2025-07-12T14:30:45.567")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonProperty("retrieved_at")
    private LocalDateTime retrievedAt;

    @Schema(description = "계좌 상태", example = "ACTIVE")
    @JsonProperty("account_status")
    private String accountStatus;

    @Schema(description = "일일 출금 한도", example = "5000000.00")
    @JsonProperty("daily_withdrawal_limit")
    private BigDecimal dailyWithdrawalLimit;

    @Schema(description = "일일 출금 사용 금액", example = "300000.00")
    @JsonProperty("daily_withdrawal_used")
    private BigDecimal dailyWithdrawalUsed;

    @Schema(description = "일일 출금 가능 금액", example = "4700000.00")
    @JsonProperty("daily_withdrawal_available")
    private BigDecimal dailyWithdrawalAvailable;

    /**
     * 출금 가능 여부 확인
     */
    public boolean canWithdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        // 잔액 확인
        if (availableBalance == null || availableBalance.compareTo(amount) < 0) {
            return false;
        }
        
        // 일일 한도 확인
        if (dailyWithdrawalAvailable != null && dailyWithdrawalAvailable.compareTo(amount) < 0) {
            return false;
        }
        
        return "ACTIVE".equals(accountStatus);
    }

    /**
     * 잔액 안전성 체크
     */
    public boolean isBalanceHealthy() {
        return balance != null && 
               balance.compareTo(BigDecimal.ZERO) >= 0 && 
               availableBalance != null && 
               availableBalance.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * 팩토리 메서드 - 조회 시점 자동 설정
     */
    public static BalanceResponse of(String accountNumber, BigDecimal balance, 
                                   BigDecimal availableBalance, String accountStatus) {
        return BalanceResponse.builder()
                .accountNumber(accountNumber)
                .balance(balance)
                .availableBalance(availableBalance)
                .holdAmount(balance.subtract(availableBalance))
                .accountStatus(accountStatus)
                .retrievedAt(LocalDateTime.now())
                .build();
    }
}

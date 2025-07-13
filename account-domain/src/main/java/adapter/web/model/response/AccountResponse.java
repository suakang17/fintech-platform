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
 * 계좌 정보 응답 DTO
 * 
 * @author Fintech Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계좌 정보 응답")
public class AccountResponse {

    @Schema(description = "계좌번호", example = "1001234567890")
    @JsonProperty("account_number")
    private String accountNumber;

    @Schema(description = "현재 잔액", example = "1500000.00")
    @JsonProperty("balance")
    private BigDecimal balance;

    @Schema(description = "계좌 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "FROZEN"})
    @JsonProperty("status")
    private String status;

    @Schema(description = "계좌 개설일", example = "2025-01-15T09:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정일", example = "2025-07-12T14:25:30")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "계좌 별칭", example = "주거래 통장")
    @JsonProperty("account_alias")
    private String accountAlias;

    @Schema(description = "계좌 타입", example = "CHECKING", allowableValues = {"CHECKING", "SAVINGS", "INVESTMENT"})
    @JsonProperty("account_type")
    private String accountType;

    @Schema(description = "사용 가능 금액 (잔액 - 보류 금액)", example = "1500000.00")
    @JsonProperty("available_balance")
    private BigDecimal availableBalance;

    @Schema(description = "보류 금액", example = "0.00")
    @JsonProperty("hold_amount")
    private BigDecimal holdAmount;

    /**
     * 잔액 마스킹 (보안용)
     */
    public String getMaskedBalance() {
        if (balance == null) return "***";
        String balanceStr = balance.toString();
        if (balanceStr.length() <= 3) return "***";
        return balanceStr.substring(0, balanceStr.length() - 3) + "***";
    }

    /**
     * 계좌 활성 상태 확인
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    /**
     * 사용 가능 금액 계산
     */
    public BigDecimal calculateAvailableBalance() {
        if (balance == null) return BigDecimal.ZERO;
        if (holdAmount == null) return balance;
        return balance.subtract(holdAmount);
    }
}

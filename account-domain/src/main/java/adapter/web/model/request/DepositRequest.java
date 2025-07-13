package adapter.web.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 입금 요청 DTO
 * 
 * @author Fintech Platform Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "입금 요청")
public class DepositRequest {

    @Schema(description = "입금 금액", example = "100000.00", required = true)
    @NotNull(message = "입금 금액은 필수입니다")
    @DecimalMin(value = "0.01", message = "입금 금액은 0.01원 이상이어야 합니다")
    @DecimalMax(value = "10000000.00", message = "입금 금액은 1천만원을 초과할 수 없습니다")
    @Digits(integer = 10, fraction = 2, message = "금액 형식이 올바르지 않습니다")
    @JsonProperty("amount")
    private BigDecimal amount;

    @Schema(description = "거래 설명", example = "급여 입금", required = true)
    @NotBlank(message = "거래 설명은 필수입니다")
    @Size(min = 1, max = 100, message = "거래 설명은 1자 이상 100자 이하여야 합니다")
    @JsonProperty("description")
    private String description;

    @Schema(description = "클라이언트 거래 ID (선택사항)", example = "DEP_20250712_001")
    @Size(max = 50, message = "거래 ID는 50자를 초과할 수 없습니다")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "거래 ID는 영문, 숫자, _, - 만 사용 가능합니다")
    @JsonProperty("transaction_id")
    private String transactionId;

    @Schema(description = "입금 출처", example = "SALARY")
    @Size(max = 20, message = "입금 출처는 20자를 초과할 수 없습니다")
    @JsonProperty("source_type")
    private String sourceType;

    @Schema(description = "입금자 정보", example = "홍길동")
    @Size(max = 50, message = "입금자 정보는 50자를 초과할 수 없습니다")
    @JsonProperty("depositor_info")
    private String depositorInfo;

    /**
     * 금액 검증 로직
     */
    public boolean isValidAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 거래 ID 생성 (없는 경우)
     */
    public String getOrGenerateTransactionId() {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return "DEP_" + System.currentTimeMillis();
        }
        return transactionId;
    }
}

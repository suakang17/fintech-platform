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
 * 거래 정보 응답 DTO
 * 
 * @author Fintech Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "거래 정보 응답")
public class TransactionResponse {

    @Schema(description = "거래 고유 ID", example = "TXN_20250712_14250001")
    @JsonProperty("transaction_id")
    private String transactionId;

    @Schema(description = "계좌번호", example = "1001234567890")
    @JsonProperty("account_number")
    private String accountNumber;

    @Schema(description = "거래 유형", example = "WITHDRAW", 
            allowableValues = {"DEPOSIT", "WITHDRAW", "TRANSFER_IN", "TRANSFER_OUT"})
    @JsonProperty("transaction_type")
    private String transactionType;

    @Schema(description = "거래 금액", example = "50000.00")
    @JsonProperty("amount")
    private BigDecimal amount;

    @Schema(description = "거래 후 잔액", example = "1450000.00")
    @JsonProperty("balance_after")
    private BigDecimal balanceAfter;

    @Schema(description = "거래 전 잔액", example = "1500000.00")
    @JsonProperty("balance_before")
    private BigDecimal balanceBefore;

    @Schema(description = "거래 설명", example = "ATM 출금")
    @JsonProperty("description")
    private String description;

    @Schema(description = "거래 일시", example = "2025-07-12T14:25:30.123")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonProperty("transaction_at")
    private LocalDateTime transactionAt;

    @Schema(description = "거래 상태", example = "SUCCESS", 
            allowableValues = {"SUCCESS", "FAILED", "PENDING", "CANCELLED"})
    @JsonProperty("status")
    private String status;

    @Schema(description = "거래 채널", example = "API", 
            allowableValues = {"API", "ATM", "BRANCH", "MOBILE", "WEB"})
    @JsonProperty("channel")
    private String channel;

    @Schema(description = "거래 수수료", example = "1000.00")
    @JsonProperty("fee_amount")
    private BigDecimal feeAmount;

    @Schema(description = "사유 코드", example = "ATM_WITHDRAWAL")
    @JsonProperty("reason_code")
    private String reasonCode;

    @Schema(description = "상대방 계좌번호 (이체 시)", example = "2001234567890")
    @JsonProperty("counterpart_account")
    private String counterpartAccount;

    @Schema(description = "상대방 이름 (이체 시)", example = "김철수")
    @JsonProperty("counterpart_name")
    private String counterpartName;

    @Schema(description = "요청 ID (추적용)", example = "REQ_20250712_142530")
    @JsonProperty("request_id")
    private String requestId;

    @Schema(description = "멱등성 키", example = "IDEM_20250712_142530_001")
    @JsonProperty("idempotency_key")
    private String idempotencyKey;

    /**
     * 거래 성공 여부 확인
     */
    public boolean isSuccessful() {
        return "SUCCESS".equals(status);
    }

    /**
     * 거래 진행 중 여부 확인
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * 출금 거래 여부 확인
     */
    public boolean isWithdrawal() {
        return "WITHDRAW".equals(transactionType) || "TRANSFER_OUT".equals(transactionType);
    }

    /**
     * 입금 거래 여부 확인
     */
    public boolean isDeposit() {
        return "DEPOSIT".equals(transactionType) || "TRANSFER_IN".equals(transactionType);
    }

    /**
     * 실제 변동 금액 계산 (수수료 포함)
     */
    public BigDecimal getTotalAmount() {
        BigDecimal total = amount;
        if (feeAmount != null && feeAmount.compareTo(BigDecimal.ZERO) > 0) {
            total = total.add(feeAmount);
        }
        return total;
    }

    /**
     * 팩토리 메서드 - 성공 거래
     */
    public static TransactionResponse success(String transactionId, String accountNumber,
                                            String transactionType, BigDecimal amount,
                                            BigDecimal balanceAfter, String description) {
        return TransactionResponse.builder()
                .transactionId(transactionId)
                .accountNumber(accountNumber)
                .transactionType(transactionType)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .transactionAt(LocalDateTime.now())
                .status("SUCCESS")
                .channel("API")
                .build();
    }
}

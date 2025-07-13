package adapter.web.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 에러 응답 DTO
 * 
 * @author Fintech Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "INSUFFICIENT_BALANCE")
    @JsonProperty("error_code")
    private String errorCode;

    @Schema(description = "에러 메시지", example = "잔액이 부족합니다.")
    @JsonProperty("error_message")
    private String errorMessage;

    @Schema(description = "상세 메시지", example = "현재 잔액: 30,000원, 요청 금액: 50,000원")
    @JsonProperty("detail_message")
    private String detailMessage;

    @Schema(description = "요청 ID (추적용)", example = "REQ_20250712_142530")
    @JsonProperty("request_id")
    private String requestId;

    @Schema(description = "에러 발생 시간", example = "2025-07-12T14:25:30.123")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "요청 경로", example = "/api/v1/accounts/1001234567890/withdraw")
    @JsonProperty("path")
    private String path;

    @Schema(description = "HTTP 메서드", example = "POST")
    @JsonProperty("method")
    private String method;

    @Schema(description = "검증 오류 목록")
    @JsonProperty("validation_errors")
    private List<ValidationError> validationErrors;

    @Schema(description = "추가 정보")
    @JsonProperty("additional_info")
    private Map<String, Object> additionalInfo;

    /**
     * 검증 오류 상세 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "검증 오류 상세")
    public static class ValidationError {
        
        @Schema(description = "필드명", example = "amount")
        @JsonProperty("field")
        private String field;

        @Schema(description = "입력 값", example = "0")
        @JsonProperty("rejected_value")
        private Object rejectedValue;

        @Schema(description = "오류 메시지", example = "출금 금액은 0.01원 이상이어야 합니다")
        @JsonProperty("message")
        private String message;

        @Schema(description = "오류 코드", example = "DecimalMin")
        @JsonProperty("code")
        private String code;
    }

    /**
     * 일반 비즈니스 오류 생성
     */
    public static ErrorResponse businessError(String errorCode, String errorMessage) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 잔액 부족 오류 생성
     */
    public static ErrorResponse insufficientBalance(String currentBalance, String requestAmount) {
        return ErrorResponse.builder()
                .errorCode("INSUFFICIENT_BALANCE")
                .errorMessage("잔액이 부족합니다.")
                .detailMessage(String.format("현재 잔액: %s원, 요청 금액: %s원", currentBalance, requestAmount))
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 계좌 상태 오류 생성
     */
    public static ErrorResponse inactiveAccount(String accountNumber, String status) {
        return ErrorResponse.builder()
                .errorCode("INACTIVE_ACCOUNT")
                .errorMessage("비활성 상태의 계좌는 거래할 수 없습니다.")
                .detailMessage(String.format("계좌번호: %s, 상태: %s", accountNumber, status))
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 계좌 없음 오류 생성
     */
    public static ErrorResponse accountNotFound(String accountNumber) {
        return ErrorResponse.builder()
                .errorCode("ACCOUNT_NOT_FOUND")
                .errorMessage("계좌를 찾을 수 없습니다.")
                .detailMessage(String.format("계좌번호: %s", accountNumber))
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 검증 오류 생성
     */
    public static ErrorResponse validationError(List<ValidationError> validationErrors) {
        return ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .errorMessage("입력 값이 올바르지 않습니다.")
                .validationErrors(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 시스템 오류 생성
     */
    public static ErrorResponse systemError(String message) {
        return ErrorResponse.builder()
                .errorCode("SYSTEM_ERROR")
                .errorMessage("시스템 오류가 발생했습니다.")
                .detailMessage(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 중복 요청 오류 생성
     */
    public static ErrorResponse duplicateRequest(String idempotencyKey) {
        return ErrorResponse.builder()
                .errorCode("DUPLICATE_REQUEST")
                .errorMessage("중복된 요청입니다.")
                .detailMessage(String.format("Idempotency Key: %s", idempotencyKey))
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 요청 ID 설정
     */
    public ErrorResponse withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 경로 정보 설정
     */
    public ErrorResponse withPath(String path, String method) {
        this.path = path;
        this.method = method;
        return this;
    }
}

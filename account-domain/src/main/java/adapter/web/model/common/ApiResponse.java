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

/**
 * API 공통 응답 래퍼
 * 
 * @author Fintech Platform Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API 공통 응답")
public class ApiResponse<T> {

    @Schema(description = "응답 코드", example = "SUCCESS")
    @JsonProperty("code")
    private String code;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    @JsonProperty("message")
    private String message;

    @Schema(description = "응답 데이터")
    @JsonProperty("data")
    private T data;

    @Schema(description = "응답 시간", example = "2025-07-12T14:25:30.123")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "요청 ID (추적용)", example = "REQ_20250712_142530")
    @JsonProperty("request_id")
    private String requestId;

    @Schema(description = "API 버전", example = "v1.0")
    @JsonProperty("version")
    private String version;

    /**
     * 성공 응답 생성 (데이터 있음)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .timestamp(LocalDateTime.now())
                .version("v1.0")
                .build();
    }

    /**
     * 성공 응답 생성 (데이터 있음, 커스텀 메시지)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .version("v1.0")
                .build();
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .timestamp(LocalDateTime.now())
                .version("v1.0")
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("v1.0")
                .build();
    }

    /**
     * 비즈니스 오류 응답 생성
     */
    public static <T> ApiResponse<T> businessError(String message) {
        return ApiResponse.<T>builder()
                .code("BUSINESS_ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("v1.0")
                .build();
    }

    /**
     * 검증 오류 응답 생성
     */
    public static <T> ApiResponse<T> validationError(String message) {
        return ApiResponse.<T>builder()
                .code("VALIDATION_ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .version("v1.0")
                .build();
    }

    /**
     * 시스템 오류 응답 생성
     */
    public static <T> ApiResponse<T> systemError(String message) {
        return ApiResponse.<T>builder()
                .code("SYSTEM_ERROR")
                .message(message != null ? message : "시스템 오류가 발생했습니다.")
                .timestamp(LocalDateTime.now())
                .version("v1.0")
                .build();
    }

    /**
     * 요청 ID 설정
     */
    public ApiResponse<T> withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 성공 여부 확인
     */
    public boolean isSuccess() {
        return "SUCCESS".equals(code);
    }
}

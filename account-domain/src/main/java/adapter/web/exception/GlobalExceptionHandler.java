package adapter.web.exception;

import adapter.web.model.common.ApiResponse;
import adapter.web.model.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 전역 예외 처리기
 * 모든 Controller에서 발생하는 예외를 통합 처리
 *
 * @author Fintech Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Bean Validation 실패 (@Valid 어노테이션)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String requestId = extractRequestId(request);
        log.warn("[{}] 검증 실패 - 경로: {}", requestId, request.getRequestURI());

        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            String errorCode = error.getCode();

            validationErrors.add(ErrorResponse.ValidationError.builder()
                    .field(fieldName)
                    .rejectedValue(rejectedValue)
                    .message(errorMessage)
                    .code(errorCode)
                    .build());
        });

        ErrorResponse errorResponse = ErrorResponse.validationError(validationErrors)
                .withRequestId(requestId)
                .withPath(request.getRequestURI(), request.getMethod());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_ERROR", errorResponse.getErrorMessage()));
    }

    /**
     * Path Variable/Request Parameter 검증 실패
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        String requestId = extractRequestId(request);
        log.warn("[{}] 제약조건 위반 - 경로: {}, 오류: {}", requestId, request.getRequestURI(), ex.getMessage());

        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            Object rejectedValue = violation.getInvalidValue();

            validationErrors.add(ErrorResponse.ValidationError.builder()
                    .field(fieldName)
                    .rejectedValue(rejectedValue)
                    .message(errorMessage)
                    .code("ConstraintViolation")
                    .build());
        }

        ErrorResponse errorResponse = ErrorResponse.validationError(validationErrors)
                .withRequestId(requestId)
                .withPath(request.getRequestURI(), request.getMethod());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_ERROR", errorResponse.getErrorMessage()));
    }

    /**
     * 잘못된 JSON 형식
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String requestId = extractRequestId(request);
        log.warn("[{}] JSON 파싱 실패 - 경로: {}, 오류: {}", requestId, request.getRequestURI(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.businessError("INVALID_JSON", "잘못된 JSON 형식입니다.")
                .withRequestId(requestId)
                .withPath(request.getRequestURI(), request.getMethod());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_JSON", errorResponse.getErrorMessage()));
    }

    /**
     * 타입 불일치 오류
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String requestId = extractRequestId(request);
        log.warn("[{}] 타입 불일치 - 경로: {}, 파라미터: {}, 값: {}",
                requestId, request.getRequestURI(), ex.getName(), ex.getValue());

        String message = String.format("파라미터 '%s'의 값 '%s'이(가) 올바르지 않습니다.", ex.getName(), ex.getValue());

        ErrorResponse errorResponse = ErrorResponse.businessError("TYPE_MISMATCH", message)
                .withRequestId(requestId)
                .withPath(request.getRequestURI(), request.getMethod());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("TYPE_MISMATCH", errorResponse.getErrorMessage()));
    }

    /**
     * 필수 헤더 누락
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestHeaderException(
            MissingRequestHeaderException ex, HttpServletRequest request) {

        String requestId = extractRequestId(request);
        log.warn("[{}] 필수 헤더 누락 - 경로: {}, 헤더: {}", requestId, request.getRequestURI(), ex.getHeaderName());

        String message = String.format("필수 헤더 '%s'가 누락되었습니다.", ex.getHeaderName());

        ErrorResponse errorResponse = ErrorResponse.businessError("MISSING_HEADER", message)
                .withRequestId(requestId)
                .withPath(request.getRequestURI(), request.getMethod());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("MISSING_HEADER", errorResponse.getErrorMessage()));
    }

    /**
     * 지원하지 않는 HTTP 메서드
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        String requestId = extractRequestId(request);
        log.warn("[{}] 지원하지 않는 HTTP 메서드 - 경로: {}, 메서드: {}",
                requestId, request.getRequestURI(), ex.getMethod());

        String message = String.format("HTTP 메서드 '%s'는 지원하지 않습니다. 지원 메서드: %s",
                ex.getMethod(), ex.getSupportedHttpMethods());

        ErrorResponse errorResponse = ErrorResponse.businessError("METHOD_NOT_SUPPORTED", message)
                .withRequestId(requestId)
                .withPath(request.getRequestURI(), request.getMethod());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error("METHOD_NOT_SUPPORTED", errorResponse.getErrorMessage()));
    }

    /**
     * 리소스를 찾을 수 없음 (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {

        String requestId = extractRequestId(request);
        log.warn("[{}] 리소스 없음 - 경로: {}, 메서드: {}",
                requestId, ex.getRequestURL(), ex.getHttpMethod());

        ErrorResponse errorResponse = ErrorResponse.businessError("NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.")
                .withRequestId(requestId)
                .withPath(ex.getRequestURL(), ex.getHttpMethod());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("NOT_FOUND", errorResponse.getErrorMessage()));
    }

    /**
     * 비즈니스 예외 처리 (도메인 예외들)
     */
    @ExceptionHandler({
            // domain.exception 패키지의 예외들
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBusinessExceptions(
            RuntimeException ex, HttpServletRequest request) {

        String requestId = extractRequestId(request);
        log.warn("[{}] 비즈니스 오류 - 경로: {}, 오류: {}",
                requestId, request.getRequestURI(), ex.getMessage());

        // 예외 타입에 따른 HTTP 상태 코드 결정
        HttpStatus status = determineHttpStatus(ex);
        String errorCode = determineErrorCode(ex);

        ErrorResponse errorResponse = ErrorResponse.businessError(errorCode, ex.getMessage())
                .withRequestId(requestId)
                .withPath(request.getRequestURI(), request.getMethod());

        return ResponseEntity.status(status)
                .body(ApiResponse.error(errorCode, errorResponse.getErrorMessage()));
    }

    /**
     * 모든 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {

        String requestId = extractRequestId(request);
        log.error("[{}] 시스템 오류 - 경로: {}, 오류: {}",
                requestId, request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.systemError("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                .withRequestId(requestId)
                .withPath(request.getRequestURI(), request.getMethod());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("SYSTEM_ERROR", errorResponse.getErrorMessage()));
    }

    /**
     * 요청에서 Request ID 추출
     */
    private String extractRequestId(HttpServletRequest request) {
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = "ERR_" + System.currentTimeMillis();
        }
        return requestId;
    }

    /**
     * 예외 타입에 따른 HTTP 상태 코드 결정
     */
    private HttpStatus determineHttpStatus(RuntimeException ex) {
        String exceptionName = ex.getClass().getSimpleName();

        // 도메인 예외들에 대한 매핑
        switch (exceptionName) {
            case "InsufficientBalanceException":
            case "InactiveAccountException":
                return HttpStatus.CONFLICT; // 409
            case "AccountNotFoundException":
            case "TransactionNotFoundException":
                return HttpStatus.NOT_FOUND; // 404
            case "InvalidAmountException":
            case "InvalidAccountNumberException":
                return HttpStatus.BAD_REQUEST; // 400
            case "AccountLockedException":
            case "DailyLimitExceededException":
                return HttpStatus.UNPROCESSABLE_ENTITY; // 422
            default:
                return HttpStatus.BAD_REQUEST; // 400
        }
    }

    /**
     * 예외 타입에 따른 에러 코드 결정
     */
    private String determineErrorCode(RuntimeException ex) {
        String exceptionName = ex.getClass().getSimpleName();

        // 도메인 예외들에 대한 에러 코드 매핑
        switch (exceptionName) {
            case "InsufficientBalanceException":
                return "INSUFFICIENT_BALANCE";
            case "InactiveAccountException":
                return "INACTIVE_ACCOUNT";
            case "AccountNotFoundException":
                return "ACCOUNT_NOT_FOUND";
            case "TransactionNotFoundException":
                return "TRANSACTION_NOT_FOUND";
            case "InvalidAmountException":
                return "INVALID_AMOUNT";
            case "InvalidAccountNumberException":
                return "INVALID_ACCOUNT_NUMBER";
            case "AccountLockedException":
                return "ACCOUNT_LOCKED";
            case "DailyLimitExceededException":
                return "DAILY_LIMIT_EXCEEDED";
            case "DuplicateTransactionException":
                return "DUPLICATE_TRANSACTION";
            default:
                return "BUSINESS_ERROR";
        }
    }
}
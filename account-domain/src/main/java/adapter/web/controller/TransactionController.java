package adapter.web.controller;

import adapter.web.model.common.ApiResponse;
import adapter.web.model.common.ErrorResponse;
import adapter.web.model.request.DepositRequest;
import adapter.web.model.request.WithdrawRequest;
import adapter.web.model.response.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 거래 관리 API Controller
 * 핵심 기능: 입금, 출금 (동시성 제어 적용 예정)
 *
 * @author Fintech Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Transaction Management", description = "거래 관리 API")
public class TransactionController {
    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    // TODO: TransactionService 의존성 주입 예정
    // private final TransactionService transactionService;

    @Operation(
            summary = "출금 처리",
            description = "계좌에서 지정된 금액을 출금합니다. 동시성 제어가 적용되어 안전한 거래를 보장합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "출금 성공",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "잔액 부족 또는 계좌 상태 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422",
                    description = "비즈니스 규칙 위반",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @Parameter(description = "계좌번호 (10-20자리)", example = "1001234567890", required = true)
            @PathVariable
            @Pattern(regexp = "^[0-9]{10,20}$", message = "계좌번호는 10-20자리 숫자여야 합니다")
            String accountNumber,

            @Parameter(description = "출금 요청 데이터", required = true)
            @Valid @RequestBody
            WithdrawRequest request,

            @Parameter(description = "멱등성 키 (중복 요청 방지)", example = "IDEM_20250712_142530_001")
            @RequestHeader(value = "Idempotency-Key", required = false)
            String idempotencyKey,

            @Parameter(description = "요청 추적 ID", example = "REQ_20250712_142530")
            @RequestHeader(value = "X-Request-ID", required = false)
            String requestId) {

        // 요청 ID 생성
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = "WTH_" + System.currentTimeMillis();
        }

        // 멱등성 키 생성
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            idempotencyKey = "IDEM_" + UUID.randomUUID().toString().substring(0, 8);
        }

        log.info("[{}] 출금 요청 - 계좌번호: {}, 금액: {}, 설명: {}, 멱등성키: {}",
                requestId, accountNumber, request.getAmount(), request.getDescription(), idempotencyKey);

        try {
            // TODO: 실제 서비스 로직 구현 예정
            // @DistributedLock 애너테이션이 적용된 서비스 메서드 호출
            // TransactionResponse transaction = transactionService.withdraw(accountNumber, request, idempotencyKey);

            // 임시 응답 (개발 단계)
            BigDecimal currentBalance = BigDecimal.valueOf(1500000.00);
            BigDecimal afterBalance = currentBalance.subtract(request.getAmount());

            // 잔액 부족 시뮬레이션
            if (afterBalance.compareTo(BigDecimal.ZERO) < 0) {
                log.warn("[{}] 출금 실패 - 잔액 부족: 현재 {}, 요청 {}",
                        requestId, currentBalance, request.getAmount());

                ErrorResponse errorResponse = ErrorResponse.insufficientBalance(
                                currentBalance.toString(), request.getAmount().toString())
                        .withRequestId(requestId)
                        .withPath("/api/v1/accounts/" + accountNumber + "/withdraw", "POST");

                return ResponseEntity.status(409)
                        .body(ApiResponse.error("INSUFFICIENT_BALANCE", errorResponse.getErrorMessage()));
            }

            TransactionResponse mockResponse = TransactionResponse.builder()
                    .transactionId(request.getOrGenerateTransactionId())
                    .accountNumber(accountNumber)
                    .transactionType("WITHDRAW")
                    .amount(request.getAmount())
                    .balanceAfter(afterBalance)
                    .balanceBefore(currentBalance)
                    .description(request.getDescription())
                    .transactionAt(LocalDateTime.now())
                    .status("SUCCESS")
                    .channel("API")
                    .reasonCode(request.getReasonCode())
                    .requestId(requestId)
                    .idempotencyKey(idempotencyKey)
                    .build();

            log.info("[{}] 출금 성공 - 계좌번호: {}, 금액: {}, 거래후잔액: {}, 거래ID: {}",
                    requestId, accountNumber, request.getAmount(), afterBalance, mockResponse.getTransactionId());

            return ResponseEntity.ok(
                    ApiResponse.success(mockResponse, "출금이 성공적으로 처리되었습니다.")
                            .withRequestId(requestId)
            );

        } catch (Exception e) {
            log.error("[{}] 출금 처리 실패 - 계좌번호: {}, 금액: {}, 오류: {}",
                    requestId, accountNumber, request.getAmount(), e.getMessage(), e);

            ErrorResponse errorResponse = ErrorResponse.systemError("출금 처리 중 시스템 오류가 발생했습니다.")
                    .withRequestId(requestId)
                    .withPath("/api/v1/accounts/" + accountNumber + "/withdraw", "POST");

            return ResponseEntity.status(500)
                    .body(ApiResponse.error("SYSTEM_ERROR", errorResponse.getErrorMessage()));
        }
    }

    @Operation(
            summary = "입금 처리",
            description = "계좌에 지정된 금액을 입금합니다. 동시성 제어가 적용되어 안전한 거래를 보장합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "입금 성공",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "계좌 상태 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @Parameter(description = "계좌번호 (10-20자리)", example = "1001234567890", required = true)
            @PathVariable
            @Pattern(regexp = "^[0-9]{10,20}$", message = "계좌번호는 10-20자리 숫자여야 합니다")
            String accountNumber,

            @Parameter(description = "입금 요청 데이터", required = true)
            @Valid @RequestBody
            DepositRequest request,

            @Parameter(description = "멱등성 키 (중복 요청 방지)", example = "IDEM_20250712_142530_002")
            @RequestHeader(value = "Idempotency-Key", required = false)
            String idempotencyKey,

            @Parameter(description = "요청 추적 ID", example = "REQ_20250712_142530")
            @RequestHeader(value = "X-Request-ID", required = false)
            String requestId) {

        // 요청 ID 생성
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = "DEP_" + System.currentTimeMillis();
        }

        // 멱등성 키 생성
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            idempotencyKey = "IDEM_" + UUID.randomUUID().toString().substring(0, 8);
        }

        log.info("[{}] 입금 요청 - 계좌번호: {}, 금액: {}, 설명: {}, 멱등성키: {}",
                requestId, accountNumber, request.getAmount(), request.getDescription(), idempotencyKey);

        try {
            // TODO: 실제 서비스 로직 구현 예정
            // @DistributedLock 애너테이션이 적용된 서비스 메서드 호출
            // TransactionResponse transaction = transactionService.deposit(accountNumber, request, idempotencyKey);

            // 임시 응답 (개발 단계)
            BigDecimal currentBalance = BigDecimal.valueOf(1500000.00);
            BigDecimal afterBalance = currentBalance.add(request.getAmount());

            TransactionResponse mockResponse = TransactionResponse.builder()
                    .transactionId(request.getOrGenerateTransactionId())
                    .accountNumber(accountNumber)
                    .transactionType("DEPOSIT")
                    .amount(request.getAmount())
                    .balanceAfter(afterBalance)
                    .balanceBefore(currentBalance)
                    .description(request.getDescription())
                    .transactionAt(LocalDateTime.now())
                    .status("SUCCESS")
                    .channel("API")
                    .counterpartName(request.getDepositorInfo())
                    .requestId(requestId)
                    .idempotencyKey(idempotencyKey)
                    .build();

            log.info("[{}] 입금 성공 - 계좌번호: {}, 금액: {}, 거래후잔액: {}, 거래ID: {}",
                    requestId, accountNumber, request.getAmount(), afterBalance, mockResponse.getTransactionId());

            return ResponseEntity.ok(
                    ApiResponse.success(mockResponse, "입금이 성공적으로 처리되었습니다.")
                            .withRequestId(requestId)
            );

        } catch (Exception e) {
            log.error("[{}] 입금 처리 실패 - 계좌번호: {}, 금액: {}, 오류: {}",
                    requestId, accountNumber, request.getAmount(), e.getMessage(), e);

            ErrorResponse errorResponse = ErrorResponse.systemError("입금 처리 중 시스템 오류가 발생했습니다.")
                    .withRequestId(requestId)
                    .withPath("/api/v1/accounts/" + accountNumber + "/deposit", "POST");

            return ResponseEntity.status(500)
                    .body(ApiResponse.error("SYSTEM_ERROR", errorResponse.getErrorMessage()));
        }
    }

    @Operation(
            summary = "거래 상세 조회",
            description = "거래 ID로 특정 거래의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "거래를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
            @Parameter(description = "거래 ID", example = "TXN_20250712_14250001", required = true)
            @PathVariable
            String transactionId,

            @Parameter(description = "요청 추적 ID", example = "REQ_20250712_142530")
            @RequestHeader(value = "X-Request-ID", required = false)
            String requestId) {

        // 요청 ID 생성
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = "TXN_" + System.currentTimeMillis();
        }

        log.info("[{}] 거래 상세 조회 요청 - 거래ID: {}", requestId, transactionId);

        try {
            // TODO: TransactionService를 통한 거래 조회 로직 구현 예정
            // TransactionResponse transaction = transactionService.getTransaction(transactionId);

            // 임시 응답 (개발 단계)
            TransactionResponse mockResponse = TransactionResponse.builder()
                    .transactionId(transactionId)
                    .accountNumber("1001234567890")
                    .transactionType("WITHDRAW")
                    .amount(BigDecimal.valueOf(50000.00))
                    .balanceAfter(BigDecimal.valueOf(1450000.00))
                    .balanceBefore(BigDecimal.valueOf(1500000.00))
                    .description("ATM 출금")
                    .transactionAt(LocalDateTime.now().minusHours(2))
                    .status("SUCCESS")
                    .channel("API")
                    .reasonCode("ATM_WITHDRAWAL")
                    .build();

            log.info("[{}] 거래 상세 조회 성공 - 거래ID: {}, 계좌번호: {}, 금액: {}",
                    requestId, transactionId, mockResponse.getAccountNumber(), mockResponse.getAmount());

            return ResponseEntity.ok(
                    ApiResponse.success(mockResponse, "거래 정보를 성공적으로 조회했습니다.")
                            .withRequestId(requestId)
            );

        } catch (Exception e) {
            log.error("[{}] 거래 조회 실패 - 거래ID: {}, 오류: {}",
                    requestId, transactionId, e.getMessage(), e);

            ErrorResponse errorResponse = ErrorResponse.businessError("TRANSACTION_NOT_FOUND", "거래를 찾을 수 없습니다.")
                    .withRequestId(requestId)
                    .withPath("/api/v1/accounts/transactions/" + transactionId, "GET");

            return ResponseEntity.status(404)
                    .body(ApiResponse.error("TRANSACTION_NOT_FOUND", errorResponse.getErrorMessage()));
        }
    }
}
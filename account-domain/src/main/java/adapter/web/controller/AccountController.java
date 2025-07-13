package adapter.web.controller;

import adapter.web.model.common.ApiResponse;
import adapter.web.model.common.ErrorResponse;
import adapter.web.model.response.AccountResponse;
import adapter.web.model.response.BalanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 계좌 관리 API Controller
 *
 * @author Fintech Platform Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Account Management", description = "계좌 관리 API")
public class AccountController {
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    // TODO: AccountService 의존성 주입 예정
    // private final AccountService accountService;

    @Operation(
            summary = "계좌 정보 조회",
            description = "계좌번호로 계좌의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 계좌번호 형식",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @Parameter(description = "계좌번호 (10-20자리)", example = "1001234567890", required = true)
            @PathVariable
            @Pattern(regexp = "^[0-9]{10,20}$", message = "계좌번호는 10-20자리 숫자여야 합니다")
            String accountNumber,

            @Parameter(description = "요청 추적 ID", example = "REQ_20250712_142530")
            @RequestHeader(value = "X-Request-ID", required = false)
            String requestId) {

        // 요청 ID 생성 (없는 경우)
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = "REQ_" + System.currentTimeMillis();
        }

        log.info("[{}] 계좌 정보 조회 요청 - 계좌번호: {}", requestId, accountNumber);

        try {
            // TODO: AccountService를 통한 계좌 조회 로직 구현 예정
            // AccountResponse account = accountService.getAccount(accountNumber);

            // 임시 응답 (개발 단계)
            AccountResponse mockResponse = AccountResponse.builder()
                    .accountNumber(accountNumber)
                    .balance(java.math.BigDecimal.valueOf(1500000.00))
                    .status("ACTIVE")
                    .createdAt(java.time.LocalDateTime.now().minusDays(30))
                    .updatedAt(java.time.LocalDateTime.now())
                    .accountAlias("주거래 통장")
                    .accountType("CHECKING")
                    .availableBalance(java.math.BigDecimal.valueOf(1500000.00))
                    .holdAmount(java.math.BigDecimal.ZERO)
                    .build();

            log.info("[{}] 계좌 정보 조회 성공 - 계좌번호: {}, 잔액: {}",
                    requestId, accountNumber, mockResponse.getBalance());

            return ResponseEntity.ok(
                    ApiResponse.success(mockResponse, "계좌 정보를 성공적으로 조회했습니다.")
                            .withRequestId(requestId)
            );

        } catch (Exception e) {
            log.error("[{}] 계좌 정보 조회 실패 - 계좌번호: {}, 오류: {}",
                    requestId, accountNumber, e.getMessage(), e);

            ErrorResponse errorResponse = ErrorResponse.accountNotFound(accountNumber)
                    .withRequestId(requestId)
                    .withPath("/api/v1/accounts/" + accountNumber, "GET");

            return ResponseEntity.status(404)
                    .body(ApiResponse.error("ACCOUNT_NOT_FOUND", errorResponse.getErrorMessage()));
        }
    }

    @Operation(
            summary = "실시간 잔액 조회",
            description = "계좌의 실시간 잔액 정보를 조회합니다. 사용 가능 금액, 보류 금액 등을 포함합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BalanceResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(
            @Parameter(description = "계좌번호 (10-20자리)", example = "1001234567890", required = true)
            @PathVariable
            @Pattern(regexp = "^[0-9]{10,20}$", message = "계좌번호는 10-20자리 숫자여야 합니다")
            String accountNumber,

            @Parameter(description = "요청 추적 ID", example = "REQ_20250712_142530")
            @RequestHeader(value = "X-Request-ID", required = false)
            String requestId) {

        // 요청 ID 생성
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = "BAL_" + System.currentTimeMillis();
        }

        log.info("[{}] 잔액 조회 요청 - 계좌번호: {}", requestId, accountNumber);

        try {
            // TODO: AccountService를 통한 실시간 잔액 조회 로직 구현 예정
            // BalanceResponse balance = accountService.getBalance(accountNumber);

            // 임시 응답 (개발 단계)
            BalanceResponse mockResponse = BalanceResponse.builder()
                    .accountNumber(accountNumber)
                    .balance(java.math.BigDecimal.valueOf(1500000.00))
                    .availableBalance(java.math.BigDecimal.valueOf(1500000.00))
                    .holdAmount(java.math.BigDecimal.ZERO)
                    .lastUpdatedAt(java.time.LocalDateTime.now().minusMinutes(5))
                    .retrievedAt(java.time.LocalDateTime.now())
                    .accountStatus("ACTIVE")
                    .dailyWithdrawalLimit(java.math.BigDecimal.valueOf(5000000.00))
                    .dailyWithdrawalUsed(java.math.BigDecimal.valueOf(300000.00))
                    .dailyWithdrawalAvailable(java.math.BigDecimal.valueOf(4700000.00))
                    .build();

            log.info("[{}] 잔액 조회 성공 - 계좌번호: {}, 잔액: {}, 사용가능: {}",
                    requestId, accountNumber, mockResponse.getBalance(), mockResponse.getAvailableBalance());

            return ResponseEntity.ok(
                    ApiResponse.success(mockResponse, "잔액 정보를 성공적으로 조회했습니다.")
                            .withRequestId(requestId)
            );

        } catch (Exception e) {
            log.error("[{}] 잔액 조회 실패 - 계좌번호: {}, 오류: {}",
                    requestId, accountNumber, e.getMessage(), e);

            ErrorResponse errorResponse = ErrorResponse.accountNotFound(accountNumber)
                    .withRequestId(requestId)
                    .withPath("/api/v1/accounts/" + accountNumber + "/balance", "GET");

            return ResponseEntity.status(404)
                    .body(ApiResponse.error("ACCOUNT_NOT_FOUND", errorResponse.getErrorMessage()));
        }
    }

    @Operation(
            summary = "계좌 상태 변경",
            description = "계좌의 상태를 변경합니다. (ACTIVE, INACTIVE, FROZEN)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 상태 값"
            )
    })
    @PatchMapping("/{accountNumber}/status")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccountStatus(
            @Parameter(description = "계좌번호 (10-20자리)", example = "1001234567890", required = true)
            @PathVariable
            @Pattern(regexp = "^[0-9]{10,20}$", message = "계좌번호는 10-20자리 숫자여야 합니다")
            String accountNumber,

            @Parameter(description = "변경할 계좌 상태", example = "INACTIVE")
            @RequestParam
            @Pattern(regexp = "^(ACTIVE|INACTIVE|FROZEN)$", message = "계좌 상태는 ACTIVE, INACTIVE, FROZEN 중 하나여야 합니다")
            String status,

            @Parameter(description = "요청 추적 ID", example = "REQ_20250712_142530")
            @RequestHeader(value = "X-Request-ID", required = false)
            String requestId) {

        // 요청 ID 생성
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = "UPD_" + System.currentTimeMillis();
        }

        log.info("[{}] 계좌 상태 변경 요청 - 계좌번호: {}, 변경할 상태: {}", requestId, accountNumber, status);

        try {
            // TODO: AccountService를 통한 계좌 상태 변경 로직 구현 예정
            // AccountResponse account = accountService.updateAccountStatus(accountNumber, status);

            // 임시 응답 (개발 단계)
            AccountResponse mockResponse = AccountResponse.builder()
                    .accountNumber(accountNumber)
                    .balance(java.math.BigDecimal.valueOf(1500000.00))
                    .status(status)
                    .createdAt(java.time.LocalDateTime.now().minusDays(30))
                    .updatedAt(java.time.LocalDateTime.now())
                    .accountAlias("주거래 통장")
                    .accountType("CHECKING")
                    .availableBalance(java.math.BigDecimal.valueOf(1500000.00))
                    .holdAmount(java.math.BigDecimal.ZERO)
                    .build();

            log.info("[{}] 계좌 상태 변경 성공 - 계좌번호: {}, 새 상태: {}",
                    requestId, accountNumber, status);

            return ResponseEntity.ok(
                    ApiResponse.success(mockResponse, "계좌 상태가 성공적으로 변경되었습니다.")
                            .withRequestId(requestId)
            );

        } catch (Exception e) {
            log.error("[{}] 계좌 상태 변경 실패 - 계좌번호: {}, 상태: {}, 오류: {}",
                    requestId, accountNumber, status, e.getMessage(), e);

            ErrorResponse errorResponse = ErrorResponse.businessError("ACCOUNT_UPDATE_FAILED", "계좌 상태 변경에 실패했습니다.")
                    .withRequestId(requestId)
                    .withPath("/api/v1/accounts/" + accountNumber + "/status", "PATCH");

            return ResponseEntity.status(400)
                    .body(ApiResponse.error("ACCOUNT_UPDATE_FAILED", errorResponse.getErrorMessage()));
        }
    }
}
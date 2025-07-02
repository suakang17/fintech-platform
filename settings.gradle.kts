rootProject.name = "fintech-platform"

// 헥사고날 + DDD 모듈 구조
include(
    ":shared-kernel",
    ":platform-infrastructure", 
    ":account-domain",
    ":payment-domain",
    ":transfer-domain",
    ":application-launcher"
)

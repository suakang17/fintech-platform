rootProject.name = "fintech-platform"

include(
    ":shared-kernel",
    ":platform-infrastructure", 
    ":account-domain",
    ":payment-domain",
    ":transfer-domain",
    ":application-launcher"
)

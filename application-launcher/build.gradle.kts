plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

dependencies {
    // 모든 도메인 모듈 포함
    implementation(project(":shared-kernel"))
    implementation(project(":platform-infrastructure"))
    implementation(project(":account-domain"))
    implementation(project(":payment-domain"))
    implementation(project(":transfer-domain"))
    
    // Spring Boot 실행 환경
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // 개발 도구
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// Spring Boot 설정
springBoot {
    mainClass.set("com.fintech.platform.FintechPlatformApplication")
}

// 실행 가능한 JAR 생성
tasks.jar {
    enabled = false
}

tasks.bootJar {
    enabled = true
    archiveClassifier.set("")
    mainClass.set("com.fintech.platform.FintechPlatformApplication")
}

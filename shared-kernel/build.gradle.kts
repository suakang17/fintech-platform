dependencies {
    // Spring Boot 기본
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    
    // JSON 처리
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // 유틸리티
    implementation("org.apache.commons:commons-lang3")
    
    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

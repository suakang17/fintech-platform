dependencies {
    // 프로젝트 의존성
    implementation(project(":shared-kernel"))
    implementation(project(":platform-infrastructure"))
    implementation(project(":account-domain"))
    
    // Spring Boot Web
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // HTTP Client (오픈뱅킹 API 연동용)
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    
    // API 문서화
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")
    
    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

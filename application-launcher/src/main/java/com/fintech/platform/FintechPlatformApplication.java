package com.fintech.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 핀테크 플랫폼 메인 애플리케이션
 */
@SpringBootApplication(scanBasePackages = "com.fintech.platform")
public class FintechPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(FintechPlatformApplication.class, args);
    }
}

plugins {
    id("org.springframework.boot") version "3.2.5" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
}

allprojects {
    group = "com.fintech.platform"
    version = "1.0.0"
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    
    tasks.withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
        options.encoding = "UTF-8"
    }
    
    dependencies {
        // Spring Boot BOM 적용
        add("implementation", platform("org.springframework.boot:spring-boot-dependencies:3.2.5"))
        
        // 공통 의존성
        add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks.register("dockerUp") {
    group = "docker"
    description = "Start development environment (Redis, Kafka, etc.)"
    
    doLast {
        println("🚀 Starting Docker Development Environment...")
        
        exec {
            workingDir = file("docker")
            commandLine("docker-compose", "up", "-d")
        }
        
        println("⏳ Waiting for services to be ready...")
        Thread.sleep(5000)
        
        println("🌐 Access URLs:")
        println("  - Kafka UI: http://localhost:8080")
        println("  - Redis: localhost:6379")
        println("  - Kafka: localhost:9092")
    }
}

tasks.register("dockerDown") {
    group = "docker"
    description = "Stop development environment"
    
    doLast {
        println("🛑 Stopping Fintech Platform Development Environment...")
        
        exec {
            workingDir = file("docker")
            commandLine("docker-compose", "down")
        }
        
        println("✅ All services stopped")
    }
}

tasks.register("dockerStatus") {
    group = "docker" 
    description = "Show status of development environment"
    
    doLast {
        println("📊 Development Environment Status:")
        
        exec {
            workingDir = file("docker")
            commandLine("docker-compose", "ps")
        }
    }
}

tasks.register("dockerLogs") {
    group = "docker"
    description = "Show logs from development environment"
    
    doLast {
        exec {
            workingDir = file("docker")
            commandLine("docker-compose", "logs", "-f")
        }
    }
}
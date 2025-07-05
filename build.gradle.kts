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
    description = "Start development environment (MySQL, Redis, Kafka, etc.)"
    
    doLast {
        println("🚀 Starting Docker Development Environment...")
        
        exec {
            workingDir = file("docker")
            commandLine("docker-compose", "up", "-d")
        }
        
        println("⏳ Waiting for services to be ready...")
        Thread.sleep(10000)  // MySQL 초기화 시간 고려
        
        println("🌐 Access URLs:")
        println("  - MySQL: localhost:3306 (database: fintech_platform)")
        println("  - Redis: localhost:6379")
        println("  - Kafka UI: http://localhost:8080")
        println("  - Kafka: localhost:9092")
        println("")
        println("🔧 Next steps:")
        println("  - Check status: ./gradlew dockerStatus")
        println("  - View logs: ./gradlew dockerLogs")
        println("  - Connect to MySQL: mysql -h 127.0.0.1 -P 3306 -u fintech_user -p fintech_platform")
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
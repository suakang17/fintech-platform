# Application Launcher Module

## 개요

FinTech Platform의 메인 애플리케이션 실행 모듈입니다. 모든 도메인 모듈을 통합하여 단일 애플리케이션으로 실행합니다.

## 모듈 구조

```
application-launcher/
├── src/main/java/
│   └── com/fintech/platform/
│       └── FinTechPlatformApplication.java     # 메인 애플리케이션 클래스
├── src/main/resources/
│   ├── application.yml           # 공통 설정
│   ├── application-local.yml     # 로컬 개발 환경
│   ├── application-dev.yml       # 개발 서버 환경
│   └── application-prod.yml      # 운영 환경
└── build.gradle.kts             # 빌드 설정
```

## 의존성 모듈

- **shared-kernel**: 공통 라이브러리
- **platform-infrastructure**: 인프라스트럭처 (Redis, DB 설정)
- **account-domain**: 계좌 관리 도메인
- **payment-domain**: 결제 처리 도메인
- **transfer-domain**: 이체 서비스 도메인

## 환경별 설정

### Local 환경 (application-local.yml)

**목적**: 로컬 개발 환경

**특징**:
- MySQL: localhost:3306
- Redis: localhost:6379
- Connection Pool: 10개 (소규모)
- DDL 전략: create-drop (자동 테이블 생성/삭제)
- 로깅: DEBUG 레벨 (상세 로깅)
- 보안: 최소 (개발 편의성 우선)

**사용법**:
```bash
# 기본 실행 (local 프로파일)
./gradlew bootRun

# 명시적 프로파일 지정
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Dev 환경 (application-dev.yml)

**목적**: 개발 서버

**특징**:
- MySQL: 개발 서버 (환경변수 설정)
- Redis: 개발 서버 (환경변수 설정)
- Connection Pool: 30개 (중간 규모)
- DDL 전략: validate (스키마 검증만)
- 로깅: INFO 레벨 (적정 로깅)
- 보안: 중간 (환경변수 사용)

**사용법**:
```bash
# 환경변수 설정 예시
export DB_HOST=dev-mysql-server
export DB_USERNAME=dev_user
export DB_PASSWORD=dev_password
export REDIS_HOST=dev-redis-server

# 실행
java -jar -Dspring.profiles.active=dev fintech-platform.jar
```

### Prod 환경 (application-prod.yml)

**목적**: 운영 서버

**특징**:
- MySQL: 운영 DB (모든 정보 환경변수)
- Redis: 운영 Redis (SSL 연결)
- Connection Pool: 50개 (대규모)
- DDL 전략: validate (안전성 우선)
- 로깅: WARN 레벨 (최소 로깅)
- 보안: 최대 (모든 민감정보 암호화)

**사용법**:
```bash
# 환경변수 설정 (필수)
export DB_HOST=prod-mysql-cluster
export DB_USERNAME=prod_user
export DB_PASSWORD=secure_password
export REDIS_HOST=prod-redis-cluster
export REDIS_PASSWORD=redis_password

# 실행
java -jar -Dspring.profiles.active=prod fintech-platform.jar
```

## 데이터베이스 설정

### MySQL 연결 정보

| 환경 | 호스트 | 데이터베이스 | 사용자 | 비밀번호 |
|------|--------|--------------|--------|----------|
| Local | localhost:3306 | fintech_platform | admin | fintech123 |
| Dev | ${DB_HOST} | ${DB_NAME} | ${DB_USERNAME} | ${DB_PASSWORD} |
| Prod | ${DB_HOST} | ${DB_NAME} | ${DB_USERNAME} | ${DB_PASSWORD} |

### Connection Pool 설정

| 설정 | Local | Dev | Prod |
|------|-------|-----|------|
| maximum-pool-size | 10 | 30 | 50 |
| minimum-idle | 5 | 10 | 15 |
| connection-timeout | 20초 | 20초 | 20초 |
| leak-detection-threshold | 60초 | 60초 | 60초 |

## Redis 설정

### 연결 정보

| 환경 | 호스트 | 포트 | 비밀번호 | SSL |
|------|--------|------|----------|-----|
| Local | localhost | 6379 | 없음 | 사용안함 |
| Dev | ${REDIS_HOST} | ${REDIS_PORT} | ${REDIS_PASSWORD} | 사용안함 |
| Prod | ${REDIS_HOST} | ${REDIS_PORT} | ${REDIS_PASSWORD} | 사용 |

### 용도

- **Redisson**: 분산락 구현
- **RedisTemplate**: 캐싱 처리
- **Session Store**: 세션 클러스터링 (향후 확장)

## 애플리케이션 실행

### 로컬 개발

```bash
# 1. MySQL 서비스 시작
brew services start mysql

# 2. Redis 서비스 시작
brew services start redis

# 3. 애플리케이션 실행
./gradlew bootRun
```

### 상태 확인

```bash
# 헬스체크
curl http://localhost:8080/api/actuator/health

# 애플리케이션 정보
curl http://localhost:8080/api/actuator/info

# 메트릭 확인
curl http://localhost:8080/api/actuator/metrics
```

## 로깅 설정

### 로그 파일 위치

- **Local**: `logs/fintech-platform-local.log`
- **Dev**: `logs/fintech-platform-dev.log`
- **Prod**: `logs/fintech-platform-prod.log`

### 로깅 레벨

| 패키지 | Local | Dev | Prod |
|---------|-------|-----|------|
| com.fintech.platform | DEBUG | INFO | WARN |
| org.springframework.data.redis | DEBUG | INFO | WARN |
| org.redisson | DEBUG | INFO | WARN |
| org.hibernate.SQL | DEBUG | INFO | WARN |

## 모니터링

### Actuator 엔드포인트

| 엔드포인트 | 설명 | Local | Dev | Prod |
|------------|------|-------|-----|------|
| /actuator/health | 헬스체크 | 상세정보 | 인증시만 | 기본정보만 |
| /actuator/info | 앱 정보 | 사용 | 사용 | 사용 |
| /actuator/metrics | 메트릭 | 사용 | 사용 | 사용안함 |

### 보안 설정

- **Local**: 인증 없이 모든 엔드포인트 접근 가능
- **Dev**: 관리 엔드포인트는 `/management` 경로로 분리
- **Prod**: 최소한의 엔드포인트만 노출, 인증 필요

## 성능 최적화

### JPA 최적화

- **OSIV 비활성화**: `open-in-view: false`
- **배치 처리**: `default_batch_fetch_size: 100`
- **JDBC 배치**: `batch_size: 50`

### 서버 최적화 (운영환경)

- **Tomcat 스레드**: 200개
- **압축**: JSON, XML, HTML 압축 활성화
- **연결 수**: 최대 8192개

## 문제 해결

### 일반적인 문제

**1. MySQL 연결 실패**
```bash
# MySQL 서비스 확인
brew services list | grep mysql

# MySQL 서비스 시작
brew services start mysql

# 연결 테스트
mysql -u admin -pfintech123 fintech_platform
```

**2. Redis 연결 실패**
```bash
# Redis 서비스 확인
brew services list | grep redis

# Redis 서비스 시작
brew services start redis

# 연결 테스트
redis-cli ping
```

**3. 포트 충돌**
```bash
# 8080 포트 사용 프로세스 확인
lsof -i :8080

# 프로세스 종료
kill -9 [PID]
```

## 환경변수 설정 가이드

### 개발 서버용 환경변수

```bash
# Database
export DB_HOST=your-dev-mysql-host
export DB_PORT=3306
export DB_NAME=fintech_platform
export DB_USERNAME=your-dev-username
export DB_PASSWORD=your-dev-password

# Redis
export REDIS_HOST=your-dev-redis-host
export REDIS_PORT=6379
export REDIS_PASSWORD=your-dev-redis-password
```

### 운영 서버용 환경변수

```bash
# Database
export DB_HOST=your-prod-mysql-host
export DB_PORT=3306
export DB_NAME=your-prod-database-name
export DB_USERNAME=your-prod-username
export DB_PASSWORD=your-secure-password

# Redis
export REDIS_HOST=your-prod-redis-host
export REDIS_PORT=6379
export REDIS_PASSWORD=your-secure-redis-password
```


## .env 파일 사용법

**1. .env.example 파일 생성**:
```bash
# .env.example (GitHub에 올려도 안전)
DB_HOST=your-database-host
DB_USERNAME=your-username
DB_PASSWORD=your-password
REDIS_HOST=your-redis-host
REDIS_PASSWORD=your-redis-password
```

**2. 실제 .env 파일 생성**:
```bash
# .env (실제 값, GitHub에 절대 업로드 금지)
cp .env.example .env
# 실제 값으로 수정
```

**3. .gitignore 설정**:
```gitignore
# 환경변수 파일 제외
.env
.env.local
.env.dev
.env.prod
application-local-secret.yml
**/application-*-secret.yml
```
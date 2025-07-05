# Docker Development Environment

이 디렉터리는 개발을 위한 외부 서비스들(Redis, Kafka 등)을 Docker로 관리합니다.

## Quick Start

```bash
# 개발 환경 시작
./gradlew dockerUp

# 상태 확인
./gradlew dockerStatus

# 로그 확인
./gradlew dockerLogs

# 개발 환경 종료
./gradlew dockerDown
```

## 서비스

| 서비스 | 포트 | 접속 정보 |
|--------|------|-----------|
| Redis | 6379 | password: 환경변수 설정 |
| Kafka | 9092 | - |
| Zookeeper | 2181 | - |
| Kafka UI | 8080 | http://localhost:8080 |

## 버저닝

Docker 이미지 버전들은 `gradle.properties`에서 관리됩니다:

```properties
# gradle.properties
REDIS_VERSION=7
KAFKA_VERSION=7.4.0
```

패스워드는 `.env` 파일에서 관리됩니다:

```bash
# .env
REDIS_PASSWORD=fintech123
```

## 구조

```
docker/
├── docker-compose.yml     # 메인 Docker Compose 파일
├── README.md              # 이 파일
├── redis/                 # Redis 설정 (필요시)
├── kafka/                 # Kafka 설정 (필요시)
└── scripts/               # 스크립트 (필요시)
```

## 트러블슈팅

### 포트 충돌
기본 포트가 이미 사용 중이라면:
```bash
# 사용 중인 포트 확인
lsof -i :6379
lsof -i :9092

# 기존 프로세스 종료 후 재시작
./gradlew dockerDown
./gradlew dockerUp
```

### 환경변수 문제
```bash
# .env 파일 확인
cat .env | grep REDIS_PASSWORD

# gradle.properties 확인  
cat gradle.properties | grep VERSION
```

### 데이터 초기화
```bash
# 볼륨까지 완전 삭제
cd docker
docker-compose down -v

# 재시작
./gradlew dockerUp
```

### 연결 테스트
```bash
# Redis 연결 테스트
redis-cli -h localhost -p 6379 -a $REDIS_PASSWORD ping

# Kafka 토픽 목록 확인
kafka-topics --bootstrap-server localhost:9092 --list
```

## 환경변수 설정

### 필수 환경변수
- `REDIS_PASSWORD`: Redis 접속 비밀번호 (`.env`에서 설정)
- `REDIS_VERSION`: Redis 이미지 버전 (`gradle.properties`에서 설정)  
- `KAFKA_VERSION`: Kafka 이미지 버전 (`gradle.properties`에서 설정)

### 환경변수 우선순위
1. Docker Compose 실행 시 환경변수
2. `.env` 파일
3. `gradle.properties` 
4. Docker Compose 기본값 (예: `${REDIS_VERSION:-7}`)

## etc

개발 환경이 준비되었으면:

1. **Redis 연결 테스트** - 애플리케이션에서 Redis 접속 확인
2. **Kafka 연결 테스트** - Producer/Consumer 테스트

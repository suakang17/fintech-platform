-- fintech-platform 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS fintech_platform 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE fintech_platform;

-- 계좌 테이블
CREATE TABLE accounts (
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE COMMENT '계좌번호',
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00 COMMENT '잔액',
    account_status ENUM('ACTIVE', 'INACTIVE', 'FROZEN') NOT NULL DEFAULT 'ACTIVE' COMMENT '계좌상태',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    version BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    INDEX idx_account_number (account_number),
    INDEX idx_account_status (account_status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB COMMENT='계좌';

-- 거래 테이블
CREATE TABLE transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL COMMENT '계좌ID',
    transaction_type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER_OUT', 'TRANSFER_IN') NOT NULL COMMENT '거래유형',
    amount DECIMAL(19,2) NOT NULL COMMENT '거래금액',
    balance_after DECIMAL(19,2) NOT NULL COMMENT '거래후잔액',
    transaction_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '거래일시',
    description VARCHAR(255) COMMENT '거래설명',
    FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    INDEX idx_account_id (account_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_transaction_at (transaction_at),
    INDEX idx_account_transaction_at (account_id, transaction_at DESC)
) ENGINE=InnoDB COMMENT='거래내역';

-- 분산락 테이블 (Redis 백업용)
CREATE TABLE distributed_locks (
    lock_key VARCHAR(255) PRIMARY KEY,
    locked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    locked_by VARCHAR(100) NOT NULL COMMENT '락 소유자',
    expires_at TIMESTAMP NOT NULL COMMENT '만료시간',
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB COMMENT='분산락 백업';

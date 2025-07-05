-- 샘플 데이터 삽입
USE fintech_platform;

-- 테스트용 계좌 생성
INSERT INTO accounts (account_number, balance, account_status) VALUES
('1001234567890', 1000000.00, 'ACTIVE'),    -- account domain 시나리오용 계좌 (100만원)
('2001234567890', 500000.00, 'ACTIVE'),     -- 일반 테스트 계좌 (50만원)
('3001234567890', 0.00, 'ACTIVE'),          -- 잔액 0원 계좌
('4001234567890', 250000.00, 'INACTIVE');   -- 비활성 계좌

-- 기존 거래 내역 (선택사항)
INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description) VALUES
(1, 'DEPOSIT', 1000000.00, 1000000.00, '초기입금'),
(2, 'DEPOSIT', 500000.00, 500000.00, '초기입금'),
(4, 'DEPOSIT', 250000.00, 250000.00, '초기입금');

-- 확인용 조회
SELECT 
    a.account_number,
    a.balance,
    a.account_status,
    COUNT(t.transaction_id) as transaction_count
FROM accounts a
LEFT JOIN transactions t ON a.account_id = t.account_id
GROUP BY a.account_id, a.account_number, a.balance, a.account_status
ORDER BY a.account_id;

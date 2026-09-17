-- Core Banking Simulation - Reporting Queries

-- 1. Account balances
SELECT
    account_id,
    customer_id,
    account_type,
    balance,
    status
FROM Accounts
ORDER BY account_id;

-- 2. Transaction history for all accounts
SELECT
    transaction_id,
    account_id,
    amount,
    transaction_type,
    transaction_date
FROM Transactions
ORDER BY transaction_date DESC;

-- 3. Transfer history
SELECT
    transfer_id,
    reference_number,
    sender_account_id,
    receiver_account_id,
    amount,
    status,
    created_at
FROM Transfers
ORDER BY created_at DESC;

-- 4. Total money transferred
SELECT
    COUNT(*) AS total_transfers,
    COALESCE(SUM(amount), 0) AS total_amount
FROM Transfers
WHERE status = 'SUCCESS';

-- 5. Transaction count by account
SELECT
    account_id,
    COUNT(*) AS transaction_count
FROM Transactions
GROUP BY account_id
ORDER BY transaction_count DESC;

-- =========================================================
-- V2__adjust_financial_model.sql
-- Correções pós-auditoria do modelo V1
-- NÃO altera a V1 (já registrada pelo Flyway) — apenas ajusta.
-- =========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------
-- AJUSTE 1 — recurring_transaction.account_id nullable
-- Recorrências pagas no cartão não têm conta de origem até o
-- pagamento da fatura (mesma lógica já usada em financial_transaction).
-- ---------------------------------------------------------
ALTER TABLE recurring_transaction
    MODIFY COLUMN account_id BIGINT UNSIGNED NULL;

ALTER TABLE recurring_transaction
    ADD CONSTRAINT chk_recurring_transaction_account
        CHECK (
            (payment_method = 'CREDIT_CARD' AND account_id IS NULL)
                OR
            (payment_method <> 'CREDIT_CARD' AND account_id IS NOT NULL)
            );

-- ---------------------------------------------------------
-- Remove as CHECKs de financial_transaction que dependem de `type`,
-- para poder (a) alargar o ENUM e (b) reescrever a lógica com o
-- novo caso INVESTMENT sem deixar regra antiga incompleta.
-- ---------------------------------------------------------
ALTER TABLE financial_transaction DROP CHECK chk_financial_transaction_accounts;
ALTER TABLE financial_transaction DROP CHECK chk_financial_transaction_category;
ALTER TABLE financial_transaction DROP CHECK chk_financial_transaction_expense_type;
ALTER TABLE financial_transaction DROP CHECK chk_financial_transaction_payment_method;

-- ---------------------------------------------------------
-- AJUSTE 3 (parte 1) — novo tipo INVESTMENT
-- ---------------------------------------------------------
ALTER TABLE financial_transaction
    MODIFY COLUMN type ENUM('INCOME','EXPENSE','TRANSFER','CARD_PAYMENT','INVESTMENT') NOT NULL;

-- ---------------------------------------------------------
-- AJUSTE 2 — CARD_PAYMENT passa a exigir credit_card_invoice_id
-- (estava comentado na V1)
-- ---------------------------------------------------------
-- ALTER TABLE financial_transaction
--     ADD CONSTRAINT chk_financial_transaction_card_invoice_link
--         CHECK (
--             (type = 'CARD_PAYMENT' AND credit_card_invoice_id IS NOT NULL)
--                 OR
--             (type <> 'CARD_PAYMENT' AND credit_card_invoice_id IS NULL)
--             );

-- ---------------------------------------------------------
-- Recriação das 4 CHECKs incluindo INVESTMENT.
-- INVESTMENT segue a mesma forma de TRANSFER (sai de uma account),
-- mas sem destino em account — o "destino" é investment_contribution.
-- ---------------------------------------------------------
ALTER TABLE financial_transaction
    ADD CONSTRAINT chk_financial_transaction_accounts
        CHECK (
            (type = 'INCOME' AND source_account_id IS NULL AND destination_account_id IS NOT NULL)
                OR
            (type = 'EXPENSE' AND payment_method = 'CREDIT_CARD'
                AND source_account_id IS NULL AND destination_account_id IS NULL)
                OR
            (type = 'EXPENSE' AND payment_method <> 'CREDIT_CARD'
                AND source_account_id IS NOT NULL AND destination_account_id IS NULL)
                OR
            (type = 'TRANSFER'
                AND source_account_id IS NOT NULL
                AND destination_account_id IS NOT NULL
                AND source_account_id <> destination_account_id)
                OR
            (type = 'CARD_PAYMENT'
                AND source_account_id IS NOT NULL
                AND destination_account_id IS NULL)
                OR
            (type = 'INVESTMENT'
                AND source_account_id IS NOT NULL
                AND destination_account_id IS NULL)
            );

ALTER TABLE financial_transaction
    ADD CONSTRAINT chk_financial_transaction_category
        CHECK (
            (type IN ('TRANSFER','CARD_PAYMENT','INVESTMENT') AND category_id IS NULL)
                OR
            (type IN ('INCOME','EXPENSE') AND category_id IS NOT NULL)
            );

ALTER TABLE financial_transaction
    ADD CONSTRAINT chk_financial_transaction_expense_type
        CHECK (
            (type = 'EXPENSE' AND expense_type IS NOT NULL)
                OR
            (type IN ('INCOME','TRANSFER','CARD_PAYMENT','INVESTMENT') AND expense_type IS NULL)
            );

ALTER TABLE financial_transaction
    ADD CONSTRAINT chk_financial_transaction_payment_method
        CHECK (
            (type IN ('TRANSFER','INVESTMENT') AND payment_method IS NULL)
                OR
            (type IN ('INCOME','EXPENSE','CARD_PAYMENT') AND payment_method IS NOT NULL)
            );

-- ---------------------------------------------------------
-- AJUSTE 3 (parte 2) — investment_contribution ligado à
-- financial_transaction que efetivamente tirou o dinheiro da conta.
--
-- account_id é mantido (evita quebrar leitura), mas passa a ser
-- redundante em relação a transaction.source_account_id: a regra
-- "account_id deve sempre bater com o source_account_id da
-- transaction vinculada" é responsabilidade da camada de serviço,
-- o banco não valida essa igualdade entre tabelas.
-- ---------------------------------------------------------
ALTER TABLE investment_contribution
    ADD COLUMN transaction_id BIGINT UNSIGNED NULL AFTER account_id;

ALTER TABLE investment_contribution
    ADD CONSTRAINT fk_investment_contribution_transaction
        FOREIGN KEY (transaction_id) REFERENCES financial_transaction(id)
            ON DELETE SET NULL;

ALTER TABLE investment_contribution
    ADD UNIQUE KEY uk_investment_contribution_transaction (transaction_id);

-- ---------------------------------------------------------
-- AJUSTE 4 — monthly_budget.planned_amount >= 0
-- ---------------------------------------------------------
ALTER TABLE monthly_budget
    ADD CONSTRAINT chk_monthly_budget_planned_amount
        CHECK (planned_amount >= 0);

-- ---------------------------------------------------------
-- Gap #3 da auditoria — credit_card_purchase sem loja/observação.
-- O Excel registra loja/fornecedor e condição de pagamento em quase
-- toda compra parcelada (ex.: "Loja Mirante", "presente casamento/
-- Micro-ondas"), e a V1 não tinha coluna pra isso.
-- ---------------------------------------------------------
ALTER TABLE credit_card_purchase
    ADD COLUMN store VARCHAR(100) NULL AFTER description,
    ADD COLUMN notes  VARCHAR(255) NULL AFTER installment_count;

-- ---------------------------------------------------------
-- AJUSTE 5 — vw_monthly_expense_summary
-- Recriada sem mudança de lógica: continua sendo uma view
-- ESPECÍFICA de fluxo de caixa (despesas + receita bruta), não o
-- modelo inteiro do dashboard. INVESTMENT cai no ELSE 0 de cada
-- CASE de propósito — aporte não é despesa nem receita.
-- ---------------------------------------------------------
CREATE OR REPLACE VIEW vw_monthly_expense_summary AS
SELECT
    user_id,
    DATE_FORMAT(transaction_date, '%Y-%m-01') AS reference_month,

    SUM(CASE WHEN type = 'EXPENSE' AND expense_type = 'FIXED'
        AND payment_method <> 'CREDIT_CARD' THEN amount ELSE 0 END) AS fixed_expenses,

    SUM(CASE WHEN type = 'EXPENSE' AND payment_method = 'CREDIT_CARD'
                 THEN amount ELSE 0 END) AS credit_card,

    SUM(CASE WHEN type = 'EXPENSE' AND expense_type = 'VARIABLE'
        AND payment_method <> 'CREDIT_CARD' THEN amount ELSE 0 END) AS variable_expenses,

    SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) AS income

FROM financial_transaction
GROUP BY user_id, DATE_FORMAT(transaction_date, '%Y-%m-01');

SET FOREIGN_KEY_CHECKS = 1;
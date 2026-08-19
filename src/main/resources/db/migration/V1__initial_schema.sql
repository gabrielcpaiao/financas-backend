-- =========================================================
-- Schema MySQL — Personal Finance Control System
-- Revised consolidated data model
-- =========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------
-- APP_USER
-- ---------------------------------------------------------
CREATE TABLE app_user (
                          id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          name           VARCHAR(120) NOT NULL,
                          email          VARCHAR(180) NOT NULL,
                          password_hash  VARCHAR(255) NOT NULL,
                          created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          UNIQUE KEY uk_app_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- ACCOUNT
-- Represents bank accounts, digital accounts, cash, etc.
-- ---------------------------------------------------------
CREATE TABLE account (
                         id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                         user_id         BIGINT UNSIGNED NOT NULL,
                         name            VARCHAR(100) NOT NULL,
                         type            ENUM('CHECKING','SAVINGS','DIGITAL','CASH','OTHER') NOT NULL,
                         initial_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                         active          BOOLEAN NOT NULL DEFAULT TRUE,
                         created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT fk_account_user
                             FOREIGN KEY (user_id) REFERENCES app_user(id)
                                 ON DELETE CASCADE,

                         UNIQUE KEY uk_account_user_name (user_id, name),
                         INDEX idx_account_user_active (user_id, active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- CATEGORY
-- User-defined hierarchical categories for income and expenses.
-- ---------------------------------------------------------
CREATE TABLE category (
                          id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          user_id    BIGINT UNSIGNED NOT NULL,
                          name       VARCHAR(80) NOT NULL,
                          color VARCHAR(7) NOT NULL DEFAULT '#CCCCCC',
                          context    ENUM('INCOME','EXPENSE') NOT NULL,
                          parent_id  BIGINT UNSIGNED NULL,
                          active     BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_category_user
                              FOREIGN KEY (user_id) REFERENCES app_user(id)
                                  ON DELETE CASCADE,

                          CONSTRAINT fk_category_parent
                              FOREIGN KEY (parent_id) REFERENCES category(id)
                                  ON DELETE SET NULL,

                          UNIQUE KEY uk_category_user_name_context (user_id, name, context),
                          INDEX idx_category_user_context (user_id, context),
                          INDEX idx_category_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- FINANCIAL_GOAL
-- Examples: Emergency Fund, Retirement, Safety Net.
-- ---------------------------------------------------------
CREATE TABLE financial_goal (
                                id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                user_id    BIGINT UNSIGNED NOT NULL,
                                name       VARCHAR(80) NOT NULL,
                                target_amount DECIMAL(15,2) NULL,
                                target_date DATE NULL,
                                active     BOOLEAN NOT NULL DEFAULT TRUE,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                CONSTRAINT fk_financial_goal_user
                                    FOREIGN KEY (user_id) REFERENCES app_user(id)
                                        ON DELETE CASCADE,

                                UNIQUE KEY uk_financial_goal_user_name (user_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- RECURRING_TRANSACTION
-- Template for recurring income/expenses.
-- ---------------------------------------------------------
CREATE TABLE recurring_transaction (
                                       id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                       user_id         BIGINT UNSIGNED NOT NULL,
                                       account_id      BIGINT UNSIGNED NOT NULL,
                                       category_id     BIGINT UNSIGNED NOT NULL,
                                       type            ENUM('INCOME','EXPENSE') NOT NULL,
                                       description     VARCHAR(150) NOT NULL,
                                       amount          DECIMAL(15,2) NOT NULL,
                                       expense_type    ENUM('FIXED','VARIABLE') NULL,
                                       payment_method  ENUM('CASH','DEBIT_CARD','PIX','CREDIT_CARD') NOT NULL,
                                       due_day         TINYINT UNSIGNED NULL,
                                       start_date      DATE NOT NULL,
                                       end_date        DATE NULL,
                                       active          BOOLEAN NOT NULL DEFAULT TRUE,
                                       created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_recurring_transaction_user
                                           FOREIGN KEY (user_id) REFERENCES app_user(id)
                                               ON DELETE CASCADE,

                                       CONSTRAINT fk_recurring_transaction_account
                                           FOREIGN KEY (account_id) REFERENCES account(id)
                                               ON DELETE RESTRICT,

                                       CONSTRAINT fk_recurring_transaction_category
                                           FOREIGN KEY (category_id) REFERENCES category(id)
                                               ON DELETE RESTRICT,

                                       INDEX idx_recurring_transaction_user_active (user_id, active),
                                       INDEX idx_recurring_transaction_start_date (start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- CREDIT_CARD
-- ---------------------------------------------------------
CREATE TABLE credit_card (
                             id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                             user_id          BIGINT UNSIGNED NOT NULL,
                             name             VARCHAR(100) NOT NULL,
                             brand            VARCHAR(50) NULL,
                             credit_limit     DECIMAL(15,2) NOT NULL,
                             closing_day      TINYINT UNSIGNED NOT NULL,
                             due_day          TINYINT UNSIGNED NOT NULL,
                             active           BOOLEAN NOT NULL DEFAULT TRUE,
                             created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             CONSTRAINT fk_credit_card_user
                                 FOREIGN KEY (user_id) REFERENCES app_user(id)
                                     ON DELETE CASCADE,

                             UNIQUE KEY uk_credit_card_user_name (user_id, name),
                             INDEX idx_credit_card_user_active (user_id, active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- CREDIT_CARD_INVOICE
-- One invoice represents one billing cycle of a credit card.
-- ---------------------------------------------------------
CREATE TABLE credit_card_invoice (
                                     id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                     credit_card_id   BIGINT UNSIGNED NOT NULL,
                                     reference_month  DATE NOT NULL COMMENT 'Always the first day of the reference month',
                                     closing_date     DATE NOT NULL,
                                     due_date         DATE NOT NULL,
                                     status           ENUM('OPEN','CLOSED','PAID','OVERDUE') NOT NULL DEFAULT 'OPEN',
                                     paid_at          DATETIME NULL,
                                     created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                     CONSTRAINT fk_credit_card_invoice_card
                                         FOREIGN KEY (credit_card_id) REFERENCES credit_card(id)
                                             ON DELETE CASCADE,

                                     UNIQUE KEY uk_credit_card_invoice_month (credit_card_id, reference_month),
                                     INDEX idx_credit_card_invoice_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- CREDIT_CARD_PURCHASE
-- Represents the original purchase made with a credit card.
-- ---------------------------------------------------------
CREATE TABLE credit_card_purchase (
                                      id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                      user_id            BIGINT UNSIGNED NOT NULL,
                                      credit_card_id     BIGINT UNSIGNED NOT NULL,
                                      category_id        BIGINT UNSIGNED NOT NULL,
                                      description        VARCHAR(150) NOT NULL,
                                      total_amount       DECIMAL(15,2) NOT NULL,
                                      installment_count  SMALLINT UNSIGNED NOT NULL DEFAULT 1,
                                      purchase_date      DATE NOT NULL,
                                      created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                      CONSTRAINT fk_credit_card_purchase_user
                                          FOREIGN KEY (user_id) REFERENCES app_user(id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT fk_credit_card_purchase_card
                                          FOREIGN KEY (credit_card_id) REFERENCES credit_card(id)
                                              ON DELETE RESTRICT,

                                      CONSTRAINT fk_credit_card_purchase_category
                                          FOREIGN KEY (category_id) REFERENCES category(id)
                                              ON DELETE RESTRICT,

                                      INDEX idx_credit_card_purchase_user_date (user_id, purchase_date),
                                      INDEX idx_credit_card_purchase_card (credit_card_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- CREDIT_CARD_INSTALLMENT
-- Individual installment generated by a credit card purchase.
-- ---------------------------------------------------------
CREATE TABLE credit_card_installment (
                                         id                    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                         credit_card_purchase_id BIGINT UNSIGNED NOT NULL,
                                         invoice_id             BIGINT UNSIGNED NULL,
                                         installment_number     SMALLINT UNSIGNED NOT NULL,
                                         amount                 DECIMAL(15,2) NOT NULL,
                                         due_date               DATE NOT NULL,
                                         status                 ENUM('PENDING','PAID','OVERDUE','CANCELLED') NOT NULL DEFAULT 'PENDING',
                                         paid_at                DATETIME NULL,
                                         created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT fk_credit_card_installment_purchase
                                             FOREIGN KEY (credit_card_purchase_id) REFERENCES credit_card_purchase(id)
                                                 ON DELETE CASCADE,

                                         CONSTRAINT fk_credit_card_installment_invoice
                                             FOREIGN KEY (invoice_id) REFERENCES credit_card_invoice(id)
                                                 ON DELETE SET NULL,

                                         UNIQUE KEY uk_credit_card_installment_number
                                             (credit_card_purchase_id, installment_number),

                                         INDEX idx_credit_card_installment_invoice (invoice_id),
                                         INDEX idx_credit_card_installment_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- FINANCIAL_TRANSACTION
-- Core entity for actual income, expenses and transfers.
--
-- INCOME:   destination_account_id required.
-- EXPENSE:  source_account_id required.
-- TRANSFER: both accounts required and must be different.
-- Transfers do not use a category.
-- ---------------------------------------------------------
CREATE TABLE financial_transaction (
                                       id                         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                       user_id                    BIGINT UNSIGNED NOT NULL,
                                       source_account_id         BIGINT UNSIGNED NULL,
                                       destination_account_id    BIGINT UNSIGNED NULL,
                                       category_id               BIGINT UNSIGNED NULL,
                                       type                       ENUM('INCOME','EXPENSE','TRANSFER','CARD_PAYMENT') NOT NULL,
                                       transaction_date           DATE NOT NULL,
                                       description                VARCHAR(150) NOT NULL,
                                       amount                     DECIMAL(15,2) NOT NULL,
                                       expense_type               ENUM('FIXED','VARIABLE') NULL,
                                       payment_method             ENUM('CASH','DEBIT_CARD','PIX','CREDIT_CARD') NULL,
                                       notes                      VARCHAR(255) NULL,
                                       recurring_transaction_id   BIGINT UNSIGNED NULL,
                                       credit_card_installment_id BIGINT UNSIGNED NULL,
                                       credit_card_invoice_id     BIGINT UNSIGNED NULL COMMENT 'used only when type=CARD_PAYMENT: the invoice being paid',
                                       created_at                 DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at                 DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_financial_transaction_user
                                           FOREIGN KEY (user_id) REFERENCES app_user(id)
                                               ON DELETE CASCADE,

                                       CONSTRAINT fk_financial_transaction_source_account
                                           FOREIGN KEY (source_account_id) REFERENCES account(id)
                                               ON DELETE RESTRICT,

                                       CONSTRAINT fk_financial_transaction_destination_account
                                           FOREIGN KEY (destination_account_id) REFERENCES account(id)
                                               ON DELETE RESTRICT,

                                       CONSTRAINT fk_financial_transaction_category
                                           FOREIGN KEY (category_id) REFERENCES category(id)
                                               ON DELETE RESTRICT,

                                       CONSTRAINT fk_financial_transaction_recurring
                                           FOREIGN KEY (recurring_transaction_id) REFERENCES recurring_transaction(id)
                                               ON DELETE SET NULL,

                                       CONSTRAINT fk_financial_transaction_installment
                                           FOREIGN KEY (credit_card_installment_id) REFERENCES credit_card_installment(id)
                                               ON DELETE SET NULL,

                                       CONSTRAINT fk_financial_transaction_card_invoice
                                           FOREIGN KEY (credit_card_invoice_id) REFERENCES credit_card_invoice(id)
                                               ON DELETE RESTRICT,

                                       CONSTRAINT chk_financial_transaction_amount
                                           CHECK (amount > 0),

    -- Expenses paid on a credit card do NOT require a source_account_id: the
    -- money only actually leaves an account later, when the invoice is paid
    -- (see the CARD_PAYMENT branch below). This applies to any card expense,
    -- not only installment purchases (e.g. a recurring subscription charged
    -- to the card every month, with no credit_card_installment_id at all).
                                       CONSTRAINT chk_financial_transaction_accounts
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
                                               ),

                                       CONSTRAINT chk_financial_transaction_category
                                           CHECK (
                                               (type IN ('TRANSFER','CARD_PAYMENT') AND category_id IS NULL)
                                                   OR
                                               (type IN ('INCOME','EXPENSE') AND category_id IS NOT NULL)
                                               ),

                                       CONSTRAINT chk_financial_transaction_expense_type
                                           CHECK (
                                               (type = 'EXPENSE' AND expense_type IS NOT NULL)
                                                   OR
                                               (type IN ('INCOME','TRANSFER','CARD_PAYMENT') AND expense_type IS NULL)
                                               ),

                                       CONSTRAINT chk_financial_transaction_payment_method
                                           CHECK (
                                               (type = 'TRANSFER' AND payment_method IS NULL)
                                                   OR
                                               (type IN ('INCOME','EXPENSE','CARD_PAYMENT') AND payment_method IS NOT NULL)
                                               ),

    -- credit_card_invoice_id is only meaningful (and required) for CARD_PAYMENT
--     CONSTRAINT chk_financial_transaction_card_invoice_link
--       CHECK (
--         (type = 'CARD_PAYMENT' AND credit_card_invoice_id IS NOT NULL)
--         OR
--         (type <> 'CARD_PAYMENT' AND credit_card_invoice_id IS NULL)
--       ),

                                       INDEX idx_financial_transaction_user_date (user_id, transaction_date),
                                       INDEX idx_financial_transaction_source_date (source_account_id, transaction_date),
                                       INDEX idx_financial_transaction_destination_date (destination_account_id, transaction_date),
                                       INDEX idx_financial_transaction_category (category_id),
                                       INDEX idx_financial_transaction_type (user_id, type),
                                       INDEX idx_financial_transaction_card_invoice (credit_card_invoice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- MONTHLY_BUDGET
-- Budget planned for a category in a given month.
-- ---------------------------------------------------------
CREATE TABLE monthly_budget (
                                id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                user_id          BIGINT UNSIGNED NOT NULL,
                                category_id      BIGINT UNSIGNED NOT NULL,
                                reference_month  DATE NOT NULL COMMENT 'Always the first day of the reference month/year',
                                planned_amount   DECIMAL(15,2) NOT NULL,
                                created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                CONSTRAINT fk_monthly_budget_user
                                    FOREIGN KEY (user_id) REFERENCES app_user(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_monthly_budget_category
                                    FOREIGN KEY (category_id) REFERENCES category(id)
                                        ON DELETE RESTRICT,

                                UNIQUE KEY uk_monthly_budget_user_category_month
                                    (user_id, category_id, reference_month),

                                INDEX idx_monthly_budget_user_month (user_id, reference_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- INVESTMENT
-- Represents an investment/asset.
-- ---------------------------------------------------------
CREATE TABLE investment (
                            id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                            user_id           BIGINT UNSIGNED NOT NULL,
                            financial_goal_id BIGINT UNSIGNED NULL,
                            name              VARCHAR(120) NOT NULL,
                            type              ENUM('CDB','TREASURY','STOCK','ETF','FUND','CRYPTO','OTHER') NOT NULL,
                            description       VARCHAR(255) NULL,
                            active            BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                            CONSTRAINT fk_investment_user
                                FOREIGN KEY (user_id) REFERENCES app_user(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_investment_financial_goal
                                FOREIGN KEY (financial_goal_id) REFERENCES financial_goal(id)
                                    ON DELETE SET NULL,

                            UNIQUE KEY uk_investment_user_name (user_id, name),
                            INDEX idx_investment_user_active (user_id, active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- INVESTMENT_CONTRIBUTION
-- Represents an actual contribution/aporte to an investment.
-- ---------------------------------------------------------
CREATE TABLE investment_contribution (
                                         id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                         investment_id   BIGINT UNSIGNED NOT NULL,
                                         account_id      BIGINT UNSIGNED NOT NULL,
                                         contribution_date DATE NOT NULL,
                                         amount          DECIMAL(15,2) NOT NULL,
                                         notes           VARCHAR(255) NULL,
                                         created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT fk_investment_contribution_investment
                                             FOREIGN KEY (investment_id) REFERENCES investment(id)
                                                 ON DELETE CASCADE,

                                         CONSTRAINT fk_investment_contribution_account
                                             FOREIGN KEY (account_id) REFERENCES account(id)
                                                 ON DELETE RESTRICT,

                                         CONSTRAINT chk_investment_contribution_amount
                                             CHECK (amount > 0),

                                         INDEX idx_investment_contribution_investment_date
                                             (investment_id, contribution_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- WISHLIST_ITEM / PLANNED_PURCHASE
-- ---------------------------------------------------------
CREATE TABLE planned_purchase (
                                  id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                  user_id                 BIGINT UNSIGNED NOT NULL,
                                  category_id             BIGINT UNSIGNED NULL,
                                  item_name               VARCHAR(150) NOT NULL,
                                  unit_price              DECIMAL(15,2) NOT NULL,
                                  quantity                SMALLINT UNSIGNED NOT NULL DEFAULT 1,
                                  expected_payment_method ENUM('CASH','DEBIT_CARD','PIX','CREDIT_CARD') NULL,
                                  priority                ENUM('HIGH','MEDIUM','LOW') NOT NULL DEFAULT 'MEDIUM',
                                  status                  ENUM('PENDING','DONE','CANCELLED') NOT NULL DEFAULT 'PENDING',
                                  purchase_date           DATE NULL,
                                  transaction_id          BIGINT UNSIGNED NULL,
                                  credit_card_purchase_id BIGINT UNSIGNED NULL,
                                  notes                   VARCHAR(255) NULL,
                                  created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_planned_purchase_user
                                      FOREIGN KEY (user_id) REFERENCES app_user(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_planned_purchase_category
                                      FOREIGN KEY (category_id) REFERENCES category(id)
                                          ON DELETE SET NULL,

                                  CONSTRAINT fk_planned_purchase_transaction
                                      FOREIGN KEY (transaction_id) REFERENCES financial_transaction(id)
                                          ON DELETE SET NULL,

                                  CONSTRAINT fk_planned_purchase_credit_card_purchase
                                      FOREIGN KEY (credit_card_purchase_id) REFERENCES credit_card_purchase(id)
                                          ON DELETE SET NULL,

                                  CONSTRAINT chk_planned_purchase_price
                                      CHECK (unit_price > 0),

                                  CONSTRAINT chk_planned_purchase_quantity
                                      CHECK (quantity > 0),

                                  INDEX idx_planned_purchase_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- VIEW — Monthly expense summary
-- =========================================================
CREATE OR REPLACE VIEW vw_monthly_expense_summary AS
SELECT
    user_id,
    DATE_FORMAT(transaction_date, '%Y-%m-01') AS reference_month,

    SUM(
            CASE
                WHEN type = 'EXPENSE'
                    AND expense_type = 'FIXED'
                    AND payment_method <> 'CREDIT_CARD'
                    THEN amount
                ELSE 0
                END
    ) AS fixed_expenses,

    SUM(
            CASE
                WHEN type = 'EXPENSE'
                    AND payment_method = 'CREDIT_CARD'
                    THEN amount
                ELSE 0
                END
    ) AS credit_card,

    SUM(
            CASE
                WHEN type = 'EXPENSE'
                    AND expense_type = 'VARIABLE'
                    AND payment_method <> 'CREDIT_CARD'
                    THEN amount
                ELSE 0
                END
    ) AS variable_expenses,

    SUM(
            CASE
                WHEN type = 'INCOME'
                    THEN amount
                ELSE 0
                END
    ) AS income

FROM financial_transaction
GROUP BY user_id, DATE_FORMAT(transaction_date, '%Y-%m-01');

SET FOREIGN_KEY_CHECKS = 1;

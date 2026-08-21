ALTER TABLE credit_card
    ADD CONSTRAINT chk_credit_card_closing_day
        CHECK (closing_day BETWEEN 1 AND 31),
    ADD CONSTRAINT chk_credit_card_due_day
        CHECK (due_day BETWEEN 1 AND 31);
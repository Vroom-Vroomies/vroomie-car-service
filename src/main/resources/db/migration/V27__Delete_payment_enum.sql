UPDATE tbl_car_contract SET payment_cycle = "MONTHLY" WHERE id IN (3, 6);

UPDATE tbl_insurance SET payment_type = "MONTHLY" WHERE id = 3;

ALTER TABLE tbl_car_contract
    MODIFY COLUMN payment_cycle enum('MONTHLY') COLLATE utf8mb4_unicode_ci NOT NULL;

ALTER TABLE tbl_insurance
    MODIFY COLUMN payment_type enum('MONTHLY') COLLATE utf8mb4_unicode_ci NOT NULL;
INSERT INTO tbl_cost_type (`cost_type_name`, `description`) VALUES
    ('차량 대출금', '차량 구매 대출금에 대한 월 대출 상환금');

ALTER TABLE tbl_contract_cost ADD COLUMN insurance_id BIGINT NULL AFTER contract_id;

ALTER TABLE tbl_contract_cost MODIFY contract_id BIGINT NULL;

ALTER TABLE `tbl_contract_cost` ADD CONSTRAINT `FK_tbl_insurance_TO_tbl_contract_cost_1` FOREIGN KEY (
                                                                                                      `insurance_id`
    )
    REFERENCES `tbl_insurance` (
                                `id`
        ) ON DELETE CASCADE;

UPDATE tbl_car_contract
SET payment_day = 10
WHERE id IN (3, 6);

UPDATE tbl_insurance
SET payment_day = 10
WHERE id = 3;

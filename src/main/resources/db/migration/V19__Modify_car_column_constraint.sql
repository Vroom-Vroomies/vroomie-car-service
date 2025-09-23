UPDATE tbl_car
SET total_mileage = 0
WHERE total_mileage IS NULL;

ALTER TABLE tbl_car
MODIFY COLUMN total_mileage BIGINT NOT NULL DEFAULT 0;
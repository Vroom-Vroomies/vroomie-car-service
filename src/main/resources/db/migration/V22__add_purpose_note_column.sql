ALTER TABLE `tbl_driving_log`
    ADD COLUMN `purpose` ENUM('BUSINESS','PERSONAL','COMMUTE') NULL COMMENT '운행 목적' AFTER `odometer_distance`,
    ADD COLUMN `note` VARCHAR(255) NULL COMMENT '비고' AFTER `purpose`;
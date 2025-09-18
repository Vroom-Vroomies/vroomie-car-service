-- flyway에선 쓰이지 않지만 로컬에서 사용 시 필요한 명령어

-- CREATE DATABASE companycar_db
-- DEFAULT CHARSET=utf8mb4
-- COLLATE=utf8mb4_unicode_ci;

-- GRANT ALL PRIVILEGES ON companycar_db.* TO 'vroomie'@'%';

-- USE companycar_db;

-- =================================================================
-- 테이블 삭제
-- =================================================================
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `tbl_accident_image`;
DROP TABLE IF EXISTS `tbl_car`;
DROP TABLE IF EXISTS `tbl_accident`;
DROP TABLE IF EXISTS `tbl_insurance`;
DROP TABLE IF EXISTS `tbl_driving_log`;
DROP TABLE IF EXISTS `tbl_reserved_log`;
DROP TABLE IF EXISTS `tbl_repair_image`;
DROP TABLE IF EXISTS `tbl_car_contract`;
DROP TABLE IF EXISTS `tbl_employee`;
DROP TABLE IF EXISTS `tbl_cost_type`;
DROP TABLE IF EXISTS `tbl_repair`;
DROP TABLE IF EXISTS `tbl_variable_cost`;
DROP TABLE IF EXISTS `tbl_lease_detail`;
DROP TABLE IF EXISTS `tbl_inspection`;
DROP TABLE IF EXISTS `tbl_contract_cost`;
DROP TABLE IF EXISTS `tbl_purchase_detail`;
DROP TABLE IF EXISTS `tbl_reservation`;
DROP TABLE IF EXISTS `tbl_company`;
DROP TABLE IF EXISTS `tbl_rent_detail`;
DROP TABLE IF EXISTS `tbl_provided_log`;
SET FOREIGN_KEY_CHECKS = 1;

-- =================================================================
-- 테이블 생성
-- =================================================================

CREATE TABLE `tbl_accident_image` (
                                      `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                      `accident_id`	BIGINT	NOT NULL,
                                      `image`	VARCHAR(255)	NULL
);

CREATE TABLE `tbl_car` (
                           `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           `company_id`	BIGINT	NOT NULL,
                           `identification`	VARCHAR(30)	NOT NULL,
                           `number`	VARCHAR(15)	NOT NULL,
                           `image`	VARCHAR(255)	NULL,
                           `model`	VARCHAR(100)	NOT NULL,
                           `type`	VARCHAR(15)	NOT NULL,
                           `total_mileage`	BIGINT	NULL,
                           `color`	VARCHAR(15)	NOT NULL,
                           `year`	BIGINT	NOT NULL,
                           `status`	ENUM('ACTIVE', 'SCRAPPED', 'SOLD', 'CONTRACT_ENDED')	NOT NULL,
                           `insu_expiration`	DATE	NOT NULL,
                           `last_inspection`	DATE	NULL,
                           `inspection_cycle`	INT	NULL,
                           `allowable_capacity`	INT	NULL,
                           `fuel_type`	VARCHAR(30)	NULL,
                           `usage_type`	ENUM('ASSIGNED', 'SHARED')	NULL,
                           `gear_type`	ENUM('AUTO', 'MANUAL')	NULL
);

CREATE TABLE `tbl_accident` (
                                `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                `car_id`	BIGINT	NOT NULL,
                                `emp_email`	VARCHAR(255)	NOT NULL	COMMENT '아이디로 사용',
                                `type`	ENUM('SINGLE_VEHICLE', 'VS_VEHICLE', 'VS_PERSON')	NOT NULL,
                                `note`	VARCHAR(30)	NULL,
                                `detail`	VARCHAR(255)	NOT NULL,
                                `occurred_at`	DATE	NOT NULL,
                                `cost`	DECIMAL(10, 2)	NULL
);

CREATE TABLE `tbl_insurance` (
                                 `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                 `car_id`	BIGINT	NOT NULL,
                                 `start_date`	DATETIME	NULL,
                                 `end_date`	DATETIME	NULL,
                                 `premium`	DECIMAL(10, 2)	NULL,
                                 `company`	VARCHAR(50)	NULL,
                                 `name`	VARCHAR(100)	NULL,
                                 `status`	ENUM('NEW', 'RENEWED', 'EXPIRED')	NULL
);

CREATE TABLE `tbl_driving_log` (
                                   `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   `emp_email`	VARCHAR(255)	NOT NULL	COMMENT '아이디로 사용',
                                   `car_id`	BIGINT	NOT NULL,
                                   `purpose`	ENUM('BUSINESS', 'PERSONAL', 'COMMUTING')	NULL,
                                   `detail`	VARCHAR(255)	NULL,
                                   `start_odometer`	BIGINT	NULL,
                                   `start_odometer_image`	VARCHAR(255)	NULL,
                                   `end_odometer`	BIGINT	NULL,
                                   `end_odometer_image`	VARCHAR(255)	NULL,
                                   `started_at`	DATETIME	NULL,
                                   `ended_at`	DATETIME	NULL,
                                   `log_status`	ENUM('PREPARING', 'WRITING', 'PENDING', 'COMPLETED')	NOT NULL	DEFAULT 'PREPARING',
                                   `created_at`	TIMESTAMP	NOT NULL,
                                   `updated_at`	TIMESTAMP	NULL,
                                   `is_saved`	BOOLEAN	NOT NULL	DEFAULT FALSE
);

CREATE TABLE `tbl_reserved_log` (
                                    `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    `car_id`	BIGINT	NOT NULL,
                                    `admin_emp_email`	VARCHAR(255)	NOT NULL	COMMENT '아이디로 사용',
                                    `reservation_id`	BIGINT	NOT NULL,
                                    `started_at`	DATETIME	NULL,
                                    `ended_at`	DATETIME	NULL,
                                    `reserved_status`	ENUM('RENTED', 'RETURNED')	NULL	DEFAULT 'RENTED',
                                    `created_at`	TIMESTAMP	NOT NULL,
                                    `return_date`	DATETIME	NULL,
                                    `updated_at`	TIMESTAMP	NULL
);

CREATE TABLE `tbl_repair_image` (
                                    `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    `repair_id`	BIGINT	NOT NULL,
                                    `type`	ENUM('BEFORE', 'AFTER')	NULL,
                                    `image`	VARCHAR(255)	NULL
);

CREATE TABLE `tbl_car_contract` (
                                    `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    `car_id`	BIGINT	NOT NULL,
                                    `contract_type`	ENUM('LEASE', 'RENT', 'PURCHASE')	NULL,
                                    `provider`	VARCHAR(50)	NULL,
                                    `monthly_fee`	DECIMAL(10, 2)	NULL,
                                    `insurance_included`	BOOLEAN	NULL,
                                    `start_at`	DATE	NULL,
                                    `end_at`	DATE	NULL,
                                    `created_at`	TIMESTAMP	NULL,
                                    `updated_at`	TIMESTAMP	NULL,
                                    `renewal_date`	DATE	NULL,
                                    `status`	ENUM('NEW', 'RENEWED', 'EXPIRED')	NULL
);

CREATE TABLE `tbl_employee` (
                                `email`	VARCHAR(255)	NOT NULL PRIMARY KEY,
                                `name`	VARCHAR(30)	NOT NULL,
                                `phone`	VARCHAR(15)	NOT NULL,
                                `department`	VARCHAR(50)	NOT NULL,
                                `is_deleted`	BOOLEAN	NOT NULL	DEFAULT FALSE,
                                `licence_status`	BOOLEAN	NOT NULL	DEFAULT FALSE,
                                `licence_image`	VARCHAR(255)	NULL,
                                `role`	ENUM('SUPER_ADMIN', 'ADMIN', 'USER')	NOT NULL,
                                `is_active`	BOOLEAN	NOT NULL	DEFAULT FALSE
);

CREATE TABLE `tbl_cost_type` (
                                 `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                 `cost_type_name`	VARCHAR(50)	NULL,
                                 `description`	VARCHAR(200)	NULL
);

CREATE TABLE `tbl_repair` (
                              `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                              `emp_email`	VARCHAR(255)	NOT NULL	COMMENT '아이디로 사용',
                              `car_id`	BIGINT	NOT NULL,
                              `type`	ENUM('PAINTING', 'REPLACEMENT', 'DETACH_ATTACH', 'SHEET_METAL', 'GENERAL', 'OTHER')	NOT NULL,
                              `detail`	VARCHAR(255)	NOT NULL,
                              `status`	ENUM('IN_REPAIR', 'COMPLETED')	NOT NULL	DEFAULT 'IN_REPAIR',
                              `started_at`	DATE	NOT NULL,
                              `ended_date`	DATE	NULL,
                              `cost`	DECIMAL(10, 2)	NULL,
                              `is_saved`	BOOLEAN	NULL	DEFAULT FALSE
);

CREATE TABLE `tbl_variable_cost` (
                                     `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                     `log_id`	BIGINT	NOT NULL,
                                     `category`	ENUM('FUEL_COST', 'PARKING_COST', 'CAR_WASH_COST', 'TOLL_FEE', 'FINE')	NULL,
                                     `reciept`	VARCHAR(255)	NULL,
                                     `payment`	ENUM('COMPANY_CARD', 'PERSONAL_CARD', 'CASH')	NULL,
                                     `cost`	DECIMAL(10,2)	NULL,
                                     `place`	VARCHAR(255)	NULL
);

CREATE TABLE `tbl_lease_detail` (
                                    `id`	BIGINT	NOT NULL PRIMARY KEY,
                                    `monthly_lease`	DECIMAL(10,2)	NULL,
                                    `lease_period_months`	INT	NULL,
                                    `residual_value`	DECIMAL(10,2)	NULL,
                                    `buyout_option_price`	DECIMAL(10,2)	NULL,
                                    `mileage_limit`	BIGINT	NULL,
                                    `excess_mileage_rate`	DECIMAL(10,2)	NULL,
                                    `type`	ENUM('FINANCE_LEASE', 'OPERATING_LEASE')	NULL
);

CREATE TABLE `tbl_inspection` (
                                  `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                  `car_id`	BIGINT	NOT NULL,
                                  `date`	DATETIME	NULL,
                                  `center_name`	VARCHAR(30)	NULL,
                                  `center_location`	VARCHAR(255)	NULL,
                                  `inspector_name`	VARCHAR(30)	NULL,
                                  `inspection_type`	ENUM('REGULAR', 'COMPREHENCE')	NULL,
                                  `final_result`	VARCHAR(255)	NULL,
                                  `valid_until`	DATE	NULL,
                                  `failure_reason`	VARCHAR(255)	NULL,
                                  `result`	VARCHAR(255)	NULL,
                                  `remarks`	VARCHAR(255)	NULL,
                                  `created_at`	TIMESTAMP	NOT NULL,
                                  `updated_at`	TIMESTAMP	NULL,
                                  `created_by`	VARCHAR(255)	NOT NULL
);

CREATE TABLE `tbl_contract_cost` (
                                     `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                     `car_id`	BIGINT	NOT NULL,
                                     `cost_type_id`	BIGINT	NOT NULL,
                                     `contract_id`	BIGINT	NOT NULL,
                                     `amount`	DECIMAL(10, 2)	NULL,
                                     `cost_date`	DATE	NULL,
                                     `description`	VARCHAR(255)	NULL,
                                     `created_at`	TIMESTAMP	NULL,
                                     `updated_at`	TIMESTAMP	NULL
);

CREATE TABLE `tbl_purchase_detail` (
                                       `id`	BIGINT	NOT NULL PRIMARY KEY,
                                       `purchase_price`	DECIMAL(10, 2)	NULL,
                                       `down_payment`	DECIMAL(10, 2)	NULL,
                                       `loan_amount`	DECIMAL(10, 2)	NULL,
                                       `interest_rate`	DECIMAL(10, 2)	NULL,
                                       `loan_term_months`	INT	NULL,
                                       `monthly_payment`	DECIMAL(10, 2)	NULL
);

CREATE TABLE `tbl_reservation` (
                                   `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   `car_id`	BIGINT	NOT NULL,
                                   `member_emp_email`	VARCHAR(255)	NOT NULL	COMMENT '아이디로 사용',
                                   `reservation_start`	DATETIME	NULL,
                                   `reservation_end`	DATETIME	NULL,
                                   `reservation_status`	ENUM('PENDING','APPROVED','REJECTED','CANCLED')	NULL,
                                   `purpose`	VARCHAR(255)	NULL,
                                   `created_at`	TIMESTAMP	NOT NULL,
                                   `updated_at`	TIMESTAMP	NULL
);

CREATE TABLE `tbl_company` (
                               `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               `name`	VARCHAR(50)	NOT NULL
);

CREATE TABLE `tbl_rent_detail` (
                                   `id`	BIGINT	NOT NULL PRIMARY KEY,
                                   `monthly_rent`	DECIMAL(10, 2)	NULL,
                                   `deposit`	DECIMAL(10, 2)	NULL,
                                   `payment_cycle`	INT	NULL,
                                   `auto_renewal`	BOOLEAN	NULL,
                                   `type`	ENUM('SHORT_RENT', 'LONG_RENT')	NULL
);

CREATE TABLE `tbl_provided_log` (
                                    `id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    `emp_email`	VARCHAR(255)	NOT NULL	COMMENT '사번 -> 일반 아이디로 변경',
                                    `car_id`	BIGINT	NOT NULL,
                                    `provide_date`	DATE	NOT NULL,
                                    `return_date`	DATE	NULL	DEFAULT NULL,
                                    `created_at`	TIMESTAMP	NULL,
                                    `updated_at`	TIMESTAMP	NULL
);


-- =================================================================
-- FK 제약 조건
-- =================================================================

ALTER TABLE `tbl_accident_image` ADD CONSTRAINT `FK_tbl_accident_TO_tbl_accident_image_1` FOREIGN KEY (
                                                                                                       `accident_id`
    )
    REFERENCES `tbl_accident` (
                               `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_car` ADD CONSTRAINT `FK_tbl_company_TO_tbl_car_1` FOREIGN KEY (
                                                                                `company_id`
    )
    REFERENCES `tbl_company` (
                              `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_accident` ADD CONSTRAINT `FK_tbl_car_TO_tbl_accident_1` FOREIGN KEY (
                                                                                      `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_accident` ADD CONSTRAINT `FK_tbl_employee_TO_tbl_accident_1` FOREIGN KEY (
                                                                                           `emp_email`
    )
    REFERENCES `tbl_employee` (
                               `email`
        );

ALTER TABLE `tbl_insurance` ADD CONSTRAINT `FK_tbl_car_TO_tbl_insurance_1` FOREIGN KEY (
                                                                                        `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_driving_log` ADD CONSTRAINT `FK_tbl_employee_TO_tbl_driving_log_1` FOREIGN KEY (
                                                                                                 `emp_email`
    )
    REFERENCES `tbl_employee` (
                               `email`
        );

ALTER TABLE `tbl_driving_log` ADD CONSTRAINT `FK_tbl_car_TO_tbl_driving_log_1` FOREIGN KEY (
                                                                                            `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_reserved_log` ADD CONSTRAINT `FK_tbl_car_TO_tbl_reserved_log_1` FOREIGN KEY (
                                                                                              `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_reserved_log` ADD CONSTRAINT `FK_tbl_employee_TO_tbl_reserved_log_1` FOREIGN KEY (
                                                                                                   `admin_emp_email`
    )
    REFERENCES `tbl_employee` (
                               `email`
        );

ALTER TABLE `tbl_reserved_log` ADD CONSTRAINT `FK_tbl_reservation_TO_tbl_reserved_log_1` FOREIGN KEY (
                                                                                                      `reservation_id`
    )
    REFERENCES `tbl_reservation` (
                                  `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_repair_image` ADD CONSTRAINT `FK_tbl_repair_TO_tbl_repair_image_1` FOREIGN KEY (
                                                                                                 `repair_id`
    )
    REFERENCES `tbl_repair` (
                             `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_car_contract` ADD CONSTRAINT `FK_tbl_car_TO_tbl_car_contract_1` FOREIGN KEY (
                                                                                              `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_repair` ADD CONSTRAINT `FK_tbl_employee_TO_tbl_repair_1` FOREIGN KEY (
                                                                                       `emp_email`
    )
    REFERENCES `tbl_employee` (
                               `email`
        );

ALTER TABLE `tbl_repair` ADD CONSTRAINT `FK_tbl_car_TO_tbl_repair_1` FOREIGN KEY (
                                                                                  `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_variable_cost` ADD CONSTRAINT `FK_tbl_driving_log_TO_tbl_variable_cost_1` FOREIGN KEY (
                                                                                                        `log_id`
    )
    REFERENCES `tbl_driving_log` (
                                  `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_lease_detail` ADD CONSTRAINT `FK_tbl_car_contract_TO_tbl_lease_detail_1` FOREIGN KEY (
                                                                                                       `id`
    )
    REFERENCES `tbl_car_contract` (
                                   `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_inspection` ADD CONSTRAINT `FK_tbl_car_TO_tbl_inspection_1` FOREIGN KEY (
                                                                                          `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_contract_cost` ADD CONSTRAINT `FK_tbl_car_TO_tbl_contract_cost_1` FOREIGN KEY (
                                                                                                `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_contract_cost` ADD CONSTRAINT `FK_tbl_cost_type_TO_tbl_contract_cost_1` FOREIGN KEY (
                                                                                                      `cost_type_id`
    )
    REFERENCES `tbl_cost_type` (
                                `id`
        );

ALTER TABLE `tbl_contract_cost` ADD CONSTRAINT `FK_tbl_car_contract_TO_tbl_contract_cost_1` FOREIGN KEY (
                                                                                                         `contract_id`
    )
    REFERENCES `tbl_car_contract` (
                                   `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_purchase_detail` ADD CONSTRAINT `FK_tbl_car_contract_TO_tbl_purchase_detail_1` FOREIGN KEY (
                                                                                                             `id`
    )
    REFERENCES `tbl_car_contract` (
                                   `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_reservation` ADD CONSTRAINT `FK_tbl_car_TO_tbl_reservation_1` FOREIGN KEY (
                                                                                            `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_reservation` ADD CONSTRAINT `FK_tbl_employee_TO_tbl_reservation_1` FOREIGN KEY (
                                                                                                 `member_emp_email`
    )
    REFERENCES `tbl_employee` (
                               `email`
        );

ALTER TABLE `tbl_rent_detail` ADD CONSTRAINT `FK_tbl_car_contract_TO_tbl_rent_detail_1` FOREIGN KEY (
                                                                                                     `id`
    )
    REFERENCES `tbl_car_contract` (
                                   `id`
        ) ON DELETE CASCADE;

ALTER TABLE `tbl_provided_log` ADD CONSTRAINT `FK_tbl_employee_TO_tbl_provided_log_1` FOREIGN KEY (
                                                                                                   `emp_email`
    )
    REFERENCES `tbl_employee` (
                               `email`
        );

ALTER TABLE `tbl_provided_log` ADD CONSTRAINT `FK_tbl_car_TO_tbl_provided_log_1` FOREIGN KEY (
                                                                                              `car_id`
    )
    REFERENCES `tbl_car` (
                          `id`
        ) ON DELETE CASCADE;


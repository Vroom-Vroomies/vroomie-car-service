-- Drop tables in reverse order of dependencies
DROP TABLE IF EXISTS `tbl_car_checkups`;
DROP TABLE IF EXISTS `tbl_accident`;
DROP TABLE IF EXISTS `tbl_log`;
DROP TABLE IF EXISTS `tbl_book_history`;
DROP TABLE IF EXISTS `tbl_repair`;
DROP TABLE IF EXISTS `tbl_result`;
DROP TABLE IF EXISTS `tbl_car`;

-- Create tables with inline PRIMARY KEY definitions for AUTO_INCREMENT columns

CREATE TABLE `tbl_car` (
	`id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`identification`	VARCHAR(30)	NOT NULL,
	`number`	VARCHAR(15)	NOT NULL,
	`model`	VARCHAR(100)	NOT NULL,
	`type`	VARCHAR(15)	NOT NULL,
	`book_status`	VARCHAR(15)	NOT NULL,
	`mileage`	BIGINT	NULL,
	`color`	VARCHAR(15)	NOT NULL,
	`year`	BIGINT	NOT NULL,
	`contract_type`	ENUM('RENT', 'LEASE', 'PURCHASE') NOT NULL DEFAULT 'RENT',
	`contract_date`	DATE	NOT NULL,
	`car_status`	ENUM('GOOD', 'FAIR', 'DAMAGED')	NOT NULL DEFAULT 'GOOD',
	`insurance_expiration`	DATE	NOT NULL,
	`last_inspection`	DATE	NULL
);

CREATE TABLE `tbl_result` (
	`id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`exterior_inspection`	VARCHAR(255)	NULL,
	`lighting_system`	VARCHAR(255)	NULL,
	`brake_system`	VARCHAR(255)	NULL,
	`steering_system`	VARCHAR(255)	NULL,
	`tire_condition`	VARCHAR(255)	NULL,
	`emission_test`	VARCHAR(255)	NULL,
	`noise_test`	VARCHAR(255)	NULL,
	`speedometer_test`	VARCHAR(255)	NULL,
	`electrical_system`	VARCHAR(255)	NULL
);

CREATE TABLE `tbl_accident` (
	`id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`car_id`	BIGINT	NOT NULL,
	`emp_id`	BIGINT	NOT NULL,
	`type`	VARCHAR(100)	NOT NULL,
	`detail`	VARCHAR(255)	NOT NULL,
	`date`	DATE	NOT NULL,
	FOREIGN KEY (`car_id`) REFERENCES `tbl_car` (`id`)
);

CREATE TABLE `tbl_log` (
	`id`	VARCHAR(255)	NOT NULL PRIMARY KEY,
	`emp_id`	BIGINT	NOT NULL,
	`car_id`	BIGINT	NOT NULL,
	`start_location`	VARCHAR(255)	NOT NULL,
	`end_location`	VARCHAR(255)	NOT NULL,
	`purpose`	VARCHAR(255)	NOT NULL,
	`start_odometer`	BIGINT	NOT NULL,
	`end_odometer`	BIGINT	NOT NULL,
	`start_time`	DATETIME	NOT NULL,
	`end_time`	DATETIME	NOT NULL,
	`log_status`	ENUM('IN_PROGRESS', 'COMPLETED')	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NULL,
	FOREIGN KEY (`car_id`) REFERENCES `tbl_car` (`id`)
);

CREATE TABLE `tbl_book_history` (
	`id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`car_id`	BIGINT	NOT NULL,
	`emp_id`	BIGINT	NOT NULL,
	`book_start`	DATETIME NULL,
	`book_end`	DATETIME	NULL,
	`return_date`	DATETIME	NULL,
	`book_status`	ENUM('RENTED', 'RETURNED', 'CANCELLED')	NULL DEFAULT 'RENTED',
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NULL,
	FOREIGN KEY (`car_id`) REFERENCES `tbl_car` (`id`)
);

CREATE TABLE `tbl_repair` (
	`id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`car_id`	BIGINT	NOT NULL,
	`type`	VARCHAR(100)	NOT NULL,
	`detail`	VARCHAR(255)	NOT NULL,
	`status`	ENUM('REQUESTED', 'IN_PROGRESS', 'COMPLETED')	NOT NULL,
	`start_date`	DATE	NOT NULL,
	`end_date`	DATE	NULL,
	FOREIGN KEY (`car_id`) REFERENCES `tbl_car` (`id`)
);

CREATE TABLE `tbl_car_checkups` (
	`id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`inspection_date`	DATETIME	NULL,
	`inspection_center_name`	VARCHAR(30)	NULL,
	`inspector_name`	VARCHAR(30)	NULL,
	`inspection_type`	VARCHAR(100)	NULL,
	`final_result`	VARCHAR(255)	NULL,
	`result_id`	BIGINT	NOT NULL,
	`valid_until`	DATE	NULL,
	`failure_reason`	VARCHAR(255)	NULL,
	`reinspection_required`	BOOLEAN	NULL,
	`remarks`	VARCHAR(255)	NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NULL,
	`created_by`	BIGINT	NOT NULL,
	`car_id`	BIGINT	NOT NULL,
	FOREIGN KEY (`result_id`) REFERENCES `tbl_result` (`id`),
	FOREIGN KEY (`car_id`) REFERENCES `tbl_car` (`id`)
);
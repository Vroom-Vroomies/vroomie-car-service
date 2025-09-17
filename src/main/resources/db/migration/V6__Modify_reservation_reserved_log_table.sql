ALTER TABLE `tbl_reservation` 
CHANGE COLUMN `reservation_start` `started_at` DATETIME NULL,
CHANGE COLUMN `reservation_end` `ended_at` DATETIME NULL,
CHANGE COLUMN `reservation_status` `status` ENUM('PENDING','APPROVED','REJECTED','CANCLED') NULL;

ALTER TABLE `tbl_reserved_log`
CHANGE COLUMN `reserved_status` `status` ENUM('RENTED', 'RETURNED', 'OVERDUE', 'RESERVED') NULL DEFAULT 'RENTED',
CHANGE COLUMN `return_date` `return_date` DATETIME NULL,
DROP COLUMN `updated_at`;
-- V9__modify_contract_tables.sql
-- 기존 스키마를 새로운 구조로 맞추고, NOT NULL 제약 충돌 방지를 위한 기본값 세팅

-- tbl_car_contract NULL 방어
UPDATE tbl_car_contract
SET provider   = COALESCE(provider, 'UNKNOWN'),
    start_at   = COALESCE(start_at, '2000-01-01'),
    end_at     = COALESCE(end_at, '2099-12-31'),
    contract_type = COALESCE(contract_type, 'LEASE'),
    status     = COALESCE(status, 'NEW')
WHERE provider IS NULL
   OR start_at IS NULL
   OR end_at IS NULL
   OR contract_type IS NULL
   OR status IS NULL;

-- tbl_insurance NULL 방어
UPDATE tbl_insurance
SET start_date = COALESCE(start_date, '2000-01-01'),
    end_date   = COALESCE(end_date, '2099-12-31'),
    premium    = COALESCE(premium, 0.00),
    company    = COALESCE(company, 'UNKNOWN'),
    name       = COALESCE(name, 'UNKNOWN'),
    status     = COALESCE(status, 'NEW')
WHERE start_date IS NULL
   OR end_date IS NULL
   OR premium IS NULL
   OR company IS NULL
   OR name IS NULL
   OR status IS NULL;

-- tbl_lease_detail NULL 방어
UPDATE tbl_lease_detail
SET monthly_lease = COALESCE(monthly_lease, 0.00),
    type          = COALESCE(type, 'OPERATING_LEASE')
WHERE monthly_lease IS NULL
   OR type IS NULL;

-- tbl_rent_detail NULL 방어
UPDATE tbl_rent_detail
SET monthly_rent = COALESCE(monthly_rent, 0.00),
    type         = COALESCE(type, 'LONG_RENT')
WHERE monthly_rent IS NULL
   OR type IS NULL;

-- tbl_purchase_detail NULL 방어
UPDATE tbl_purchase_detail
SET purchase_price = COALESCE(purchase_price, 0.00)
WHERE purchase_price IS NULL;

-- tbl_car_contract ALTER
ALTER TABLE `tbl_car_contract`
    MODIFY `contract_type` ENUM('LEASE', 'RENT', 'PURCHASE') NOT NULL,
    MODIFY `provider` VARCHAR(50) NOT NULL,
    MODIFY `insurance_included` BOOLEAN DEFAULT FALSE,
    MODIFY `start_at` DATE NOT NULL,
    MODIFY `end_at` DATE NOT NULL,
    MODIFY `status` ENUM('NEW', 'RENEWED', 'EXPIRED') DEFAULT 'NEW',
    ADD COLUMN `payment_day` INT NULL AFTER `status`,
    ADD COLUMN `payment_cycle` ENUM('DAILY','WEEKLY','MONTHLY','QUARTERLY','YEARLY') DEFAULT 'MONTHLY' AFTER `payment_day`,
    ADD COLUMN `first_payment_day` DATE NULL AFTER `payment_cycle`;

-- tbl_lease_detail ALTER
ALTER TABLE `tbl_lease_detail`
    MODIFY `monthly_lease` DECIMAL(10,2) NOT NULL,
    MODIFY `type` ENUM('FINANCE_LEASE', 'OPERATING_LEASE') DEFAULT 'OPERATING_LEASE';

-- tbl_rent_detail ALTER
ALTER TABLE `tbl_rent_detail`
    MODIFY `monthly_rent` DECIMAL(10, 2) NOT NULL,
DROP COLUMN `payment_cycle`,
    MODIFY `type` ENUM('SHORT_RENT', 'LONG_RENT') DEFAULT 'LONG_RENT';

-- tbl_purchase_detail ALTER
ALTER TABLE `tbl_purchase_detail`
    MODIFY `purchase_price` DECIMAL(10, 2) NOT NULL;

-- tbl_insurance ALTER
ALTER TABLE `tbl_insurance`
    MODIFY `start_date` DATE NOT NULL,
    MODIFY `end_date` DATE NOT NULL,
    MODIFY `premium` DECIMAL(10, 2) NOT NULL,
    MODIFY `company` VARCHAR(50) NOT NULL,
    MODIFY `name` VARCHAR(100) NOT NULL,
    MODIFY `status` ENUM('NEW', 'RENEWED', 'EXPIRED') DEFAULT 'NEW',
    ADD COLUMN `payment_type` ENUM('LUMP_SUM','MONTHLY','QUARTERLY') DEFAULT 'MONTHLY' AFTER `status`,
    ADD COLUMN `payment_day` INT NULL AFTER `payment_type`,
    ADD COLUMN `first_payment_day` DATE NULL AFTER `payment_day`;

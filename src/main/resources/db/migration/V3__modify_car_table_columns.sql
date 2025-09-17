-- insu_expiration 컬럼을 NULL 허용으로 변경
ALTER TABLE tbl_car MODIFY COLUMN insu_expiration DATE NULL;

-- 기존 fuel_type 컬럼을 백업용으로 이름 변경
ALTER TABLE tbl_car RENAME COLUMN fuel_type TO fuel_type_varchar;

-- ENUM 타입의 fuel_type 컬럼을 추가
ALTER TABLE tbl_car ADD COLUMN fuel_type
    ENUM('GASOLINE', 'DIESEL', 'LPG', 'HYBRID', 'ELECTRIC', 'HYDROGEN', 'CNG') NULL;
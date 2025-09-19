-- 1. 기존 컬럼 삭제
ALTER TABLE `tbl_driving_log`
DROP COLUMN `purpose`,
    DROP COLUMN `detail`;

-- 2. 위치 관련 컬럼 추가 (ended_at 아래에 순서대로)
ALTER TABLE `tbl_driving_log`
    ADD COLUMN `start_lat` DECIMAL(10,7) NULL COMMENT '운행 시작 위도' AFTER `ended_at`,
    ADD COLUMN `start_lng` DECIMAL(10,7) NULL COMMENT '운행 시작 경도' AFTER `start_lat`,
    ADD COLUMN `end_lat` DECIMAL(10,7) NULL COMMENT '운행 종료 위도' AFTER `start_lng`,
    ADD COLUMN `end_lng` DECIMAL(10,7) NULL COMMENT '운행 종료 경도' AFTER `end_lat`,
    ADD COLUMN `start_location` VARCHAR( 255) NULL COMMENT '운행 시작 주소' AFTER `end_lng`,
    ADD COLUMN `end_location` VARCHAR(255) NULL COMMENT '운행 종료 주소' AFTER `start_location`,
    ADD COLUMN gps_distance DECIMAL(6,2) NULL COMMENT 'GPS 기반 운행 거리(km)' AFTER `end_location`,
    ADD COLUMN odometer_distance DECIMAL(6,2) NULL COMMENT '계기판 기반 운행 거리(km)' AFTER `gps_distance`;

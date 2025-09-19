-- detail 컬럼 추가
ALTER TABLE tbl_reservation ADD COLUMN detail VARCHAR(255) NULL;

-- 기존 purpose 데이터를 ENUM 값으로 매핑 업데이트
UPDATE tbl_reservation SET purpose = 'BUSINESS' WHERE purpose = '판교 고객사 미팅';
UPDATE tbl_reservation SET purpose = 'BUSINESS' WHERE purpose = '워크샵 장비 운반';
UPDATE tbl_reservation SET purpose = 'BUSINESS' WHERE purpose = '세미나 참석';
UPDATE tbl_reservation SET purpose = 'PERSONAL' WHERE purpose = '개인 용무';
UPDATE tbl_reservation SET purpose = 'BUSINESS' WHERE purpose = '부산 지사 출장';

-- purpose 컬럼을 ENUM으로 변경
ALTER TABLE tbl_reservation MODIFY COLUMN purpose ENUM('BUSINESS', 'PERSONAL', 'COMMUTING') NULL;
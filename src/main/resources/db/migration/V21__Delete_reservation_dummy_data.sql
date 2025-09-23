-- =================================================================
-- 예약/대여 관련 더미 데이터 삭제
-- =================================================================

-- 1. 대여 이력 더미 데이터 삭제 (tbl_reserved_log)
DELETE FROM `tbl_reserved_log` WHERE `reservation_id` IN (1, 2, 5);

-- 2. 차량 예약 더미 데이터 삭제 (tbl_reservation)  
DELETE FROM `tbl_reservation` WHERE `id` IN (1, 2, 3, 4, 5);


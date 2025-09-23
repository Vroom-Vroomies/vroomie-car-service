-- V21: 동시성 처리 성능 향상을 위한 인덱스 추가
-- 기존 인덱스와 충돌을 피하기 위해 다른 이름으로 생성

-- 1. 차량별 시간대 예약 충돌 검사용 인덱스
CREATE INDEX idx_car_reservation_time_conflict 
ON tbl_reservation (car_id, started_at, ended_at, status);

-- 2. 예약 로그의 차량별 시간대 충돌 검사용 인덱스  
CREATE INDEX idx_car_reserved_log_time_conflict 
ON tbl_reserved_log (car_id, started_at, ended_at, status);

-- 3. 사용자별 활성 예약 조회용 인덱스
CREATE INDEX idx_member_active_reservations 
ON tbl_reservation (member_emp_email, status, created_at);

-- 4. 예약별 활성 대여 조회용 인덱스
CREATE INDEX idx_reservation_active_rentals 
ON tbl_reserved_log (reservation_id, status, created_at);
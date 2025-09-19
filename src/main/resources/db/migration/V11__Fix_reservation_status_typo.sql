-- reservation_status ENUM에서 CANCLED -> CANCELLED 오타 수정
ALTER TABLE tbl_reservation 
MODIFY COLUMN reservation_status ENUM('PENDING','APPROVED','REJECTED','CANCELLED') NULL;
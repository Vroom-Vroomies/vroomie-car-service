
-- =================================================================
-- 더미 데이터
-- =================================================================

-- 1. 회사 정보 (tbl_company)
INSERT INTO `tbl_company` (`name`) VALUES ('위대한상상');


-- 2. 비용 타입 정보 (tbl_cost_type)
INSERT INTO `tbl_cost_type` (`cost_type_name`, `description`) VALUES
                                                                  ('월 리스료', '차량 리스 계약에 따른 월별 납부액'),
                                                                  ('월 렌탈료', '차량 렌트 계약에 따른 월별 납부액'),
                                                                  ('보험료', '연간 자동차 보험료'),
                                                                  ('자동차세', '연간 자동차세'),
                                                                  ('정비 비용', '주기적 또는 비주기적 차량 정비 비용');


-- 3. 직원 정보 (tbl_employee)
INSERT INTO `tbl_employee` (`email`, `name`, `phone`, `department`, `is_deleted`, `licence_status`, `licence_image`, `role`, `is_active`) VALUES
                                                                                                                                              ('superadmin@wemade.com', '나총괄', '010-0000-0000', '경영지원팀', false, true, NULL, 'SUPER_ADMIN', true),
                                                                                                                                              ('admin@wemade.com', '김관리', '010-1234-5678', '총무팀', false, true, NULL, 'ADMIN', true),
                                                                                                                                              ('user01@wemade.com', '박선우', '010-1111-2222', '개발1팀', false, true, NULL, 'USER', true),
                                                                                                                                              ('user02@wemade.com', '이하나', '010-3333-4444', '개발2팀', false, true, NULL, 'USER', true),
                                                                                                                                              ('user03@wemade.com', '최민준', '010-5555-6666', '디자인팀', false, false, NULL, 'USER', true),
                                                                                                                                              ('user04@wemade.com', '정다은', '010-7777-8888', '기획팀', false, true, NULL, 'USER', true),
                                                                                                                                              ('user05@wemade.com', '윤지훈', '010-9999-0000', '마케팅팀', false, true, NULL, 'USER', false),
                                                                                                                                              ('user06@wemade.com', '강서연', '010-2345-6789', '인사팀', true, true, NULL, 'USER', true);


-- 4. 차량 정보 (tbl_car)
INSERT INTO `tbl_car` (`company_id`, `identification`, `number`, `image`, `model`, `type`, `total_mileage`, `color`, `year`, `status`, `insu_expiration`, `last_inspection`, `inspection_cycle`, `allowable_capacity`, `fuel_type`, `usage_type`, `gear_type`) VALUES
                                                                                                                                                                                                                                                                   (1, 'KNCW12345A1234567', '12가1234', 'car_image_01.jpg', '현대 쏘나타', '중형', 55000, '흰색', 2022, 'ACTIVE', '2025-10-01', '2024-10-01', 24, 5, '휘발유', 'SHARED', 'AUTO'),
                                                                                                                                                                                                                                                                   (1, 'KNCW67890B6789012', '34나5678', 'car_image_02.jpg', '기아 카니발', '대형', 89000, '검정색', 2021, 'ACTIVE', '2025-11-15', '2024-11-15', 24, 9, '경유', 'SHARED', 'AUTO'),
                                                                                                                                                                                                                                                                   (1, 'KNCW11223C1122334', '56다9012', 'car_image_03.jpg', '제네시스 G80', '준대형', 21000, '은색', 2023, 'ACTIVE', '2025-08-20', '2024-08-20', 24, 5, '휘발유', 'ASSIGNED', 'AUTO'),
                                                                                                                                                                                                                                                                   (1, 'KNCW44556D4455667', '78라3456', 'car_image_04.jpg', '현대 아반떼', '준중형', 120000, '파란색', 2020, 'ACTIVE', '2025-06-30', '2024-06-30', 24, 5, 'LPG', 'SHARED', 'AUTO'),
                                                                                                                                                                                                                                                                   (1, 'KNCW77889E7788990', '10마7890', 'car_image_05.jpg', '기아 K5', '중형', 45000, '회색', 2022, 'SOLD', '2024-05-10', '2023-05-10', 24, 5, '하이브리드', 'SHARED', 'AUTO'),
                                                                                                                                                                                                                                                                   (1, 'KNCW98765F5432109', '23바1122', 'car_image_06.jpg', 'KG모빌리티 토레스', '중형SUV', 32000, '카키', 2023, 'ACTIVE', '2025-03-12', '2024-03-12', 24, 5, '휘발유', 'SHARED', 'AUTO'),
                                                                                                                                                                                                                                                                   (1, 'KNCW12121G2323234', '45사3344', 'car_image_07.jpg', '현대 아이오닉 5', '중형SUV', 15000, '흰색', 2023, 'ACTIVE', '2025-09-01', '2024-09-01', 24, 5, '전기', 'SHARED', 'AUTO'),
                                                                                                                                                                                                                                                                   (1, 'KNCW45454H6767678', '67아5566', 'car_image_08.jpg', '기아 레이', '경형', 76000, '민트', 2021, 'SCRAPPED', '2024-01-20', '2023-01-20', 24, 5, '휘발유', 'SHARED', 'AUTO'),
                                                                                                                                                                                                                                                                   (1, 'KNCW89898J1010101', '89자7788', 'car_image_09.jpg', '제네시스 GV70', '중형SUV', 41000, '검정색', 2022, 'ACTIVE', '2025-07-07', '2024-07-07', 24, 5, '경유', 'ASSIGNED', 'AUTO');


-- 5. 차량 계약 정보 (tbl_car_contract)
INSERT INTO `tbl_car_contract` (`car_id`, `contract_type`, `provider`, `monthly_fee`, `insurance_included`, `start_at`, `end_at`, `status`) VALUES
                                                                                                                                                (1, 'LEASE', '현대캐피탈', 650000.00, true, '2022-10-02', '2025-10-01', 'RENEWED'),
                                                                                                                                                (2, 'RENT', '롯데렌터카', 850000.00, true, '2021-11-16', '2025-11-15', 'NEW'),
                                                                                                                                                (3, 'PURCHASE', '자체구매', 0.00, false, '2023-08-21', NULL, 'NEW'),
                                                                                                                                                (4, 'RENT', 'SK렌터카', 450000.00, true, '2022-07-01', '2025-06-30', 'NEW'),
                                                                                                                                                (5, 'LEASE', 'KB캐피탈', 580000.00, true, '2022-05-11', '2024-05-10', 'EXPIRED'),
                                                                                                                                                (6, 'PURCHASE', '자체구매', 0.00, false, '2023-03-13', NULL, 'NEW'),
                                                                                                                                                (7, 'LEASE', '하나캐피탈', 720000.00, true, '2023-09-02', '2026-09-01', 'NEW'),
                                                                                                                                                (9, 'RENT', '롯데렌터카', 950000.00, true, '2022-07-08', '2025-07-07', 'NEW');


-- 6. 리스/렌트/구매 상세 정보
INSERT INTO `tbl_lease_detail` (`id`, `monthly_lease`, `lease_period_months`, `residual_value`, `buyout_option_price`, `mileage_limit`, `excess_mileage_rate`, `type`) VALUES
                                                                                                                                                                           (1, 650000.00, 36, 15000000.00, 16000000.00, 20000, 100.00, 'OPERATING_LEASE'),
                                                                                                                                                                           (5, 580000.00, 24, 18000000.00, 18500000.00, 20000, 100.00, 'OPERATING_LEASE'),
                                                                                                                                                                           (7, 720000.00, 48, 25000000.00, 26500000.00, 25000, 150.00, 'FINANCE_LEASE');

-- 렌트 (tbl_rent_detail)
INSERT INTO `tbl_rent_detail` (`id`, `monthly_rent`, `deposit`, `payment_cycle`, `auto_renewal`, `type`) VALUES
                                                                                                             (2, 850000.00, 5000000.00, 1, true, 'LONG_RENT'),
                                                                                                             (4, 450000.00, 2000000.00, 1, false, 'LONG_RENT'),
                                                                                                             (8, 950000.00, 10000000.00, 1, true, 'LONG_RENT');

-- 구매 (tbl_purchase_detail)
INSERT INTO `tbl_purchase_detail` (`id`, `purchase_price`, `down_payment`, `loan_amount`, `interest_rate`, `loan_term_months`, `monthly_payment`) VALUES
                                                                                                                                                      (3, 65000000.00, 20000000.00, 45000000.00, 5.2, 36, 1350000.00),
                                                                                                                                                      (6, 38000000.00, 38000000.00, 0.00, 0.0, 0, 0.00);


-- 7. 차량 예약 정보 (tbl_reservation)
INSERT INTO `tbl_reservation` (`car_id`, `member_emp_email`, `reservation_start`, `reservation_end`, `reservation_status`, `purpose`, `created_at`) VALUES
                                                                                                                                                        (1, 'user01@wemade.com', '2025-09-15 09:00:00', '2025-09-15 18:00:00', 'APPROVED', '판교 고객사 미팅', NOW()),
                                                                                                                                                        (2, 'user04@wemade.com', '2025-09-16 10:00:00', '2025-09-16 15:00:00', 'APPROVED', '워크샵 장비 운반', NOW()),
                                                                                                                                                        (4, 'user02@wemade.com', '2025-09-18 13:00:00', '2025-09-18 20:00:00', 'PENDING', '세미나 참석', NOW()),
                                                                                                                                                        (1, 'user01@wemade.com', '2025-09-20 09:00:00', '2025-09-20 12:00:00', 'REJECTED', '개인 용무', NOW()),
                                                                                                                                                        (7, 'user03@wemade.com', '2025-09-22 10:00:00', '2025-09-24 18:00:00', 'APPROVED', '부산 지사 출장', NOW());


-- 8. 운행 기록 (tbl_driving_log)
INSERT INTO `tbl_driving_log` (`emp_email`, `car_id`, `purpose`, `detail`, `start_odometer`, `end_odometer`, `started_at`, `ended_at`, `log_status`, `created_at`, `is_saved`) VALUES
                                                                                                                                                                                   ('user01@wemade.com', 1, 'BUSINESS', '판교 고객사 미팅', 54500, 54580, '2025-09-15 09:05:00', '2025-09-15 17:50:00', 'COMPLETED', NOW(), TRUE),
                                                                                                                                                                                   ('user04@wemade.com', 2, 'BUSINESS', '워크샵 장비 운반', 88500, 88620, '2025-09-16 10:10:00', '2025-09-16 14:45:00', 'COMPLETED', NOW(), TRUE),
                                                                                                                                                                                   ('user02@wemade.com', 4, 'BUSINESS', '강남 본사 교육 참석', 119800, 119850, '2025-08-10 09:00:00', '2025-08-10 18:30:00', 'COMPLETED', NOW(), TRUE),
                                                                                                                                                                                   ('user03@wemade.com', 7, 'COMMUTING', '자택-회사', 14800, 14830, '2025-08-11 08:30:00', '2025-08-11 09:10:00', 'COMPLETED', NOW(), TRUE),
                                                                                                                                                                                   ('user01@wemade.com', 1, 'BUSINESS', '인천공항 바이어 픽업', 53000, 53150, '2025-08-05 14:00:00', '2025-08-05 17:00:00', 'COMPLETED', NOW(), TRUE);


-- 9. 변동 비용 (tbl_variable_cost)
INSERT INTO `tbl_variable_cost` (`log_id`, `category`, `payment`, `cost`, `place`) VALUES
                                                                                       (1, 'PARKING_COST', 'COMPANY_CARD', 15000.00, '판교 공영 주차장'),
                                                                                       (1, 'TOLL_FEE', 'COMPANY_CARD', 2400.00, '판교IC'),
                                                                                       (2, 'FUEL_COST', 'COMPANY_CARD', 50000.00, 'GS칼텍스 양재'),
                                                                                       (3, 'PARKING_COST', 'PERSONAL_CARD', 20000.00, '강남 민영 주차장'),
                                                                                       (5, 'TOLL_FEE', 'COMPANY_CARD', 6600.00, '인천국제공항고속도로');


-- 10. 사고 기록 (tbl_accident)
INSERT INTO `tbl_accident` (`car_id`, `emp_email`, `type`, `note`, `detail`, `occurred_at`, `cost`) VALUES
                                                                                                        (2, 'user04@wemade.com', 'SINGLE_VEHICLE', '주차 중 접촉사고', '사내 주차장에서 후진 중 주차 기둥에 뒷범퍼 부딪힘', '2024-05-20', 350000.00),
                                                                                                        (5, 'user01@wemade.com', 'VS_VEHICLE', '후방 추돌', '정체 구간에서 뒷차가 추돌. 과실 100:0', '2023-11-10', 1200000.00);


-- 11. 정비 기록 (tbl_repair)
INSERT INTO `tbl_repair` (`emp_email`, `car_id`, `type`, `detail`, `status`, `started_at`, `ended_date`, `cost`, `is_saved`) VALUES
                                                                                                                                 ('admin@wemade.com', 2, 'PAINTING', '뒷범퍼 도색 및 복원', 'COMPLETED', '2024-05-21', '2024-05-23', 350000.00, TRUE),
                                                                                                                                 ('admin@wemade.com', 1, 'GENERAL', '엔진오일 및 필터 교체', 'COMPLETED', '2025-04-10', '2025-04-10', 150000.00, TRUE),
                                                                                                                                 ('admin@wemade.com', 4, 'REPLACEMENT', '타이어 4짝 교체', 'COMPLETED', '2025-07-15', '2025-07-15', 600000.00, TRUE),
                                                                                                                                 ('admin@wemade.com', 3, 'GENERAL', '정기 점검 및 와이퍼 교체', 'IN_REPAIR', '2025-09-12', NULL, 80000.00, FALSE);


-- 12. 정비 이미지 (tbl_repair_image)
INSERT INTO `tbl_repair_image` (`repair_id`, `type`, `image`) VALUES
                                                                  (1, 'BEFORE', 'repair_01_before.jpg'),
                                                                  (1, 'AFTER', 'repair_01_after.jpg');

-- 13. 보험 이력 (tbl_insurance)
INSERT INTO `tbl_insurance` (`car_id`, `start_date`, `end_date`, `premium`, `company`, `name`, `status`) VALUES
                                                                                                             (1, '2024-10-02 00:00:00', '2025-10-01 23:59:59', 1200000.00, '삼성화재 다이렉트', '업무용 자동차보험', 'RENEWED'),
                                                                                                             (2, '2024-11-16 00:00:00', '2025-11-15 23:59:59', 1800000.00, '현대해상', '업무용 자동차보험', 'RENEWED'),
                                                                                                             (3, '2024-08-21 00:00:00', '2025-08-20 23:59:59', 2200000.00, 'DB손해보험', '업무용 자동차보험', 'NEW'),
                                                                                                             (4, '2024-07-01 00:00:00', '2025-06-30 23:59:59', 950000.00, 'KB손해보험', '업무용 자동차보험', 'RENEWED');


-- 14. 정기점검 이력 (tbl_inspection)
INSERT INTO `tbl_inspection` (`car_id`, `date`, `center_name`, `inspector_name`, `inspection_type`, `final_result`, `valid_until`, `created_at`, `created_by`) VALUES
                                                                                                                                                                   (1, '2024-10-01 10:00:00', '블루핸즈 서현점', '김정비', 'REGULAR', '합격', '2026-09-30', NOW(), 'admin@wemade.com'),
                                                                                                                                                                   (2, '2023-11-10 14:30:00', '오토큐 분당점', '박기술', 'COMPREHENCE', '합격', '2025-11-09', NOW(), 'admin@wemade.com'),
                                                                                                                                                                   (4, '2024-06-25 11:00:00', '블루핸즈 야탑점', '이점검', 'REGULAR', '불합격', NULL, NOW(), 'admin@wemade.com');


-- 15. 계약 고정 비용 내역 (tbl_contract_cost)
INSERT INTO `tbl_contract_cost` (`car_id`, `contract_id`, `cost_type_id`, `amount`, `cost_date`, `description`) VALUES
                                                                                                                    (1, 1, 1, 650000.00, '2025-09-05', '쏘나타 25년 9월 리스료'),
                                                                                                                    (2, 2, 2, 850000.00, '2025-09-10', '카니발 25년 9월 렌탈료'),
                                                                                                                    (4, 4, 2, 450000.00, '2025-09-15', '아반떼 25년 9월 렌탈료'),
                                                                                                                    (3, 3, 4, 1300000.00, '2025-06-10', 'G80 25년 자동차세(연납)'),
                                                                                                                    (9, 8, 2, 950000.00, '2025-09-20', 'GV70 25년 9월 렌탈료');


-- 16. 차량 지급 이력 (tbl_provided_log)
INSERT INTO `tbl_provided_log` (`emp_email`, `car_id`, `provide_date`, `return_date`, `created_at`) VALUES
                                                                                                        ('superadmin@wemade.com', 3, '2023-08-21', NULL, NOW()), -- G80
                                                                                                        ('admin@wemade.com', 9, '2022-07-08', NULL, NOW());      -- GV70


-- 17. 대여 이력 (tbl_reserved_log)
INSERT INTO `tbl_reserved_log` (`car_id`, `admin_emp_email`, `reservation_id`, `started_at`, `ended_at`, `reserved_status`, `created_at`, `return_date`) VALUES
                                                                                                                                                             (1, 'admin@wemade.com', 1, '2025-09-15 09:00:00', '2025-09-15 18:00:00', 'RENTED', '2025-09-15 08:55:00', NULL),
                                                                                                                                                             (2, 'admin@wemade.com', 2, '2025-09-16 10:00:00', '2025-09-16 15:00:00', 'RETURNED', '2025-09-16 09:58:00', '2025-09-16 15:10:00'),
                                                                                                                                                             (7, 'admin@wemade.com', 5, '2025-09-22 10:00:00', '2025-09-24 18:00:00', 'RENTED', '2025-09-22 09:55:00', NULL);


-- 18. 사고 이미지 (tbl_accident_image)
INSERT INTO `tbl_accident_image` (`accident_id`, `image`) VALUES
                                                              (1, 'accident_01_bumper.jpg'),
                                                              (1, 'accident_01_pillar.jpg');
-- V25__Add_comprehensive_dashboard_dummy_data.sql
-- 대시보드 분석을 위한 종합적인 더미 데이터 추가 (2025.09.24 기준)
-- 회사 ID = 1 (위대한상상)에 대한 데이터만 추가

-- =================================================================
-- 1. 추가 차량 정보 (다양한 차량 타입으로 풍성한 데이터 생성)
-- =================================================================
INSERT INTO `tbl_car` (`company_id`, `identification`, `number`, `image`, `model`, `type`, `total_mileage`, `color`, `year`, `status`, `insu_expiration`, `last_inspection`, `inspection_cycle`, `allowable_capacity`, `fuel_type`, `usage_type`, `gear_type`) VALUES
-- 승용차 추가
(1, 'KNCW11111Z1111111', '11가1111', 'car_image_10.jpg', '현대 그랜저', '준대형', 35000, '검정색', 2024, 'ACTIVE', '2026-01-15', '2025-01-15', 24, 5, 'GASOLINE', 'SHARED', 'AUTO'),
(1, 'KNCW22222Z2222222', '22나2222', 'car_image_11.jpg', '기아 스타리아', '대형', 42000, '흰색', 2023, 'ACTIVE', '2025-12-20', '2024-12-20', 24, 11, 'DIESEL', 'SHARED', 'AUTO'),
(1, 'KNCW33333Z3333333', '33다3333', 'car_image_12.jpg', '현대 아이오닉 6', '중형', 28000, '회색', 2024, 'ACTIVE', '2026-02-10', '2025-02-10', 24, 5, 'ELECTRIC', 'SHARED', 'AUTO'),
(1, 'KNCW44444Z4444444', '44라4444', 'car_image_13.jpg', '기아 스포티지', '중형SUV', 65000, '빨간색', 2022, 'ACTIVE', '2025-10-30', '2024-10-30', 24, 5, 'GASOLINE', 'SHARED', 'AUTO'),
(1, 'KNCW55555Z5555555', '55마5555', 'car_image_14.jpg', '제네시스 G90', '대형', 15000, '검정색', 2024, 'ACTIVE', '2026-03-05', '2025-03-05', 24, 5, 'GASOLINE', 'ASSIGNED', 'AUTO'),
-- 트럭 및 상용차 추가
(1, 'KNCW66666Z6666666', '66바6666', 'truck_image_01.jpg', '현대 포터', '소형트럭', 95000, '흰색', 2021, 'ACTIVE', '2025-11-25', '2024-11-25', 12, 3, 'DIESEL', 'SHARED', 'MANUAL'),
(1, 'KNCW77777Z7777777', '77사7777', 'truck_image_02.jpg', '기아 봉고', '소형트럭', 78000, '파란색', 2022, 'ACTIVE', '2025-12-12', '2024-12-12', 12, 3, 'DIESEL', 'SHARED', 'MANUAL'),
(1, 'KNCW88888Z8888888', '88아8888', 'truck_image_03.jpg', '현대 마이티', '중형트럭', 125000, '노란색', 2020, 'ACTIVE', '2025-08-15', '2024-08-15', 12, 3, 'DIESEL', 'SHARED', 'MANUAL'),
(1, 'KNCW99999Z9999999', '99자9999', 'van_image_01.jpg', '기아 카니발', '승합', 55000, '은색', 2023, 'ACTIVE', '2026-01-30', '2025-01-30', 24, 11, 'DIESEL', 'SHARED', 'AUTO'),
-- 전기차 및 하이브리드 추가
(1, 'KNCW00000Z0000000', '00차0000', 'car_image_15.jpg', '현대 코나EV', '소형SUV', 22000, '청색', 2023, 'ACTIVE', '2025-12-01', '2024-12-01', 24, 5, 'ELECTRIC', 'SHARED', 'AUTO'),
(1, 'KNCW12345Z1234567', '12카1234', 'car_image_16.jpg', '기아 EV6', '중형SUV', 18000, '흰색', 2024, 'ACTIVE', '2026-04-15', '2025-04-15', 24, 5, 'ELECTRIC', 'SHARED', 'AUTO'),
(1, 'KNCW98765Z9876543', '98타9876', 'car_image_17.jpg', '현대 투싼 하이브리드', '중형SUV', 48000, '검정색', 2022, 'ACTIVE', '2025-09-20', '2024-09-20', 24, 5, 'HYBRID', 'SHARED', 'AUTO'),
-- 점검중, 수리중 차량 추가
(1, 'KNCW11122Z1112233', '11파1122', 'car_image_18.jpg', '기아 쏘렌토', '중형SUV', 88000, '갈색', 2021, 'ACTIVE', '2025-10-10', '2024-10-10', 24, 7, 'DIESEL', 'SHARED', 'AUTO'),
(1, 'KNCW33344Z3334455', '33하3344', 'car_image_19.jpg', '현대 벨로스터', '준중형', 95000, '노란색', 2020, 'ACTIVE', '2025-07-25', '2024-07-25', 24, 4, 'GASOLINE', 'SHARED', 'MANUAL'),
-- 보험 만료 예정 차량
(1, 'KNCW55566Z5556677', '55거5566', 'car_image_20.jpg', '기아 모닝', '경형', 102000, '민트', 2019, 'ACTIVE', '2025-09-30', '2024-09-30', 24, 5, 'GASOLINE', 'SHARED', 'AUTO');

-- =================================================================
-- 2. 추가 차량 계약 정보
-- =================================================================
INSERT INTO `tbl_car_contract` (`car_id`, `contract_type`, `provider`, `monthly_fee`, `insurance_included`, `start_at`, `end_at`, `status`) VALUES
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW11111Z1111111'), 'LEASE', '현대캐피탈', 750000.00, true, '2024-01-16', '2027-01-15', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW22222Z2222222'), 'RENT', 'SK렌터카', 920000.00, true, '2023-12-21', '2025-12-20', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW33333Z3333333'), 'LEASE', 'KB캐피탈', 680000.00, true, '2024-02-11', '2027-02-10', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW44444Z4444444'), 'PURCHASE', '자체구매', 0.00, false, '2022-10-31', '2032-10-31', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW55555Z5555555'), 'LEASE', '하나캐피탈', 1200000.00, true, '2024-03-06', '2027-03-05', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW66666Z6666666'), 'RENT', '롯데렌터카', 380000.00, true, '2021-11-26', '2025-11-25', 'RENEWED'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW77777Z7777777'), 'RENT', 'SK렌터카', 350000.00, true, '2022-12-13', '2025-12-12', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW88888Z8888888'), 'PURCHASE', '자체구매', 0.00, false, '2020-08-16', '2030-08-16', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW99999Z9999999'), 'RENT', '롯데렌터카', 880000.00, true, '2023-01-31', '2026-01-30', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW00000Z0000000'), 'LEASE', '현대캐피탈', 520000.00, true, '2023-12-02', '2026-12-01', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW12345Z1234567'), 'LEASE', 'KB캐피탈', 720000.00, true, '2024-04-16', '2027-04-15', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW98765Z9876543'), 'PURCHASE', '자체구매', 0.00, false, '2022-09-21', '2032-09-21', 'NEW'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW11122Z1112233'), 'RENT', 'SK렌터카', 760000.00, true, '2021-10-11', '2025-10-10', 'RENEWED'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW33344Z3334455'), 'LEASE', '하나캐피탈', 480000.00, true, '2020-07-26', '2024-07-25', 'EXPIRED'),
((SELECT id FROM `tbl_car` WHERE identification = 'KNCW55566Z5556677'), 'PURCHASE', '자체구매', 0.00, false, '2019-09-30', '2029-09-30', 'NEW');

-- =================================================================
-- 3. 계약 상세 정보 추가
-- =================================================================
-- 리스 상세
INSERT INTO `tbl_lease_detail` (`id`, `monthly_lease`, `lease_period_months`, `residual_value`, `buyout_option_price`, `mileage_limit`, `excess_mileage_rate`, `type`) VALUES
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW11111Z1111111' AND cc.contract_type = 'LEASE'), 750000.00, 36, 18000000.00, 19000000.00, 20000, 120.00, 'OPERATING_LEASE'),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW33333Z3333333' AND cc.contract_type = 'LEASE'), 680000.00, 36, 22000000.00, 23000000.00, 25000, 130.00, 'FINANCE_LEASE'),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW55555Z5555555' AND cc.contract_type = 'LEASE'), 1200000.00, 36, 35000000.00, 37000000.00, 30000, 200.00, 'OPERATING_LEASE'),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW00000Z0000000' AND cc.contract_type = 'LEASE'), 520000.00, 36, 16000000.00, 17000000.00, 20000, 110.00, 'OPERATING_LEASE'),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW12345Z1234567' AND cc.contract_type = 'LEASE'), 720000.00, 36, 24000000.00, 25000000.00, 25000, 140.00, 'FINANCE_LEASE'),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW33344Z3334455' AND cc.contract_type = 'LEASE'), 480000.00, 48, 14000000.00, 15000000.00, 20000, 100.00, 'OPERATING_LEASE');

-- 렌트 상세
INSERT INTO `tbl_rent_detail` (`id`, `monthly_rent`, `deposit`, `auto_renewal`, `type`) VALUES
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW22222Z2222222' AND cc.contract_type = 'RENT'), 920000.00, 8000000.00, true, 'LONG_RENT'),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW66666Z6666666' AND cc.contract_type = 'RENT'), 380000.00, 1500000.00, false, 'LONG_RENT'),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW77777Z7777777' AND cc.contract_type = 'RENT'), 350000.00, 1200000.00, true, 'LONG_RENT'),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW99999Z9999999' AND cc.contract_type = 'RENT'), 880000.00, 7000000.00,  true, 'LONG_RENT'),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW11122Z1112233' AND cc.contract_type = 'RENT'), 760000.00, 3000000.00, false, 'LONG_RENT');

-- 구매 상세
INSERT INTO `tbl_purchase_detail` (`id`, `purchase_price`, `down_payment`, `loan_amount`, `interest_rate`, `loan_term_months`, `monthly_payment`) VALUES
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW44444Z4444444' AND cc.contract_type = 'PURCHASE'), 42000000.00, 15000000.00, 27000000.00, 4.8, 48, 580000.00),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW88888Z8888888' AND cc.contract_type = 'PURCHASE'), 35000000.00, 10000000.00, 25000000.00, 5.1, 60, 470000.00),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW98765Z9876543' AND cc.contract_type = 'PURCHASE'), 48000000.00, 20000000.00, 28000000.00, 4.9, 36, 840000.00),
((SELECT cc.id FROM `tbl_car_contract` cc JOIN `tbl_car` c ON cc.car_id = c.id WHERE c.identification = 'KNCW55566Z5556677' AND cc.contract_type = 'PURCHASE'), 22000000.00, 22000000.00, 0.00, 0.0, 0, 0.00);

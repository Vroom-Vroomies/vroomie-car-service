-- 기존 데이터 삭제 (필요 시 CASCADE 적용)
DELETE FROM tbl_insurance;
DELETE FROM tbl_purchase_detail;
DELETE FROM tbl_rent_detail;
DELETE FROM tbl_lease_detail;
DELETE FROM tbl_car_contract;

-- 변경된 스키마에 맞게 더미 데이터 재삽입

-- 5. 차량 계약 정보 (tbl_car_contract)
INSERT INTO `tbl_car_contract`
(`id`, `car_id`, `contract_type`, `provider`, `monthly_fee`, `insurance_included`,
 `start_at`, `end_at`, `status`, `payment_day`, `payment_cycle`, `first_payment_day`)
VALUES
    (1, 1, 'LEASE', '현대캐피탈', 650000.00, TRUE, '2022-10-02', '2025-10-01', 'RENEWED', 5, 'MONTHLY', '2022-11-02'),
    (2, 2, 'RENT', '롯데렌터카', 850000.00, TRUE, '2021-11-16', '2025-11-15', 'NEW', 10, 'MONTHLY', '2021-12-16'),
    (3, 3, 'PURCHASE', '자체구매', 0.00, FALSE, '2023-08-21', '2026-08-20', 'NEW', NULL, 'YEARLY', '2023-08-21'), -- 수정: LUMP_SUM → YEARLY
    (4, 4, 'RENT', 'SK렌터카', 450000.00, TRUE, '2022-07-01', '2025-06-30', 'NEW', 15, 'MONTHLY', '2022-08-01'),
    (5, 5, 'LEASE', 'KB캐피탈', 580000.00, TRUE, '2022-05-11', '2024-05-10', 'EXPIRED', 11, 'MONTHLY', '2022-06-11'),
    (6, 6, 'PURCHASE', '자체구매', 0.00, FALSE, '2023-03-13', '2028-03-12', 'NEW', NULL, 'YEARLY', '2023-03-13'), -- 수정: LUMP_SUM → YEARLY
    (7, 7, 'LEASE', '하나캐피탈', 720000.00, TRUE, '2023-09-02', '2026-09-01', 'NEW', 2, 'MONTHLY', '2023-10-02'),
    (8, 9, 'RENT', '롯데렌터카', 950000.00, TRUE, '2022-07-08', '2025-07-07', 'NEW', 20, 'MONTHLY', '2022-08-08');


-- 6. 리스 상세 정보 (tbl_lease_detail)
INSERT INTO `tbl_lease_detail`
(`id`, `monthly_lease`, `lease_period_months`, `residual_value`, `buyout_option_price`, `mileage_limit`, `excess_mileage_rate`, `type`)
VALUES
    (1, 650000.00, 36, 15000000.00, 16000000.00, 20000, 100.00, 'OPERATING_LEASE'),
    (5, 580000.00, 24, 18000000.00, 18500000.00, 20000, 100.00, 'OPERATING_LEASE'),
    (7, 720000.00, 48, 25000000.00, 26500000.00, 25000, 150.00, 'FINANCE_LEASE');

-- 7. 렌트 상세 정보 (tbl_rent_detail)
INSERT INTO `tbl_rent_detail`
(`id`, `monthly_rent`, `deposit`, `auto_renewal`, `type`)
VALUES
    (2, 850000.00, 5000000.00, TRUE, 'LONG_RENT'),
    (4, 450000.00, 2000000.00, FALSE, 'LONG_RENT'),
    (8, 950000.00, 10000000.00, TRUE, 'LONG_RENT');

-- 8. 구매 상세 정보 (tbl_purchase_detail)
INSERT INTO `tbl_purchase_detail`
(`id`, `purchase_price`, `down_payment`, `loan_amount`, `interest_rate`, `loan_term_months`, `monthly_payment`)
VALUES
    (3, 65000000.00, 20000000.00, 45000000.00, 5.2, 36, 1350000.00),
    (6, 38000000.00, 38000000.00, 0.00, 0.0, 0, 0.00);

-- 9. 보험 이력 (tbl_insurance)
INSERT INTO `tbl_insurance`
(`id`, `car_id`, `start_date`, `end_date`, `premium`, `company`, `name`, `status`, `payment_type`, `payment_day`, `first_payment_day`)
VALUES
    (1, 1, '2024-10-02', '2025-10-01', 1200000.00, '삼성화재 다이렉트', '업무용 자동차보험', 'RENEWED', 'MONTHLY', 2, '2024-11-02'),
    (2, 2, '2024-11-16', '2025-11-15', 1800000.00, '현대해상', '업무용 자동차보험', 'RENEWED', 'MONTHLY', 16, '2024-12-16'),
    (3, 3, '2024-08-21', '2025-08-20', 2200000.00, 'DB손해보험', '업무용 자동차보험', 'NEW', 'LUMP_SUM', NULL, '2024-08-21'),
    (4, 4, '2024-07-01', '2025-06-30', 950000.00, 'KB손해보험', '업무용 자동차보험', 'RENEWED', 'MONTHLY', 1, '2024-08-01');

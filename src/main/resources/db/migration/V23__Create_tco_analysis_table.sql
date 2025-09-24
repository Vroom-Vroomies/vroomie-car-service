-- V23__Create_tco_analysis_table.sql
-- TCO(총 소유비용) 분석을 위한 테이블 생성

CREATE TABLE tbl_tco_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL COMMENT '회사 ID',
    vehicle_id VARCHAR(20) COMMENT '차량 ID (특정 차량 분석시 사용, null이면 전체 회사 분석)',
    analysis_period ENUM('MONTH', 'YEAR') NOT NULL COMMENT '분석 기간 유형',
    year INT NOT NULL COMMENT '분석 연도',
    month INT COMMENT '분석 월 (월별 분석시에만 사용)',
    acquisition_cost DECIMAL(15,2) DEFAULT 0 COMMENT '취득 비용 (구매/리스/렌트 비용)',
    operation_cost DECIMAL(15,2) DEFAULT 0 COMMENT '운영 비용 (연료, 주차, 통행료 등)',
    maintenance_cost DECIMAL(15,2) DEFAULT 0 COMMENT '유지보수 비용 (정비, 수리, 세차 등)',
    disposal_cost DECIMAL(15,2) DEFAULT 0 COMMENT '처분 비용 - TODO: 향후 외부 시세 API 연동시 감가상각 계산 추가',
    total_cost DECIMAL(15,2) DEFAULT 0 COMMENT '총 비용 (모든 비용의 합계)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    -- 인덱스 생성
    INDEX idx_tco_company_period (company_id, analysis_period, year, month),
    INDEX idx_tco_vehicle (vehicle_id, year, month),
    INDEX idx_tco_total_cost (total_cost DESC)
);
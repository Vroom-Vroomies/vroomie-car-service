-- V24__Create_vehicle_efficiency_table.sql
-- 차량 효율성 메트릭 분석을 위한 테이블 생성

CREATE TABLE tbl_vehicle_efficiency (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL COMMENT '회사 ID',
    vehicle_id VARCHAR(20) NOT NULL COMMENT '차량 ID',
    analysis_period ENUM('MONTH', 'YEAR') NOT NULL COMMENT '분석 기간 유형',
    year INT NOT NULL COMMENT '분석 연도',
    month INT COMMENT '분석 월 (월별 분석시에만 사용)',
    inefficiency_score DECIMAL(5,2) DEFAULT 0 COMMENT '비효율성 점수 (0-100점)',
    total_cost DECIMAL(15,2) DEFAULT 0 COMMENT '총 비용',
    total_distance DECIMAL(10,2) DEFAULT 0 COMMENT '총 주행거리 (km)',
    cost_per_km DECIMAL(8,2) DEFAULT 0 COMMENT 'km당 비용',
    usage_days INT DEFAULT 0 COMMENT '사용 일수',
    fuel_efficiency DECIMAL(8,2) DEFAULT 0 COMMENT '연비 효율성 (km/L)',
    maintenance_frequency INT DEFAULT 0 COMMENT '유지보수 발생 빈도',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',

    -- 인덱스 생성
    INDEX idx_efficiency_company_period (company_id, analysis_period, year, month),
    INDEX idx_efficiency_vehicle (vehicle_id, year, month),
    INDEX idx_efficiency_score (inefficiency_score DESC),
    INDEX idx_efficiency_cost_per_km (cost_per_km DESC)
);
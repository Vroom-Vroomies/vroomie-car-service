package com.vroomie.car_service.domain.fleet.log.repository;

import com.vroomie.car_service.domain.fleet.log.dto.CarLogListResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CarLogRepositoryImpl implements CarLogRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Page<CarLogListResponseDTO> getCarLogByCarId(Long carId, List<String> logTypes, Pageable pageable) {
        // 동적으로 UNION ALL 쿼리 생성
        List<String> unionParts = new ArrayList<>();
        if (logTypes.isEmpty() || logTypes.contains("ACCIDENT")) {
            unionParts.add(getAccidentQuery());
        }
        if (logTypes.isEmpty() || logTypes.contains("REPAIR")) {
            unionParts.add(getRepairQuery());
        }
        if (logTypes.isEmpty() || logTypes.contains("INSPECTION")) {
            unionParts.add(getInspectionQuery());
        }

        String unionQuery = String.join(" UNION ALL ", unionParts);

        // count Query 생성
        String countQueryStr = "SELECT COUNT(*) FROM (" + unionQuery + ") AS car_logs";
        Query countQuery = em.createNativeQuery(countQueryStr);
        countQuery.setParameter("carId", carId);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        // 데이터 조회 쿼리
        String dataQueryStr = "SELECT * FROM (" + unionQuery + ") AS car_logs ORDER BY date DESC";
        Query dataQuery = em.createNativeQuery(dataQueryStr);
        dataQuery.setParameter("carId", carId);
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        // generic 규칙 무시
        @SuppressWarnings("unchecked")
        List<Object[]> results = dataQuery.getResultList();

        // DTO 변환
        List<CarLogListResponseDTO> content = results.stream()
                .map(CarLogListResponseDTO::new)
                .collect(Collectors.toList());

        // Page 객체로 반환
        return new PageImpl<>(content, pageable, total);
    }

    private String getAccidentQuery() {
        return """
            SELECT
                a.id AS LogId,
                'ACCIDENT' AS logType,
                a.occurred_at AS date,
                a.note AS description,
                e.name AS handler,
                CASE WHEN a.is_saved = TRUE THEN '처리완료' ELSE '처리중' END AS status,
                a.cost AS cost
            FROM tbl_accident a
            LEFT JOIN tbl_employee e ON a.emp_email = e.email
            WHERE a.car_id = :carId
        """;
    }

    private String getRepairQuery() {
        return """
            SELECT
                r.id AS LogId,
                'REPAIR' AS logType,
                r.started_at AS date,
                r.detail AS description,
                e.name AS handler,
                r.status AS status,
                r.cost AS cost
            FROM tbl_repair r
            LEFT JOIN tbl_employee e ON r.emp_email = e.email
            WHERE r.car_id = :carId
        """;
    }

    private String getInspectionQuery() {
        return """
            SELECT
                i.id AS LogId,
                'INSPECTION' AS logType,
                i.date AS date,
                i.inspection_type AS description,
                i.inspector_name AS handler,
                i.final_result AS status,
                NULL AS cost
            FROM tbl_inspection i
            WHERE i.car_id = :carId
        """;
    }
}

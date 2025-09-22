package com.vroomie.car_service.domain.fleet.car.repository.specification;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.repair.entity.RepairEntity;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

// TODO: 2025-09-23 seoeungi
//  - QueryDSL과 비교 및 JPA, QueryDSL 장단점, 효율성 검토
public class CarAndRepairSpecification {
    public static Specification<CarEntity> hasCompanyId(Long companyId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("companyId"), companyId);
    }

    public static Specification<CarEntity> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<CarEntity> insuExpirationAfter(LocalDate date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("insuExpiration"), date);
    }
    
    public static Specification<CarEntity> isInRepair() {
        return (root, query, criteriaBuilder) -> {
            if (query == null) {
                // query는 not null
                throw new IllegalArgumentException("Cannot Specification query. cause parameter query is null");
            }

            // 서브쿼리
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<RepairEntity> repairRoot = subquery.from(RepairEntity.class);
            subquery.select(criteriaBuilder.literal(1L));
            
            // 서브쿼리 WHERE
            Predicate carMatch = criteriaBuilder.equal(repairRoot.get("car"), root);
            Predicate statusMatch = criteriaBuilder.equal(repairRoot.get("status"), "IN_REPAIR");
            subquery.where(carMatch, statusMatch);
            
            // EXISTS
            return criteriaBuilder.exists(subquery);
        };
    }
}
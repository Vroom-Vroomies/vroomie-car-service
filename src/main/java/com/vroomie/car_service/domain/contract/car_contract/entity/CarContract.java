package com.vroomie.car_service.domain.contract.car_contract.entity;

import com.vroomie.car_service.domain.contract.car_contract.enums.ContractStatus;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractType;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Slf4j
@Entity
@Table(name = "tbl_car_contract")
// Inheritance(stratege : InheritanceType.JOINED)
// 상속 매핑 전략(JOINED) : 부모 테이블(tbl_car_contracts)과 자식 테이블들(tbl_lease_details 외 2개)을 각각 두고, 조인하여 데이터를 관리
// 즉, CarContract는 추상 클래스이고, 구체적인 계약은 자식 엔티티에서 만들어져 join 되는 구조
@Inheritance(strategy = InheritanceType.JOINED)
// DiscriminatorColumn(name = "contract_type")
// 상속 매핑 시 어떤 자식 엔티티인지 구분하기 위한 컬럼을 지정
// DB에 contract_type 컬럼이 생성되어 저장될 때 "이 레코드가 어느 자식 클래스인지" 판별 가능
@DiscriminatorColumn(
        name = "contract_type",
        discriminatorType = DiscriminatorType.STRING,
        columnDefinition = "ENUM('RENT', 'LEASE', 'PURCHASE')"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public abstract class CarContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    private CarEntity car;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", insertable = false, updatable = false)
    private ContractType contractType;

    private String provider;
    @Column(name = "monthly_fee")
    private BigDecimal monthlyFee;   // 월 고정 비용

    @Column(name = "insurance_included")
    private Boolean isInsured;  // 보험 포함 여부
    @Column(name = "start_at")
    private Date startAt;
    @Column(name = "end_at")
    private Date endAt;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(name = "renewal_date")
    private Date renewalDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContractStatus contractStatus;


}

package com.vroomie.car_service.domain.fleet.car.entity;

import com.vroomie.car_service.domain.fleet.car.enums.CarFuelType;
import com.vroomie.car_service.domain.fleet.car.enums.CarGearType;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import com.vroomie.car_service.domain.fleet.car.enums.CarUsageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_car")
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    /* entity 생성되면 아래 코드로 수정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
     */
    private String identification;
    private String number;
    private String image;
    private String model;
    private String type;
    private Long totalMileage;
    private String color;
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    private LocalDate insuExpiration;
    private LocalDate lastInspection;
    private Integer inspectionCycle;
    private Integer allowableCapacity;

    @Enumerated(EnumType.STRING)
    private CarFuelType fuelType;

    @Enumerated(EnumType.STRING)
    private CarUsageType usageType;

    @Enumerated(EnumType.STRING)
    private CarGearType gearType;

    @Builder
    public CarEntity(Long companyId, String identification, String number, String image, String model, String type, Long totalMileage, String color, Integer year,
               CarStatus status, LocalDate insuExpiration, LocalDate lastInspection, Integer inspectionCycle, Integer allowableCapacity, CarFuelType fuelType,
               CarUsageType usageType, CarGearType gearType) {
        this.companyId = companyId;
        this.identification = identification;
        this.number = number;
        this.image = image;
        this.model = model;
        this.type = type;
        this.totalMileage = totalMileage;
        this.color = color;
        this.year = year;
        this.status = status;
        this.insuExpiration = insuExpiration;
        this.lastInspection = lastInspection;
        this.inspectionCycle = inspectionCycle;
        this.allowableCapacity = allowableCapacity;
        this.fuelType = fuelType;
        this.usageType = usageType;
        this.gearType = gearType;
    }

    // =================================================================
    // 비즈니스 로직을 담은 메소드
    // =================================================================

    /**
     * 운행 종료 후 최종 주행거리 기록
     * @param finalMileage 최종 주행거리
     */
    public void recordEndOfDriving(Long finalMileage) {

        if (finalMileage < this.totalMileage) {
            throw new IllegalArgumentException("최종 주행거리는 기존 주행거리보다 작을 수 없습니다.");
        }
        this.totalMileage = finalMileage;
    }

    /**
     * 보험 정보를 갱신
     * @param newExpirationDate 새로운 보험 만료일
     */
    public void renewInsurance(LocalDate newExpirationDate) {

        this.insuExpiration = newExpirationDate;
    }

    /**
     * 차량의 상태 변경 (예: 매각, 폐차 처리)
     * @param newStatus 새로운 차량 상태
     */
    public void changeStatus(CarStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * 차량 기본 정보 업데이트
     */
    // 차량 정보 업데이트 메서드
    public void update(String image, String model, Long totalMileage, String color, CarStatus status,
                       LocalDate insuExpiration, LocalDate lastInspection, Integer inspectionCycle) {
        if (image != null) this.image = image;
        if (model != null) this.model = model;
        if (totalMileage != null) this.totalMileage = totalMileage;
        if (color != null) this.color = color;
        if (status != null) this.status = status;
        if (insuExpiration != null) this.insuExpiration = insuExpiration;
        if (lastInspection != null) this.lastInspection = lastInspection;
        if (inspectionCycle != null) this.inspectionCycle = inspectionCycle;
    }

    public void updateLastInspection(LocalDateTime date){
        if (date != null) this.lastInspection = date.toLocalDate();
    }
}
package com.vroomie.car_service.domain.fleet.car.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_car")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private String identification;
    private String number;
    private String image;
    private String model;
    private String type;
    private Long totalMileage;
    private String color;
    private int year;
    private String status;
    private LocalDate insuExpiration;
    private LocalDate lastInspection;
    private Integer inspectionCycle;
    private Integer allowableCapacity;
    private String fuelType;
    private String usageType;
    private String gearType;

    @Builder
    public CarEntity(Long companyId, String identification, String number, String image, String model, String type, Long totalMileage, String color, int year, String status, LocalDate insuExpiration, LocalDate lastInspection, Integer inspectionCycle, Integer allowableCapacity, String fuelType, String usageType, String gearType) {
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

    public void update(String model, Long totalMileage, String color, String status) {

        // 모든 정보 수정 가능하게? API로 받아오면 필드 값 어떻게 구성할 지

    }
}
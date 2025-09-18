package com.vroomie.car_service.domain.fleet.repair.entity;

import com.vroomie.car_service.domain.fleet.repair.enums.RepairImageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_repair_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepairImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id", nullable = false)
    private RepairEntity repair;

    @Enumerated(EnumType.STRING)
    private RepairImageType type;

    private String image;

    @Builder
    public RepairImageEntity(RepairEntity repair, RepairImageType type, String image) {
        this.repair = repair;
        this.type = type;
        this.image = image;
    }

    public void setRepair(RepairEntity repair) {
        this.repair = repair;
    }
}
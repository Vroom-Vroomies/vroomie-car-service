package com.vroomie.car_service.domain.fleet.accident.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_accident_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccidentImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accident_id", nullable = false)
    private AccidentEntity accident;

    private String image;

    @Builder
    public AccidentImageEntity(AccidentEntity accident, String image) {
        this.accident = accident;
        this.image = image;
    }
}

package com.vroomie.car_service.domain.contract.actual_cost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "tbl_cost_type")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContractCostType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cost_type_name")
    private String name;

    private String description;
}

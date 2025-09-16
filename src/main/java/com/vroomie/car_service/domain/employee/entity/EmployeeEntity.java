package com.vroomie.car_service.domain.employee.entity;

import com.vroomie.car_service.domain.employee.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="tbl_employee")
public class EmployeeEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String email;
    private String name;
    private String phone;
    private String department;
    private boolean isDeleted;
    private boolean licenceStatus;
    private String licenceImage;

    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isActive;

}

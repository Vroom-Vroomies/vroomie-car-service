package com.vroomie.car_service.domain.fleet.drivinglog.dto;

import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogSubmitResDTO {
    private Long id;
    private String empEmail;
    private Long carId;
    private LogStatus logStatus;
}

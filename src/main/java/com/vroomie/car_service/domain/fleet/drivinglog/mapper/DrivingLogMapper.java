package com.vroomie.car_service.domain.fleet.drivinglog.mapper;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogDetailResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogEndResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogStartReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogStartResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogSubmitResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogSummaryResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.entity.DrivingLogEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DrivingLogMapper {

    // StartReqDTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeEntity", source = "employeeEntity")
    @Mapping(target = "carEntity", source = "carEntity")
    @Mapping(target = "startedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "endOdometer", ignore = true)
    @Mapping(target = "endOdometerImage", ignore = true)
    @Mapping(target = "endedAt", ignore = true)
    @Mapping(target = "endLat", ignore = true)
    @Mapping(target = "endLng", ignore = true)
    @Mapping(target = "endLocation", ignore = true)
    @Mapping(target = "gpsDistance", ignore = true)
    @Mapping(target = "odometerDistance", ignore = true)
    @Mapping(target = "logStatus", expression = "java(logStatus)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isSaved", constant = "false")
    DrivingLogEntity toDrivingLogEntity(
            DrivingLogStartReqDTO drivingLogStartReqDTO,
            EmployeeEntity employeeEntity,
            CarEntity carEntity,
            LogStatus logStatus
    );

    // Entity -> StartResDTO
    @Mapping(target = "empEmail", source = "employeeEntity.email")
    DrivingLogStartResDTO toDrivingLogStartResDTO(DrivingLogEntity drivingLogEntity);

    // Entity -> EndResDTO
    @Mapping(target = "empEmail", source = "employeeEntity.email")
    DrivingLogEndResDTO toDrivingLogEndResDTO(DrivingLogEntity drivingLogEntity);

    // Entity -> ResDTO
    @Mapping(target = "empEmail", source = "employeeEntity.email")
    DrivingLogResDTO toDrivingLogResDTO(DrivingLogEntity entity);

    // Entity -> SubmitResDTO
    @Mapping(target = "empEmail", source = "employeeEntity.email")
    @Mapping(target = "carId", source = "carEntity.id")
    DrivingLogSubmitResDTO toDrivingLogSubmitResDTO(DrivingLogEntity drivingLogEntity);

    // Entity -> SummaryResDTO
    @Mapping(target = "empEmail", source = "employeeEntity.email")
    @Mapping(target = "carId", source = "carEntity.id")
    DrivingLogSummaryResDTO toDrivingLogSummaryResDTO(DrivingLogEntity drivingLogEntity);

    // Entity -> DetailResDTO
    @Mapping(target = "empEmail", source = "employeeEntity.email")
    @Mapping(target = "carId", source = "carEntity.id")
    DrivingLogDetailResDTO toDrivingLogDetailResDTO(DrivingLogEntity drivingLogEntity);

}

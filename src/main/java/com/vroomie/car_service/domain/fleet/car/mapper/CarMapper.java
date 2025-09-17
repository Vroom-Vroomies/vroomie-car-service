package com.vroomie.car_service.domain.fleet.car.mapper;

import com.vroomie.car_service.domain.fleet.car.dto.response.CarDetailResponse;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarSimpleResponse;
import com.vroomie.car_service.domain.fleet.car.dto.response.StatusAlertResponse;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarDetailResponse toDetailResponse(CarEntity car);

    @Mapping(target = "statusAlert", expression = "java(toStatusAlert(isContractMissing, isInsuranceMissing))")
    @Mapping(source = "car.usageType", target = "usageType")
    @Mapping(source = "isRented", target = "isRented")
    CarSimpleResponse toSimpleResponse(CarEntity car, boolean isContractMissing, boolean isInsuranceMissing, boolean isRented);

    default StatusAlertResponse toStatusAlert(boolean contractMissing, boolean insuranceMissing) {
        return StatusAlertResponse.builder()
                .contract(contractMissing)
                .insurance(insuranceMissing)
                .build();
    }
}


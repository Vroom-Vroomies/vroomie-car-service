package com.vroomie.car_service.domain.fleet.car.mapper;

import com.vroomie.car_service.domain.fleet.car.dto.request.CarRegistRequest;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarDetailResponse;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarSimpleResponse;
import com.vroomie.car_service.domain.fleet.car.dto.response.StatusAlertResponse;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarDetailResponse toDetailResponse(CarEntity car);

    CarEntity toEntity(CarRegistRequest request);

    @Mapping(target = "statusAlert", expression = "java(toStatusAlert(isContractMissing, isInsuranceMissing))")
    @Mapping(source = "car.usageType", target = "usageType")
    @Mapping(source = "isRented", target = "isRented")
    CarSimpleResponse toSimpleResponse(CarEntity car, boolean isContractMissing, boolean isInsuranceMissing, boolean isRented, boolean isRepairing);

    @Mapping(target = "statusAlert", expression = "java(toStatusAlert(isContractMissing, isInsuranceMissing))")
    @Mapping(source = "car.usageType", target = "usageType")
    @Mapping(source = "isRented", target = "isRented")
    List<CarSimpleResponse> toSimpleResponseList(List<CarEntity> carList);

    default StatusAlertResponse toStatusAlert(boolean contractMissing, boolean insuranceMissing) {
        return StatusAlertResponse.builder()
                .contract(contractMissing)
                .insurance(insuranceMissing)
                .build();
    }

}


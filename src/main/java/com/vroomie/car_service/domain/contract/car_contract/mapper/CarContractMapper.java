package com.vroomie.car_service.domain.contract.car_contract.mapper;

import com.vroomie.car_service.domain.contract.car_contract.dto.response.LeaseContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.PurchaseContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.RentContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.entity.LeaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.PurchaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.RentContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarContractMapper {

    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(source = "car.model", target = "carModel")
    LeaseContractResponse toLeaseContractResponse(LeaseContract entity);

    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(source = "car.model", target = "carModel")
    RentContractResponse toRentContractResponse(RentContract entity);

    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(source = "car.model", target = "carModel")
    PurchaseContractResponse toPurchaseContractResponse(PurchaseContract
                                                                entity);
}
package com.vroomie.car_service.domain.contract.car_contract.mapper;

import com.vroomie.car_service.domain.contract.car_contract.dto.request.LeaseRegistRequest;
import com.vroomie.car_service.domain.contract.car_contract.dto.request.PurchaseRegistRequest;
import com.vroomie.car_service.domain.contract.car_contract.dto.request.RentRegistRequest;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.LeaseContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.PurchaseContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.RentContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.entity.LeaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.PurchaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.RentContract;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CarContractMapper {

    @Autowired
    protected CarRepository carRepository;

    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(source = "car.model", target = "carModel")
    public abstract LeaseContractResponse toLeaseContractResponse(LeaseContract entity);

    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(source = "car.model", target = "carModel")
    public abstract RentContractResponse toRentContractResponse(RentContract entity);

    @Mapping(source = "id", target = "contractId")
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(source = "car.model", target = "carModel")
    public abstract PurchaseContractResponse toPurchaseContractResponse(PurchaseContract entity);

    @Mapping(source = "carId", target = "car")
    @Mapping(target = "id", ignore = true)
    public abstract LeaseContract toLeaseContract(LeaseRegistRequest dto);

    @Mapping(source = "carId", target = "car")
    @Mapping(target = "id", ignore = true)
    public abstract RentContract toRentContract(RentRegistRequest dto);

    @Mapping(source = "carId", target = "car")
    @Mapping(target = "id", ignore = true)
    public abstract PurchaseContract toPurchaseContract(PurchaseRegistRequest dto);

    protected CarEntity mapCarIdToCar(Long carId) {
        if (carId == null) {
            return null;
        }
        return carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("차량을 찾을 수 없습니다. carId: " + carId));
    }
}

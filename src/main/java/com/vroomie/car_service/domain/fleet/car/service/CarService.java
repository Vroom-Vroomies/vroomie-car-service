package com.vroomie.car_service.domain.fleet.car.service;

import com.vroomie.car_service.domain.contract.car_contract.repository.CarContractRepository;
import com.vroomie.car_service.domain.contract.insurance_contract.repository.InsuContractRepository;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarListResponse;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarSimpleResponse;
import com.vroomie.car_service.domain.fleet.car.dto.response.PageInfoResponse;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import com.vroomie.car_service.domain.fleet.car.enums.CarUsageType;
import com.vroomie.car_service.domain.fleet.car.mapper.CarMapper;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.domain.operation.reservation.repository.ReservedLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarContractRepository carContractRepository;
    private final InsuContractRepository insuContractRepository;
    private final ReservedLogRepository reservedLogRepository;
    private final CarMapper carMapper;

    @Transactional(readOnly = true)
    public CarListResponse fetchCarList(Pageable pageable, String status, String usageType) {

        CarStatus statusEnum = (status != null) ? CarStatus.valueOf(status.toUpperCase()) : null;
        CarUsageType usageTypeEnum = (usageType != null) ? CarUsageType.valueOf(usageType.toUpperCase()) : null;

        Page<CarEntity> carPage;

        if (statusEnum != null && usageTypeEnum != null) {
            carPage = carRepository.findAllByStatusAndUsageType(statusEnum, usageTypeEnum, pageable);
        } else if (statusEnum != null) {
            carPage = carRepository.findAllByStatus(statusEnum, pageable);
        } else if (usageTypeEnum != null) {
            carPage = carRepository.findAllByUsageType(usageTypeEnum, pageable);
        } else {
            carPage = carRepository.findAll(pageable);
        }

        List<Long> carIds = carPage.getContent().stream().map(CarEntity::getId).toList();

        Set<Long> contractCarIds = carContractRepository.findCarIdsWithContractIn(carIds);
        Set<Long> insuranceCarIds = insuContractRepository.findCarIdsWithInsuranceIn(carIds);
        Set<Long> rentedCarIds = reservedLogRepository.findRentedCarIdsIn(carIds);

        Page<CarSimpleResponse> carDtos = carPage.map(car -> {
            boolean isContractMissing = !contractCarIds.contains(car.getId());
            boolean isInsuranceMissing = !insuranceCarIds.contains(car.getId());
            boolean isRented = rentedCarIds.contains(car.getId());

            return carMapper.toSimpleResponse(car, isContractMissing, isInsuranceMissing, isRented);
        });

        return CarListResponse.builder()
                .cars(carDtos.getContent())
                .pageInfo(PageInfoResponse.builder()
                        .page(carPage.getNumber())
                        .size(carPage.getSize())
                        .totalElements(carPage.getTotalElements())
                        .totalPages(carPage.getTotalPages())
                        .build())
                .build();
    }
}


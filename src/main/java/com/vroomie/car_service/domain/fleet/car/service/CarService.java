package com.vroomie.car_service.domain.fleet.car.service;

import com.vroomie.car_service.domain.contract.car_contract.repository.CarContractRepository;
import com.vroomie.car_service.domain.contract.insurance_contract.repository.InsuContractRepository;
import com.vroomie.car_service.domain.fleet.car.dto.request.CarRegistRequest;
import com.vroomie.car_service.domain.fleet.car.dto.request.CarUpdateRequest;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarDetailResponse;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarSimpleResponse;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import com.vroomie.car_service.domain.fleet.car.enums.CarUsageType;
import com.vroomie.car_service.domain.fleet.car.exceptions.CarException;
import com.vroomie.car_service.domain.fleet.car.mapper.CarMapper;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.domain.operation.reservation.repository.ReservedLogRepository;
import com.vroomie.car_service.global.response.PageResponse;
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
    public PageResponse<CarSimpleResponse> fetchCarList(Pageable pageable, String status, String usageType) {

        CarStatus statusEnum = null;
        CarUsageType usageTypeEnum = null;

        try {
            if (status != null && !status.isBlank()) {
                statusEnum = CarStatus.valueOf(status.toUpperCase());
            }
            if (usageType != null && !usageType.isBlank()) {
                usageTypeEnum = CarUsageType.valueOf(usageType.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            throw CarException.invalidSearchParameterException();
        }

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

        return PageResponse.of(carDtos);
    }

    @Transactional(readOnly = true)
    public CarDetailResponse fetchCarDetail(Long carId) {

        CarEntity carEntity = carRepository.findById(carId)
                .orElseThrow(CarException::carNotFoundException);

        return carMapper.toDetailResponse(carEntity);
    }

    @Transactional
    public CarDetailResponse createCar(CarRegistRequest request) {

        if (carRepository.existsByNumber(request.getNumber())) {
            throw CarException.carAlreadyExistsException();
        }

        CarEntity newCar = carMapper.toEntity(request);
        CarEntity savedCar = carRepository.save(newCar);

        return carMapper.toDetailResponse(savedCar);
    }

    @Transactional
    public CarDetailResponse updateCar(Long carId, CarUpdateRequest request) {

        CarEntity carEntity = carRepository.findById(carId)
                .orElseThrow(CarException::carNotFoundException);

        carEntity.update(
                request.getImage(),
                request.getModel(),
                request.getTotalMileage(),
                request.getColor(),
                request.getStatus(),
                request.getInsuExpiration(),
                request.getLastInspection(),
                request.getInspectionCycle()
        );

        return carMapper.toDetailResponse(carEntity);
    }
}


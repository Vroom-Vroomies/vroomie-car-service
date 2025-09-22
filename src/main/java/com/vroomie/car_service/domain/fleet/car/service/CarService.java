package com.vroomie.car_service.domain.fleet.car.service;

import com.google.zxing.WriterException;
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
import com.vroomie.car_service.domain.fleet.car.repository.specification.CarAndRepairSpecification;
import com.vroomie.car_service.domain.fleet.drivinglog.entity.DrivingLogEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import com.vroomie.car_service.domain.fleet.drivinglog.repository.DrivingLogRepository;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import com.vroomie.car_service.domain.operation.reservation.repository.ReservedLogRepository;
import com.vroomie.car_service.global.response.PageResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarService {

    @Value("${app.backend.base-url}")
    private String backendBaseUrl;

    private final QrCodeService qrCodeService;
    private final CarRepository carRepository;
    private final CarContractRepository carContractRepository;
    private final InsuContractRepository insuContractRepository;
    private final ReservedLogRepository reservedLogRepository;
    private final DrivingLogRepository drivingLogRepository;
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

    public byte[] generateQrCodeForCar(Long carId) {
        carRepository.findById(carId)
                .orElseThrow(CarException::carNotFoundException);

        String qrUrl = backendBaseUrl + "/fleet/cars/" + carId + "/action";

        try {
            return qrCodeService.generateQrCode(qrUrl, 200, 200);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("QR 코드 생성에 실패했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public String determineRedirectUrl(Long carId) {

        if (!carRepository.existsById(carId)) {
            throw CarException.carNotFoundException();
        }

        String currentUserEmail = "user01@wemade.com";

        // 차량 대여 상태 확인
        boolean isRented = reservedLogRepository.existsByCarIdAndStatusIn(carId, List.of(RentStatus.RENTED, RentStatus.OVERDUE));

        // TODO: 사용자 운행일지 등록 화면에 따라 반환값 수정 예정
        if (!isRented) {
            // 대여 중이 아니면 예약 페이지로 리다이렉트
            return "/member/reservation/" + carId;
        }

        // 대여 중이라면 운행 기록 상태 확인
        Optional<DrivingLogEntity> drivingLogOpt = drivingLogRepository
                .findTopByCarEntity_IdAndEmployeeEntity_EmailAndLogStatusOrderByCreatedAtDesc(carId, currentUserEmail, LogStatus.WRITING);

        if (drivingLogOpt.isPresent()) {
            // 작성 중인 기록이 있으면 운행 종료 페이지로 리다이렉트
            Long drivingLogId = drivingLogOpt.get().getId();
            return "/member/reservation/" + carId;
        } else {
            // 작성 중인 기록이 없으면 운행 시작 페이지로
            return "/member/reservation/" + carId;
        }
    }

    // seoeungi 추가
    // 동일쿼리라면 status 또한 파라미터 처리
    public long countInRepairCars(Long companyId) {
        Specification<CarEntity> spec = CarAndRepairSpecification.hasCompanyId(companyId)
            .and(CarAndRepairSpecification.hasStatus("ACTIVE"))
            .and(CarAndRepairSpecification.insuExpirationAfter(LocalDate.now()))
            .and(CarAndRepairSpecification.isInRepair());

        return carRepository.count(spec);
    }
}

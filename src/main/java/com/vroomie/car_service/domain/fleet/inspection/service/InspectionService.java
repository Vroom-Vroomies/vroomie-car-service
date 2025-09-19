package com.vroomie.car_service.domain.fleet.inspection.service;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.employee.repository.EmployeeRepository;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.domain.fleet.inspection.dto.InspectionCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.inspection.dto.InspectionDetailDTO;
import com.vroomie.car_service.domain.fleet.inspection.entity.InspectionEntity;
import com.vroomie.car_service.domain.fleet.inspection.mapper.InspectionMapper;
import com.vroomie.car_service.domain.fleet.inspection.respository.InspectionRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final CarRepository carRepository;
    private final EmployeeRepository employeeRepository;
    private final InspectionMapper inspectionMapper;

    @Transactional
    public InspectionDetailDTO createInspection(Long carId, InspectionCreateRequestDTO req) {
        CarEntity car = carRepository.findById(carId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));
        EmployeeEntity employee = employeeRepository.findByEmail(req.getCreatorEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        InspectionEntity inspection = InspectionEntity.builder()
                .car(car)
                .date(req.getDate())
                .centerName(req.getCenterName())
                .centerLocation(req.getCenterLocation())
                .inspectorName(req.getInspectorName())
                .inspectionType(req.getInspectionType())
                .finalResult(req.getFinalResult())
                .validUntil(req.getValidUntil())
                .failureReason(req.getFailureReason())
                .result(req.getResult())
                .remarks(req.getRemarks())
                .employee(employee)
                .build();

        // 차량 최종 점검일 업데이트
        car.updateLastInspection(req.getDate());
        carRepository.save(car);

        return inspectionMapper.toDetailDTO(inspectionRepository.save(inspection));
    }

    public InspectionDetailDTO getInspectionById(Long inspectionId) {
        InspectionEntity entity = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSPECTION_NOT_FOUND));

        return null;
    }
}

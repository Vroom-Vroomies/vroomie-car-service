package com.vroomie.car_service.domain.fleet.log.service;

import com.vroomie.car_service.domain.fleet.accident.repository.AccidentRepository;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.domain.fleet.inspection.respository.InspectionRepository;
import com.vroomie.car_service.domain.fleet.log.dto.CarLogListResponseDTO;
import com.vroomie.car_service.domain.fleet.log.projection.CarLogProjection;
import com.vroomie.car_service.domain.fleet.repair.repository.RepairRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarLogService {

    private final CarRepository carRepository;
    private final AccidentRepository accidentRepository;
    private final RepairRepository repairRepository;
    private final InspectionRepository inspectionRepository;

    public Page<CarLogListResponseDTO> getCarLogList(Long carId, String logType, Pageable pageable) {
        // 차량 존재 확인
        carRepository.findById(carId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));

        // 로그 타입 파싱
        List<String> logTypes = parseLogTypes(logType);

        // 각 타입별로 데이터 조회 후 병합
        List<CarLogProjection> allLogs = new ArrayList<>();

        if (shouldIncludeLogType("ACCIDENT", logTypes)) {
            Page<CarLogProjection> accidents = accidentRepository.findAccidentLogsByCarId(carId, Pageable.unpaged());
            allLogs.addAll(accidents.getContent());
        }

        if (shouldIncludeLogType("REPAIR", logTypes)) {
            Page<CarLogProjection> repairs = repairRepository.findRepairLogsByCarId(carId, Pageable.unpaged());
            allLogs.addAll(repairs.getContent());
        }

        if (shouldIncludeLogType("INSPECTION", logTypes)) {
            Page<CarLogProjection> inspections = inspectionRepository.findInspectionLogsByCarId(carId, Pageable.unpaged());
            allLogs.addAll(inspections.getContent());
        }

        // 날짜순 정렬
        allLogs.sort((log1, log2) -> {
            if (log1.getDate() == null && log2.getDate() == null) return 0;
            if (log1.getDate() == null) return 1;
            if (log2.getDate() == null) return -1;
            return log2.getDate().compareTo(log1.getDate());
        });

        // 페이징 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allLogs.size());

        List<CarLogProjection> pagedLogs = allLogs.subList(start, end);

        // DTO 변환
        List<CarLogListResponseDTO> content = pagedLogs.stream()
                .map(CarLogListResponseDTO::new)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, allLogs.size());
    }

    private List<String> parseLogTypes(String logType) {
        if (StringUtils.hasText(logType)) {
            return Arrays.asList(logType.toUpperCase().split(","));
        }
        return List.of(); // 빈 리스트면 전체 조회
    }

    private boolean shouldIncludeLogType(String targetType, List<String> requestedTypes) {
        return requestedTypes.isEmpty() || requestedTypes.contains(targetType);
    }
}
package com.vroomie.car_service.domain.fleet.log.service;

import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.domain.fleet.log.dto.CarLogListResponseDTO;
import com.vroomie.car_service.domain.fleet.log.repository.CarLogRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarLogService {

    private final CarLogRepository carLogRepository;
    private final CarRepository carRepository;

    public Page<CarLogListResponseDTO> getCarLogList(Long carId, String logType, Pageable pageable) {
        carRepository.findById(carId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));

        // 이력 타입 필터
        List<String> logTypes;
        if (StringUtils.hasText(logType)) {
            logTypes = Arrays.asList(logType.toUpperCase().split(","));
        } else {
            logTypes = Collections.emptyList(); // 비어있으면 전체 조회
        }

        return carLogRepository.getCarLogByCarId(carId, logTypes, pageable);
    }
}

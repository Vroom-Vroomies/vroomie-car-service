package com.vroomie.car_service.domain.fleet.log.repository;

import com.vroomie.car_service.domain.fleet.log.dto.CarLogListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CarLogRepository {
    Page<CarLogListResponseDTO> getCarLogByCarId(Long carId, List<String> logTypes, Pageable pageable);
}
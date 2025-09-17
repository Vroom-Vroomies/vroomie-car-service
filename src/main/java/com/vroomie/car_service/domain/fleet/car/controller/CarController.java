package com.vroomie.car_service.domain.fleet.car.controller;

import com.vroomie.car_service.domain.fleet.car.dto.response.CarListResponse;
import com.vroomie.car_service.domain.fleet.car.service.CarService;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarDetailResponse;
import com.vroomie.car_service.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fleet/cars")
public class CarController {

    private final CarService carService;

    @GetMapping
    public ResponseEntity<ApiResponse<CarListResponse>> getCarList(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String usageType) {

        CarListResponse response = carService.fetchCarList(pageable, status, usageType);
        return ResponseEntity.ok(ApiResponse.success(response, "차량 목록 조회가 성공적으로 완료되었습니다."));
    }


    @GetMapping("/{carId}")
    public ResponseEntity<ApiResponse<CarDetailResponse>> getCarById(@PathVariable Long carId) {

        CarDetailResponse response = carService.fetchCarDetail(carId);
        return ResponseEntity.ok(ApiResponse.success(response, "차량 상세 조회가 성공적으로 완료되었습니다."));
    }
}


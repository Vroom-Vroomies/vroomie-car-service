package com.vroomie.car_service.domain.fleet.log.controller;

import com.vroomie.car_service.domain.fleet.log.dto.CarLogListResponseDTO;
import com.vroomie.car_service.domain.fleet.log.service.CarLogService;
import com.vroomie.car_service.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fleet/cars")
@RequiredArgsConstructor
@Tag(name = "차량 점검 및 사고 이력", description = "차량의 사고, 수리, 점검 이력 통합 조회 API")
public class CarLogController {
    private final CarLogService carLogService;

    @GetMapping("/{carId}/logs")
    @Operation(summary = "차량 점검 및 사고 이력 조회", description = "특정 차량의 수리, 사고, 점검 이력을 통합하여 최신순으로 정렬된 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<CarLogListResponseDTO>>> getCarLogList(
            @PathVariable Long carId,
            @RequestParam(required = false) String logType,
            @PageableDefault(size = 10) Pageable pageable
    ){
        Page<CarLogListResponseDTO> data = carLogService.getCarLogList(carId, logType, pageable);

        return ResponseEntity.ok(ApiResponse.success(data, "차량 점검 및 사고 이력 조회에 성공했습니다."));
    }
}

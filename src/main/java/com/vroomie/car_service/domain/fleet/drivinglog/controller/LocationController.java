package com.vroomie.car_service.domain.fleet.drivinglog.controller;

import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.LocationReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.LocationResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.service.LocationService;
import com.vroomie.car_service.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fleet/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @Operation(summary = "좌표 -> 주소 변환", description = "GPS를 통해 받은 좌표를 주소로 변환합니다.")
    public ResponseEntity<ApiResponse<LocationResDTO>> getAddress(@RequestBody LocationReqDTO locationReqDTO) {
        LocationResDTO locationResDTO = locationService.getLocation(locationReqDTO);

        ApiResponse<LocationResDTO> apiResponse = ApiResponse.success(locationResDTO);

        return ResponseEntity.ok(apiResponse);
    }
}

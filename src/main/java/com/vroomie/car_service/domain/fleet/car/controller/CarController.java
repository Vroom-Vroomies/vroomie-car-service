package com.vroomie.car_service.domain.fleet.car.controller;

import com.vroomie.car_service.domain.fleet.car.dto.request.CarRegistRequest;
import com.vroomie.car_service.domain.fleet.car.dto.request.CarUpdateRequest;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarSimpleResponse;
import com.vroomie.car_service.domain.fleet.car.service.CarService;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarDetailResponse;
import com.vroomie.car_service.global.response.ApiResponse;
import com.vroomie.car_service.global.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fleet/cars")
public class CarController {

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    private final CarService carService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CarSimpleResponse>>> getCarList(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String usageType) {

        PageResponse<CarSimpleResponse> response = carService.fetchCarList(pageable, status, usageType);
        return ResponseEntity.ok(ApiResponse.success(response, "차량 목록 조회가 성공적으로 완료되었습니다."));
    }


    @GetMapping("/{carId}")
    public ResponseEntity<ApiResponse<CarDetailResponse>> getCarById(@PathVariable Long carId) {

        CarDetailResponse response = carService.fetchCarDetail(carId);
        return ResponseEntity.ok(ApiResponse.success(response, "차량 상세 조회가 성공적으로 완료되었습니다."));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CarDetailResponse>> registCar(
            @Valid @RequestBody CarRegistRequest request) {

        CarDetailResponse response = carService.createCar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "차량 등록이 성공적으로 완료되었습니다."));
    }

    @PatchMapping("/{carId}")
    public ResponseEntity<ApiResponse<CarDetailResponse>> updateCar(
            @PathVariable Long carId,
            @RequestBody CarUpdateRequest request) {

        CarDetailResponse response = carService.updateCar(carId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "차량 정보 수정이 성공적으로 완료되었습니다."));
    }

    @GetMapping("/{carId}/qr-code")
    public ResponseEntity<byte[]> getCarQrCode(@PathVariable Long carId) {

        byte[] qrCodeImage = carService.generateQrCodeForCar(carId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCodeImage.length);

        return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
    }

    // QR 코드 스캔 시 리다이렉트 처리
    @GetMapping("/{carId}/action")
    public ResponseEntity<Void> handleQrCodeAction(@PathVariable Long carId) {

        String redirectPath = carService.determineRedirectUrl(carId);
        String fullRedirectUrl = frontendBaseUrl + redirectPath;

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(fullRedirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}


package com.vroomie.car_service.domain.operation.reservation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationRequest;
import com.vroomie.car_service.domain.operation.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import com.vroomie.car_service.global.response.PageResponse;
import com.vroomie.car_service.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "대여 신청 관리")
public class ReservationController {

    private final ReservationService reservationService;

    // [관리자] 대여 신청 목록 조회
    @Operation(summary = "[관리자]대여 신청 목록 조회", description = "대여 신청 목록을 조회합니다.")
    @GetMapping("/admin")
    public ApiResponse<PageResponse<AdminReservationResponse>> getAdminReservationList(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        PageResponse<AdminReservationResponse> response = reservationService.getAdminReservationList(currentPage,
                size);
        return ApiResponse.success(response, "대여 신청 목록 조회 성공");
    }

    // [관리자] 대여 신청 상태 변경(승인 or 거절)
    @Operation(summary = "[관리자]대여 신청 상태 변경", description = "대여 신청 상태를 변경합니다.")
    @PutMapping("/admin/{id}")
    public ApiResponse<AdminReservationResponse> updateAdminReservationStatus(
            @PathVariable @Min(value = 1, message = "대여 신청 ID는 1 이상이어야 합니다.") Long id,
            @RequestBody AdminReservationRequest request) {
        return ApiResponse.success(reservationService.updateAdminReservationStatus(id, request), "대여 신청 상태 변경 성공");
    }



}

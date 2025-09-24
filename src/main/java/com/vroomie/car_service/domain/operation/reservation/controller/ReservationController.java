package com.vroomie.car_service.domain.operation.reservation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationRequest;
import com.vroomie.car_service.domain.operation.reservation.dto.member.AvailableCarListResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.member.MemberCarDetailResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.member.MemberCarReservationRequest;
import com.vroomie.car_service.domain.operation.reservation.dto.member.CurrentCarResponse;
import com.vroomie.car_service.domain.operation.reservation.service.ReservationService;
import com.vroomie.car_service.domain.operation.reservation.service.OverdueUpdateService;
import lombok.RequiredArgsConstructor;
import com.vroomie.car_service.global.response.PageResponse;
import com.vroomie.car_service.global.response.ApiResponse;
import com.vroomie.car_service.global.constants.PaginationConstants;

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
    private final OverdueUpdateService overdueUpdateService;

    // [관리자] 대여 신청 목록 조회
    @Operation(summary = "[관리자]대여 신청 목록 조회", description = "대여 신청 목록을 조회합니다.")
    @GetMapping("/admin")
    public ApiResponse<PageResponse<AdminReservationResponse>> getAdminReservationList(
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) @Min(value = PaginationConstants.MIN_PAGE_NUMBER, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) @Min(value = PaginationConstants.MIN_PAGE_SIZE, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = PaginationConstants.MAX_PAGE_SIZE, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        PageResponse<AdminReservationResponse> response = reservationService.getAdminReservationList(currentPage,
                size);
        return ApiResponse.success(response, "대여 신청 목록 조회 성공");
    }

    // [관리자] 차량별 대여 신청 목록 조회
    @Operation(summary = "[관리자]차량별 대여 신청 목록 조회", description = "차량별 대여 신청 목록을 조회합니다.")
    @GetMapping("/admin/cars/{carId}")
    public ApiResponse<PageResponse<AdminReservationResponse>> getAdminReservationListByCar(@PathVariable Long carId,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) @Min(value = PaginationConstants.MIN_PAGE_NUMBER, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) @Min(value = PaginationConstants.MIN_PAGE_SIZE, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = PaginationConstants.MAX_PAGE_SIZE, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        PageResponse<AdminReservationResponse> response = reservationService.getAdminReservationListByCar(carId,
                currentPage, size);
        return ApiResponse.success(response, "차량별 대여 신청 목록 조회 성공");
    }

    // [관리자] 대여 신청 상태 변경(승인 or 거절)
    @Operation(summary = "[관리자]대여 신청 상태 변경", description = "대여 신청 상태를 변경합니다.")
    @PutMapping("/admin/{id}")
    public ApiResponse<AdminReservationResponse> updateAdminReservationStatus(
            @PathVariable @Min(value = PaginationConstants.MIN_PAGE_NUMBER, message = "대여 신청 ID는 1 이상이어야 합니다.") Long id,
            @RequestBody AdminReservationRequest request) {

        return ApiResponse.success(reservationService.updateAdminReservationStatus(id, request), "대여 신청 상태 변경 성공");
    }

    // [사용자] 특정 시간대 대여 가능한 차량 목록 조회
    @Operation(summary = "[사용자]특정 시간대 대여 가능한 차량 목록 조회", description = "선택한 시간대에 대여 가능한 차량 목록을 조회합니다.")
    @GetMapping("/member/available-cars")
    public ApiResponse<PageResponse<AvailableCarListResponse>> getAvailableCarsByTimeSlot(
            @RequestParam String requestedStartTime,
            @RequestParam String requestedEndTime,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) @Min(value = PaginationConstants.MIN_PAGE_NUMBER, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) @Min(value = PaginationConstants.MIN_PAGE_SIZE, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = PaginationConstants.MAX_PAGE_SIZE, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        return ApiResponse.success(
                reservationService.getAvailableCarsByTimeSlot(requestedStartTime, requestedEndTime, currentPage, size),
                "특정 시간대 대여 가능한 차량 목록 조회 성공");
    }

    // [사용자] 차량 상세 조회
    @Operation(summary = "[사용자]차량 상세 조회", description = "차량 상세를 조회합니다.")
    @GetMapping("/member/cars/{carId}")
    public ApiResponse<MemberCarDetailResponse> getMemberCarDetail(@PathVariable Long carId) {

        return ApiResponse.success(reservationService.getMemberCarDetail(carId), "차량 상세 조회 성공");
    }

    // 차량 대여 신청하기
    @Operation(summary = "[사용자]차량 대여 신청하기", description = "차량 대여 신청을 합니다.")
    @PostMapping("/member/cars/{carId}")
    public ApiResponse<MemberCarDetailResponse> createMemberCarReservation(
            @RequestBody MemberCarReservationRequest request, @PathVariable Long carId) {

        return ApiResponse.success(reservationService.createMemberCarReservation(request, carId), "차량 대여 신청 성공");
    }

    // 차량 대여 취소하기
    @Operation(summary = "[사용자]차량 대여 취소하기", description = "차량 대여 취소를 합니다.")
    @DeleteMapping("/member/cars/{carId}")
    public ApiResponse<MemberCarDetailResponse> cancelMemberCarReservation(@PathVariable Long carId) {

        return ApiResponse.success(reservationService.cancelMemberCarReservation(carId), "차량 대여 취소 성공");
    }

    // [사용자] 현재 대여 중인 차량 조회
    @Operation(summary = "[사용자]현재 대여 중인 차량 조회", description = "현재 대여 중인 차량의 carId, startedAt, endedAt를 반환합니다.")
    @GetMapping("/member/current")
    public ApiResponse<CurrentCarResponse> getCurrentRentedCar() {

        return ApiResponse.success(reservationService.getCurrentRentedCar(), "현재 대여 중인 차량 조회 성공");
    }

    // [관리자] 연체 상태 수동 업데이트
    @Operation(summary = "[관리자]연체 상태 수동 업데이트", description = "연체된 대여 상태를 수동으로 업데이트합니다.")
    @PostMapping("/admin/update-overdue")
    public ApiResponse<String> updateOverdueRentals() {

        overdueUpdateService.manualUpdateOverdueRentals();
        return ApiResponse.success("연체 상태 업데이트 완료", "연체 상태 업데이트 성공");
    }
}

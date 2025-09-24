package com.vroomie.car_service.domain.operation.reservation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.vroomie.car_service.global.response.ApiResponse;
import com.vroomie.car_service.global.response.PageResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.member.ReservedLogResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.member.DetailReservedLogResponse;
import com.vroomie.car_service.domain.operation.reservation.service.ReservedLogService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.vroomie.car_service.global.constants.PaginationConstants;

@RestController
@RequestMapping("/reserved-logs")
@RequiredArgsConstructor
@Tag(name = "대여 이력 관리")
public class ReservedLogController {


    private final ReservedLogService reservedLogService;

    // [사용자] 내 대여 이력 목록 조회
    @GetMapping
    @Operation(summary = "[사용자]내 대여 이력 목록 조회", description = "내 대여 이력 목록을 조회합니다.")
    public ApiResponse<PageResponse<ReservedLogResponse>> getReservedLogList(
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) @Min(value = PaginationConstants.MIN_PAGE_NUMBER, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) @Min(value = PaginationConstants.MIN_PAGE_SIZE, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = PaginationConstants.MAX_PAGE_SIZE, message = "페이지 크기는 100 이하여야 합니다.") int size) {
        return ApiResponse.success(reservedLogService.getReservedLogList(currentPage, size), "내 대여 이력 목록 조회 성공");
    }

    // [사용자] 내 대여 이력 상세 조회
    @GetMapping("/{id}")
    @Operation(summary = "[사용자]내 대여 이력 상세 조회", description = "내 대여 이력 상세를 조회합니다.")
    public ApiResponse<DetailReservedLogResponse> getReservedLog(@PathVariable Long id) {
        return ApiResponse.success(reservedLogService.getReservedLog(id), "내 대여 이력 상세 조회 성공");
    }
}

package com.vroomie.car_service.domain.fleet.drivinglog.controller;

import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogDetailResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogEndReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogEndResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogStartReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogStartResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogSubmitResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogSummaryResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import com.vroomie.car_service.domain.fleet.drivinglog.service.DrivingLogService;
import com.vroomie.car_service.global.response.ApiResponse;
import com.vroomie.car_service.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fleet")
@Tag(name = "운행 기록", description = "운행 기록 등록 및 수정 관련 API")
public class DrivingLogController {

    private final DrivingLogService drivingLogService;

    @PostMapping("/cars/{carId}/driving-logs")
    @Operation(summary = "운행 기록 시작 시점 등록", description = "차량 운행을 시작하기 전에 필요한 정보를 등록합니다.")
    public ResponseEntity<ApiResponse<DrivingLogStartResDTO>> createDrivingLogStart(@PathVariable Long carId, @Valid @RequestBody DrivingLogStartReqDTO drivingLogStartReqDTO) {
        DrivingLogStartResDTO drivingLogStartResDTO = drivingLogService.createDrivingLogStart(drivingLogStartReqDTO, carId);

        ApiResponse<DrivingLogStartResDTO> apiResponse = ApiResponse.success(drivingLogStartResDTO, "운행 일지 등록에 성공했습니다.");

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/driving-logs/{id}")
    @Operation(summary = "운행 기록 종료 시점 등록", description = "차량 운행 종료 후 필요한 정보를 등록합니다.")
    public ResponseEntity<ApiResponse<DrivingLogEndResDTO>> updateDrivingLogEnd(@PathVariable Long id, @Valid @RequestBody DrivingLogEndReqDTO drivingLogEndReqDTO) {
        DrivingLogEndResDTO drivingLogEndResDTO = drivingLogService.updateDrivingLogEnd(id, drivingLogEndReqDTO);

        ApiResponse<DrivingLogEndResDTO> apiResponse = ApiResponse.success(drivingLogEndResDTO, "운행 일지 종료 정보 등록에 성공했습니다.");

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/driving-logs/{id}")
    @Operation(summary = "운행 기록 전체 수정", description = "차량 운행 기록을 전체 수정합니다. 관리자 권한만 가능한 기능입니다.")
    public ResponseEntity<ApiResponse<DrivingLogResDTO>> updateDrivingLogAll(@PathVariable Long id, @Valid @RequestBody DrivingLogReqDTO drivingLogReqDTO) {
        DrivingLogResDTO drivingLogResDTO = drivingLogService.updateDrivingLogAll(id, drivingLogReqDTO);

        ApiResponse<DrivingLogResDTO> apiResponse = ApiResponse.success(drivingLogResDTO, "운행 일지 수정에 성공했습니다.");

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/driving-logs/{id}/submit")
    @Operation(summary = "운행 기록 최종 제출", description = "차량 운행 기록을 최종 제출합니다. 작성자만 제출 가능하며, 제출 후에는 수정이 불가능합니다.")
    public ResponseEntity<ApiResponse<DrivingLogSubmitResDTO>> submitDrivingLog(@PathVariable Long id) {
        DrivingLogSubmitResDTO drivingLogSubmitResDTO = drivingLogService.submitDrivingLog(id);

        ApiResponse<DrivingLogSubmitResDTO> apiResponse = ApiResponse.success(drivingLogSubmitResDTO, "운행 일지가 최종 제출되었습니다.");

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/driving-logs")
    @Operation(summary = "운행 기록 목록 조회", description = "차량별, 임직원별, 기록 상태별, 운행 시작 시간별, 운행 종료 시간별, 최종 제출 시간 별로 필터링이 가능하며 정렬도 가능합니다.")
    public ResponseEntity<PageResponse<DrivingLogSummaryResDTO>> getDrivingLogList(
            @RequestParam(required = false) String empEmail,
            @RequestParam(required = false) Long carId,
            @RequestParam(required = false) LogStatus logStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endedAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAt,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<DrivingLogSummaryResDTO> drivingLogSummaryResDTOPageResponse = drivingLogService.getDrivingLogList(empEmail, carId, logStatus, startedAt, endedAt, updatedAt, sortBy, direction, page, size);

        return ResponseEntity.ok(drivingLogSummaryResDTOPageResponse);
    }

    @GetMapping("/driving-logs/{id}")
    @Operation(summary = "상세 운행 기록 조회", description = "상세 운행 기록으로 조회합니다.")
    public ResponseEntity<ApiResponse<DrivingLogDetailResDTO>> getDrivingLog(@PathVariable Long id) {
        DrivingLogDetailResDTO drivingLogDetailResDTO = drivingLogService.getDrivingLogDetail(id);

        ApiResponse<DrivingLogDetailResDTO> apiResponse = ApiResponse.success(drivingLogDetailResDTO, "운행일지 상세 조회에 성공했습니다.");

        return ResponseEntity.ok(apiResponse);
    }

}

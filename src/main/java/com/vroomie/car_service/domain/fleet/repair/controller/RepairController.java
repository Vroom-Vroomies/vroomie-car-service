package com.vroomie.car_service.domain.fleet.repair.controller;

import com.vroomie.car_service.domain.fleet.repair.dto.RepairCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairDetailDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairFinalizeRequestDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairListResponseDTO;
import com.vroomie.car_service.domain.fleet.repair.enums.RepairStatus;
import com.vroomie.car_service.domain.fleet.repair.service.RepairService;
import com.vroomie.car_service.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/fleet/repairs")
@RequiredArgsConstructor
@Tag(name = "수리 기록", description = "수리 기록 관련 API")
public class RepairController {

    private final RepairService repairService;

    @PostMapping
    @Operation(summary = "신규 수리 기록 등록")
    public ResponseEntity<ApiResponse<RepairDetailDTO>> createRepair(
            @RequestPart("req") RepairCreateRequestDTO req,
            @RequestPart(value = "beforeImages", required = false) List<MultipartFile> beforeImages,
            @RequestPart(value = "afterImages", required = false) List<MultipartFile> afterImages) {

        req.setEmpEmail("user02@wemade.com");

        RepairDetailDTO data = repairService.createRepair(req, beforeImages, afterImages);
        return ResponseEntity.ok(ApiResponse.success(data, "신규 수리 내역이 등록 되었습니다."));
    }

    @GetMapping
    @Operation(summary = "전체 수리 목록 조회", description = "전체 사고 목록을 조회합니다. \n 필터: 차 ID, 수리 상태, 최종 제출 여부")
    public ResponseEntity<ApiResponse<Page<RepairListResponseDTO>>> getRepairList(
            @RequestParam(required = false) Long carId,
            @RequestParam(required = false) RepairStatus status,
            @RequestParam(required = false) Boolean save,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        Page<RepairListResponseDTO> data = repairService.getAllRepairs(carId, status, save, pageable);

        return ResponseEntity.ok(ApiResponse.success(data, "수리 목록 조회에 성공했습니다."));
    }

    @GetMapping("/{id}")
    @Operation(summary = "수리 기록 상세 조회", description = "수리 기록 id로 수리 기록의 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<RepairDetailDTO>> getRepairDetail(@PathVariable Long id) {
        RepairDetailDTO data = repairService.getRepairById(id);

        return ResponseEntity.ok(ApiResponse.success(data, "수리 내역 상세 조회에 성공하였습니다."));
    }

    @PutMapping("/{id}")
    @Operation(summary = "수리 기록 수정", description = "수리 기록을 입력 받은 최신 정보로 업데이트 합니다")
    public ResponseEntity<ApiResponse<RepairDetailDTO>> updateRepair(
            @PathVariable Long id,
            @RequestPart("req") RepairCreateRequestDTO req,
            @RequestPart(value = "beforeImages", required = false) List<MultipartFile> beforeImages,
            @RequestPart(value = "afterImages", required = false) List<MultipartFile> afterImages) {

        RepairDetailDTO data = repairService.updateRepair(id, req, beforeImages, afterImages);

        return ResponseEntity.ok(ApiResponse.success(data, "수리 정보 수정에 성공하였습니다."));
    }

    @PatchMapping("/save/{id}")
    @Operation(summary = "사고 이력 최종 제출", description = "사고 이력을 최종 제출합니다. 최종 제출 이후에는 수정이 불가능합니다.")
    public ResponseEntity<ApiResponse<RepairDetailDTO>> finalizeRepair(
            @PathVariable Long id,
            @RequestBody RepairFinalizeRequestDTO req) {
        RepairDetailDTO data = repairService.finalizeRepair(id, req.isSave());

        return ResponseEntity.ok(ApiResponse.success(data, "수리 정보 최종 제출에 성공하였습니다."));
    }
}
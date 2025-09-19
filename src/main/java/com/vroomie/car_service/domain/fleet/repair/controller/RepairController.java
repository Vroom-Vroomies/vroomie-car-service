package com.vroomie.car_service.domain.fleet.repair.controller;

import com.vroomie.car_service.domain.fleet.repair.dto.RepairCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairDetailDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairFinalizeRequestDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairListResponseDTO;
import com.vroomie.car_service.domain.fleet.repair.enums.RepairStatus;
import com.vroomie.car_service.domain.fleet.repair.service.RepairService;
import com.vroomie.car_service.global.response.ApiResponse;
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
public class RepairController {

    private final RepairService repairService;

    @PostMapping
    public ResponseEntity<ApiResponse<RepairDetailDTO>> createRepair(
            @RequestPart("req") RepairCreateRequestDTO req,
            @RequestPart(value = "beforeImages", required = false) List<MultipartFile> beforeImages,
            @RequestPart(value = "afterImages", required = false) List<MultipartFile> afterImages) {

        req.setEmpEmail("user02@wemade.com");

        RepairDetailDTO data = repairService.createRepair(req, beforeImages, afterImages);
        return ResponseEntity.ok(ApiResponse.success(data, "신규 수리 내역이 등록 되었습니다."));
    }

    @GetMapping
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
    public ResponseEntity<ApiResponse<RepairDetailDTO>> getRepairDetail(@PathVariable Long id) {
        RepairDetailDTO data = repairService.getRepairById(id);

        return ResponseEntity.ok(ApiResponse.success(data, "수리 내역 상세 조회에 성공하였습니다."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RepairDetailDTO>> updateRepair(
            @PathVariable Long id,
            @RequestPart("req") RepairCreateRequestDTO req,
            @RequestPart(value = "beforeImages", required = false) List<MultipartFile> beforeImages,
            @RequestPart(value = "afterImages", required = false) List<MultipartFile> afterImages) {

        RepairDetailDTO data = repairService.updateRepair(id, req, beforeImages, afterImages);

        return ResponseEntity.ok(ApiResponse.success(data, "수리 정보 수정에 성공하였습니다."));
    }

    @PatchMapping("/save/{id}")
    public ResponseEntity<ApiResponse<RepairDetailDTO>> finalizeRepair(
            @PathVariable Long id,
            @RequestBody RepairFinalizeRequestDTO req) {
        RepairDetailDTO data = repairService.finalizeRepair(id, req.isSave());

        return ResponseEntity.ok(ApiResponse.success(data, "수리 정보 최종 제출에 성공하였습니다."));
    }
}
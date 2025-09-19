package com.vroomie.car_service.domain.fleet.inspection.controller;

import com.vroomie.car_service.domain.fleet.inspection.dto.InspectionCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.inspection.dto.InspectionDetailDTO;
import com.vroomie.car_service.domain.fleet.inspection.service.InspectionService;
import com.vroomie.car_service.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fleet/inspections")
@RequiredArgsConstructor
@Tag(name = "점검 기록", description = "점검 기록 관련 API")
public class InspectionController {

    private final InspectionService inspectionService;

    @PostMapping("/{carId}")
    @Operation(summary = "신규 점검 기록 등록")
    public ResponseEntity<ApiResponse<InspectionDetailDTO>> createInspection(
            @PathVariable Long carId,
            @RequestBody InspectionCreateRequestDTO req
    ) {
        req.setCreatorEmail("admin@wemade.com");

        InspectionDetailDTO data = inspectionService.createInspection(carId, req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    @Operation(summary = "점검 기록 상세 조회", description = "점검 기록 id로 점검 기록의 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<InspectionDetailDTO>> getInspectionById(@PathVariable Long id) {
        InspectionDetailDTO data = inspectionService.getInspectionById(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}

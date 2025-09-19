package com.vroomie.car_service.domain.fleet.accident.controller;

import com.vroomie.car_service.domain.fleet.accident.dto.AccidentCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentDetailDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentFinalizeRequestDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentListResponseDTO;
import com.vroomie.car_service.domain.fleet.accident.service.AccidentService;
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
@RequestMapping("/fleet/accidents")
@RequiredArgsConstructor
@Tag(name = "수리 기록", description = "수리 기록 관련 API")
public class AccidentController {

    private final AccidentService accidentService;

    @PostMapping
    @Operation(summary = "신규 수리 기록 등록")
    public ResponseEntity<ApiResponse<AccidentDetailDTO>> createAccident(
            @RequestPart("req") AccidentCreateRequestDTO req,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        req.setEmpEmail("user02@wemade.com");

        AccidentDetailDTO data = accidentService.createAccident(req, images);

        return ResponseEntity.ok(ApiResponse.success(data, "신규 사고 내역이 등록 되었습니다."));
    }

    @GetMapping
    @Operation(summary = "전체 사고 목록 조회", description = "전체 사고 목록을 조회합니다. \n 필터: 차 ID, 최종 제출 여부")
    public ResponseEntity<ApiResponse<Page<AccidentListResponseDTO>>> getAccidentList(
            @RequestParam(required = false) Long carId,
            @RequestParam(required = false) Boolean save,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<AccidentListResponseDTO> data = accidentService.getAllAccidents(carId, save, pageable);
        return ResponseEntity.ok(ApiResponse.success(data, "사고 목록 조회에 성공했습니다."));
    }

    @GetMapping("/{id}")
    @Operation(summary = "사고 이력 상세 조회", description = "ID로 사고 정보를 상세 조회합니다.")
    public ResponseEntity<ApiResponse<AccidentDetailDTO>> getAccidentDetail(@PathVariable Long id) {
        AccidentDetailDTO data = accidentService.getAccidentById(id);
        return ResponseEntity.ok(ApiResponse.success(data, "사고 내역 상세 조회에 성공하였습니다."));
    }

    @PutMapping("/{id}")
    @Operation(summary = "사고 정보 수정", description = "사고 기록을 입력 받은 최신 정보로 업데이트 합니다")
    public ResponseEntity<ApiResponse<AccidentDetailDTO>> updateAccident(
            @PathVariable Long id,
            @RequestPart("req") AccidentCreateRequestDTO req,
            @RequestPart(value = "images", required = false) List<MultipartFile> images){
        AccidentDetailDTO data = accidentService.updateAccident(id, req, images);
        return ResponseEntity.ok(ApiResponse.success(data, "사고 정보 수정에 성공하였습니다."));
    }

    @PatchMapping("/save/{id}")
    @Operation(summary = "사고 이력 최종 제출", description = "사고 이력을 최종 제출합니다. 최종 제출 이후에는 수정이 불가능합니다.")
    public ResponseEntity<ApiResponse<AccidentDetailDTO>> finalizeAccident(
            @PathVariable Long id,
            @RequestBody AccidentFinalizeRequestDTO req) {
        AccidentDetailDTO data = accidentService.finalizeAccident(id, req.isSave());
        return ResponseEntity.ok(ApiResponse.success(data, "사고 정보 제출에 성공하였습니다."));
    }

}

package com.vroomie.car_service.domain.fleet.accident.controller;

import com.vroomie.car_service.domain.fleet.accident.dto.AccidentCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentDetailDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentFinalizeRequestDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentListResponseDTO;
import com.vroomie.car_service.domain.fleet.accident.service.AccidentService;
import com.vroomie.car_service.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fleet/accidents")
@RequiredArgsConstructor
public class AccidentController {

    private final AccidentService accidentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccidentDetailDTO>> createAccident(@RequestBody AccidentCreateRequestDTO req) {
        AccidentDetailDTO data = accidentService.createAccident(req);

        return ResponseEntity.ok(ApiResponse.success(data, "신규 사고 내역이 등록 되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AccidentListResponseDTO>>> getAccidentList(
            @RequestParam(required = false) Long carId,
            @RequestParam(required = false) Boolean save,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<AccidentListResponseDTO> data = accidentService.getAllAccidents(carId, save, pageable);
        return ResponseEntity.ok(ApiResponse.success(data, "사고 목록 조회에 성공했습니다."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccidentDetailDTO>> getAccidentDetail(@PathVariable Long id) {
        AccidentDetailDTO data = accidentService.getAccidentById(id);
        return ResponseEntity.ok(ApiResponse.success(data, "사고 내역 상세 조회에 성공하였습니다."));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AccidentDetailDTO>> updateAccident(
            @PathVariable Long id,
            @RequestBody AccidentCreateRequestDTO req){
        AccidentDetailDTO data = accidentService.updateAccident(id, req);
        return ResponseEntity.ok(ApiResponse.success(data, "사고 정보 수정에 성공하였습니다."));
    }

    @PatchMapping("/save/{id}")
    public ResponseEntity<ApiResponse<AccidentDetailDTO>> finalizeAccident(
            @PathVariable Long id,
            @RequestBody AccidentFinalizeRequestDTO req) {
        AccidentDetailDTO data = accidentService.finalizeAccident(id, req.isSave());
        return ResponseEntity.ok(ApiResponse.success(data, "사고 정보 제출에 성공하였습니다."));
    }

}

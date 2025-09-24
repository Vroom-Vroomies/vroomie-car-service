package com.vroomie.car_service.domain.fleet.drivinglog.controller;

import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogPdfReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.service.DrivingLogPdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fleet")
@Tag(name = "운행 기록 pdf 생성", description = "운행 기록 pdf 생성 관련 API")
public class DrivingLogPdfController {

    private final DrivingLogPdfService drivingLogPdfService;

    @PostMapping("/cars/{carId}/driving-logs/pdf")
    @Operation(summary = "운행 기록 pdf 생성", description = "사용자가 기간을 선택하면 해당 기간 동안의 특정 차량의 운행 기록 정보가 pdf로 추출됩니다.")
    public ResponseEntity<byte[]> downloadDrivingLogPdf(@PathVariable Long carId, @RequestBody DrivingLogPdfReqDTO drivingLogPdfReqDTO) {
        byte[] pdfBytes = drivingLogPdfService.generateDrivingLogPdf(carId, drivingLogPdfReqDTO);
        String filename = URLEncoder.encode("운행일지.pdf", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}

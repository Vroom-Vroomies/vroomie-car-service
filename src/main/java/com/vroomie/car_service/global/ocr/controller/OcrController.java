package com.vroomie.car_service.global.ocr.controller;

import com.vroomie.car_service.global.ocr.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ocr")
public class OcrController {

    private final OcrService ocrService;

    /**
     * 클라이언트로부터 받은 이미지 파일을 전달하고 결과 반환(저장x)
     * @param file OCR을 수행할 이미지 파일
     * @return OCR API의 응답 결과 (JSON 문자열)
     */
    @PostMapping("/template/infer")
    public ResponseEntity<String> inferTemplateOcr(@RequestParam("file") MultipartFile file) {

        String result = ocrService.inferOcr(file);
        return ResponseEntity.ok(result);
    }
}
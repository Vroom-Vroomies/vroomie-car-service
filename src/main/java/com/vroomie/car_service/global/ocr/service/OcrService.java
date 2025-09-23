package com.vroomie.car_service.global.ocr.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.UUID;

@Service
public class OcrService {

    private final WebClient webClient;
    private final String ocrSecretKey;

    public OcrService(WebClient.Builder webClientBuilder,
                      @Value("${ncloud.ocr.url}") String ocrApiUrl,
                      @Value("${ncloud.ocr.secret-key}") String ocrSecretKey) {
        this.webClient = webClientBuilder.baseUrl(ocrApiUrl).build();
        this.ocrSecretKey = ocrSecretKey;
    }

    /**
     * MultipartFile을 받아 서버에 저장하지 않고 CLOVA OCR API로 바로 전달하여 호출
     * 결과를 JSON 문자열로 반환
     * @param file OCR을 수행할 이미지 파일
     * @return OCR 결과 JSON 문자열
     */
    public String inferOcr(MultipartFile file) {
        try {
            // 요청 바디(multipart/form-data) 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // JSON 메시지 부분 추가
            body.add("message", createOcrRequestMessage(file));

            // 이미지 파일 데이터 부분 추가
            // ByteArrayResource를 사용해 파일 데이터를 메모리에서 바로 리소스로 만듦
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    // 원본 파일 이름을 전달
                    return file.getOriginalFilename();
                }
            });

            // API 호출
            return webClient.post()
                    .uri("/infer")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("X-OCR-SECRET", ocrSecretKey)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (IOException e) {
            throw new RuntimeException("OCR 요청 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * CLOVA OCR API 요청에 필요한 JSON 메시지 문자열 생성
     * @param file 업로드된 파일
     * @return OCR 요청 JSON 문자열
     */
    private String createOcrRequestMessage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String format = "jpg"; // 기본값
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (extension.matches("^(jpg|jpeg|png|pdf|tiff)$")) {
                format = extension.equals("jpeg") ? "jpg" : extension;
            }
        }

        return String.format(
                "{\"images\":[{\"format\":\"%s\",\"name\":\"medium\",\"data\":null,\"url\":null}],\"lang\":\"ko\",\"requestId\":\"%s\",\"resultType\":\"string\",\"timestamp\":%d,\"version\":\"V2\"}",
                format,
                UUID.randomUUID().toString(),
                System.currentTimeMillis()
        );
    }
}
package com.vroomie.car_service.global.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    /**
     * MultipartFile을 MinIO에 업로드, 접근 가능한 URL 반환
     * @param file 업로드할 파일
     * @param domain 파일의 종류 (예: cars, driving-logs)
     * @return 업로드된 파일의 URL
     */
    public String uploadFile(MultipartFile file, String domain) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // "도메인/UUID-원래파일명" 형태로 파일 경로를 생성
            String fileName = domain + "/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // MinIO 서버의 실제 endpoint 주소와 버킷, 파일 이름을 조합하여 최종 URL 생성
            String endpoint = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            ).split("\\?")[0];

            // presigned URL에서 쿼리 파라미터를 제거한 URL
            return endpoint;

        } catch (Exception e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }
}
package com.vroomie.car_service.domain.fleet.accident.service;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.employee.repository.EmployeeRepository;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentDetailDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentListResponseDTO;
import com.vroomie.car_service.domain.fleet.accident.entity.AccidentEntity;
import com.vroomie.car_service.domain.fleet.accident.entity.AccidentImageEntity;
import com.vroomie.car_service.domain.fleet.accident.enums.AccidentType;
import com.vroomie.car_service.domain.fleet.accident.mapper.AccidentMapper;
import com.vroomie.car_service.domain.fleet.accident.repository.AccidentImageRepository;
import com.vroomie.car_service.domain.fleet.accident.repository.AccidentRepository;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccidentService {

    private final AccidentRepository accidentRepository;
    private final AccidentImageRepository accidentImageRepository;
    private final CarRepository carRepository;
    private final EmployeeRepository employeeRepository;
    private final AccidentMapper accidentMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 생성
    @Transactional
    public AccidentDetailDTO createAccident(AccidentCreateRequestDTO req, List<MultipartFile> images) {
        if (images != null && images.size() > 10) {
            throw new BusinessException(ErrorCode.FILE_COUNT_EXCEEDED);
        }

        CarEntity car = carRepository.findById(req.getCarId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));
        EmployeeEntity employee = employeeRepository.findByEmail(req.getEmpEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        AccidentEntity accident = AccidentEntity.builder()
                .car(car)
                .employee(employee)
                .type(AccidentType.valueOf(req.getType()))
                .note(req.getNote())
                .detail(req.getDetail())
                .occurredAt(req.getDate())
                .cost(req.getCost())
                .isSaved(false)
                .build();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String storedFileName = storeFile(image);
                AccidentImageEntity accidentImage = AccidentImageEntity.builder()
                        .image(storedFileName)
                        .build();
                accident.addImage(accidentImage);
            }
        }

        AccidentEntity savedAccident = accidentRepository.save(accident);
        return accidentMapper.toDetailResponse(savedAccident);
    }

    // 상세 조회
    public AccidentDetailDTO getAccidentById(Long id) {
        AccidentEntity accident = accidentRepository.findByIdWithImages(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCIDENT_NOT_FOUND));

        return accidentMapper.toDetailResponse(accident);
    }

    // 전체 조회
    public Page<AccidentListResponseDTO> getAllAccidents(Long carId, Boolean save, Pageable pageable) {
        Page<AccidentEntity> accidents = accidentRepository.findWithFilters(carId, save, pageable);
        return accidents.map(accidentMapper::toListResponseDTO);
    }

    // 수정
    @Transactional
    public AccidentDetailDTO updateAccident(Long id, AccidentCreateRequestDTO req, List<MultipartFile> images) {
        if (images != null && images.size() > 10) {
            throw new BusinessException(ErrorCode.FILE_COUNT_EXCEEDED);
        }

        AccidentEntity accident = accidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCIDENT_NOT_FOUND));

        checkFinalized(accident);

        // 1. 기존 이미지 파일 삭제 및 DB 정보 제거
        List<AccidentImageEntity> oldImages = accidentImageRepository.findByAccidentId(id);
        for (AccidentImageEntity image : oldImages) {
            deleteFile(image.getImage());
        }
        accidentImageRepository.deleteByAccidentId(id);

        // 2. 텍스트 정보 업데이트
        accidentMapper.update(req, accident);

        // 3. 새로운 이미지 추가
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String storedFileName = storeFile(image);
                AccidentImageEntity accidentImage = AccidentImageEntity.builder()
                        .image(storedFileName)
                        .build();
                accident.addImage(accidentImage);
            }
        }

        return accidentMapper.toDetailResponse(accidentRepository.save(accident));
    }

    // 최종 제출
    @Transactional
    public AccidentDetailDTO finalizeAccident(Long id, boolean save) {

        if (!save) {
            throw new BusinessException(ErrorCode.CANT_FINALIZE_FALSE);
        }

        AccidentEntity accident = accidentRepository.findByIdWithImages(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCIDENT_NOT_FOUND));
        checkFinalized(accident);

        if (accident.getAccidentImages() == null || accident.getAccidentImages().isEmpty()) {
            throw new BusinessException(ErrorCode.IMAGE_REQUIRED_FOR_FINALIZE);
        }

        accident.finalize();

        return accidentMapper.toDetailResponse(accident);
    }

    // 헬퍼 메소드 - 최종 저장 여부 확인
    private void checkFinalized(AccidentEntity accident) {
        if (accident.isSaved()) {
            throw new BusinessException(ErrorCode.ACCIDENT_ALREADY_FINALIZED);
        }
    }

    private String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;
        Path destinationFile = Paths.get(uploadDir).resolve(storedFileName).toAbsolutePath();

        try {
            Files.createDirectories(Paths.get(uploadDir)); // 디렉토리 생성
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return "/images/accidents/" + storedFileName; // URL 경로 반환
    }

    private void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;

        try {
            // URL 경로에서 파일 이름만 추출
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            Path file = Paths.get(uploadDir).resolve(fileName).toAbsolutePath();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // 파일 삭제 실패 시 로그만 남기고 넘어갈 수 있음
            e.printStackTrace();
        }
    }
}

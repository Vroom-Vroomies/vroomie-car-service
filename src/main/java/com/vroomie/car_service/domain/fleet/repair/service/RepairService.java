package com.vroomie.car_service.domain.fleet.repair.service;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.employee.repository.EmployeeRepository;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairDetailDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairListResponseDTO;
import com.vroomie.car_service.domain.fleet.repair.entity.RepairEntity;
import com.vroomie.car_service.domain.fleet.repair.entity.RepairImageEntity;
import com.vroomie.car_service.domain.fleet.repair.enums.RepairImageType;
import com.vroomie.car_service.domain.fleet.repair.enums.RepairStatus;
import com.vroomie.car_service.domain.fleet.repair.enums.RepairType;
import com.vroomie.car_service.domain.fleet.repair.mapper.RepairMapper;
import com.vroomie.car_service.domain.fleet.repair.repository.RepairImageRepository;
import com.vroomie.car_service.domain.fleet.repair.repository.RepairRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepairService {

    private final RepairRepository repairRepository;
    private final RepairImageRepository repairImageRepository;
    private final CarRepository carRepository;
    private final EmployeeRepository employeeRepository;
    private final RepairMapper repairMapper;

    @Value("${file.upload-dir.repairs}")
    private String uploadDir;

    @Transactional
    public RepairDetailDTO createRepair(RepairCreateRequestDTO req, List<MultipartFile> beforeImages, List<MultipartFile> afterImages) {
        validateImageCounts(beforeImages, afterImages);

        CarEntity car = carRepository.findById(req.getCarId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));
        EmployeeEntity employee = employeeRepository.findByEmail(req.getEmpEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        RepairEntity repair = RepairEntity.builder()
                .car(car)
                .employee(employee)
                .type(RepairType.valueOf(req.getType()))
                .detail(req.getDetail())
                .status(RepairStatus.valueOf(req.getStatus()))
                .startedAt(req.getStartedAt())
                .endedAt(req.getEndedAt())
                .cost(req.getCost())
                .isSaved(false)
                .build();

        processAndAddImages(repair, beforeImages, RepairImageType.BEFORE);
        processAndAddImages(repair, afterImages, RepairImageType.AFTER);

        RepairEntity savedRepair = repairRepository.save(repair);
        return repairMapper.toDetailResponse(savedRepair);
    }

    public RepairDetailDTO getRepairById(Long id) {
        RepairEntity repair = repairRepository.findByIdWithImages(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPAIR_NOT_FOUND));
        return repairMapper.toDetailResponse(repair);
    }

    public Page<RepairListResponseDTO> getAllRepairs(Long carId, RepairStatus status, Boolean isSaved, Pageable pageable) {
        Page<RepairEntity> repairs = repairRepository.findWithFilters(carId, status, isSaved, pageable);
        return repairs.map(repairMapper::toListResponseDTO);
    }

    @Transactional
    public RepairDetailDTO updateRepair(Long id, RepairCreateRequestDTO req, List<MultipartFile> beforeImages, List<MultipartFile> afterImages) {
        validateImageCounts(beforeImages, afterImages);

        RepairEntity repair = repairRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPAIR_NOT_FOUND));

        checkFinalized(repair);

        // 기존 이미지 삭제
        List<RepairImageEntity> oldImages = repairImageRepository.findByRepairId(id);
        for (RepairImageEntity image : oldImages) {
            deleteFile(image.getImage());
        }
        repairImageRepository.deleteByRepairId(id);
        repair.getRepairImages().clear();

        // 텍스트 정보 업데이트
        repairMapper.update(req, repair);

        // 새 이미지 추가
        processAndAddImages(repair, beforeImages, RepairImageType.BEFORE);
        processAndAddImages(repair, afterImages, RepairImageType.AFTER);

        return repairMapper.toDetailResponse(repairRepository.save(repair));
    }

    @Transactional
    public RepairDetailDTO finalizeRepair(Long id, boolean save) {
        if (!save) {
            throw new BusinessException(ErrorCode.CANT_FINALIZE_REPAIR_FALSE);
        }

        RepairEntity repair = repairRepository.findByIdWithImages(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPAIR_NOT_FOUND));
        checkFinalized(repair);

        boolean hasBeforeImage = repair.getRepairImages().stream().anyMatch(img -> img.getType() == RepairImageType.BEFORE);
        boolean hasAfterImage = repair.getRepairImages().stream().anyMatch(img -> img.getType() == RepairImageType.AFTER);

        if (!hasBeforeImage || !hasAfterImage) {
            throw new BusinessException(ErrorCode.IMAGE_REQUIRED_FOR_REPAIR_FINALIZE);
        }

        repair.finalizeRepair();
        return repairMapper.toDetailResponse(repair);
    }

    private void processAndAddImages(RepairEntity repair, List<MultipartFile> images, RepairImageType type) {
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String storedFileName = storeFile(image);
                RepairImageEntity repairImage = RepairImageEntity.builder()
                        .image(storedFileName)
                        .type(type)
                        .build();
                repair.addImage(repairImage);
            }
        }
    }

    private void validateImageCounts(List<MultipartFile> beforeImages, List<MultipartFile> afterImages) {
        if (beforeImages != null && beforeImages.size() > 10) {
            throw new BusinessException(ErrorCode.FILE_COUNT_EXCEEDED);
        }
        if (afterImages != null && afterImages.size() > 10) {
            throw new BusinessException(ErrorCode.FILE_COUNT_EXCEEDED);
        }
    }

    private void checkFinalized(RepairEntity repair) {
        if (repair.isSaved()) {
            throw new BusinessException(ErrorCode.REPAIR_ALREADY_FINALIZED);
        }
    }

    private String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;
        Path destinationFile = Paths.get(uploadDir).resolve(storedFileName).toAbsolutePath();

        try {
            Files.createDirectories(Paths.get(uploadDir));
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return storedFileName;
    }

    private void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            Path file = Paths.get(uploadDir).resolve(fileName).toAbsolutePath();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package com.vroomie.car_service.domain.fleet.drivinglog.service;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.employee.enums.Role;
import com.vroomie.car_service.domain.employee.excpetion.EmployeeException;
import com.vroomie.car_service.domain.employee.repository.EmployeeRepository;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.exceptions.CarException;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogDetailResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogEndReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogEndResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogStartReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogStartResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogSubmitResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogSummaryResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.entity.DrivingLogEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import com.vroomie.car_service.domain.fleet.drivinglog.exception.DrivingLogException;
import com.vroomie.car_service.domain.fleet.drivinglog.mapper.DrivingLogMapper;
import com.vroomie.car_service.domain.fleet.drivinglog.repository.DrivingLogRepository;
import com.vroomie.car_service.global.response.PageResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.vroomie.car_service.domain.employee.excpetion.EmployeeException.*;
import static com.vroomie.car_service.domain.fleet.drivinglog.exception.DrivingLogException.*;
import static com.vroomie.car_service.domain.fleet.drivinglog.exception.DrivingLogException.alreadyExistsLogException;

@Service
@RequiredArgsConstructor
public class DrivingLogService {

    private final DrivingLogRepository drivingLogRepository;
    private final DrivingLogMapper drivingLogMapper;
    private final EmployeeRepository employeeRepository;
    private final CarRepository carRepository;

    // 운행 기록 시작 시점 등록
    @Transactional
    public DrivingLogStartResDTO createDrivingLogStart(DrivingLogStartReqDTO drivingLogStartReqDTO, Long carId) {

        // 직원 정보 조회
        String empEmail = getCurrentUserEmail();
        EmployeeEntity employeeEntity = employeeRepository.findByEmail(empEmail)
                .orElseThrow(EmployeeException::employeeNotFoundException);

        // 차 정보 조회
        CarEntity carEntity = carRepository.findById(carId)
                .orElseThrow(CarException::carNotFoundException);

        // 이미 운행 중인 기록이 있는지 확인
        if (drivingLogRepository.existsByEmployeeEntity_EmailAndCarEntity_IdAndLogStatus(empEmail, carId, LogStatus.WRITING)){
            throw alreadyExistsLogException();
        }

        // DrivingLogStartReqDTO -> Entity 변환
        DrivingLogEntity drivingLogEntity = drivingLogMapper.toDrivingLogEntity(drivingLogStartReqDTO, employeeEntity, carEntity, LogStatus.WRITING);

        // Entity DB 저장
        DrivingLogEntity savedDrivingLog = drivingLogRepository.save(drivingLogEntity);

        // Entity -> DrivingLogStartResDTO 변환
        DrivingLogStartResDTO drivingLogStartResDTO = drivingLogMapper.toDrivingLogStartResDTO(savedDrivingLog);

        return drivingLogStartResDTO;
    }

    // 운행 기록 종료 시점 업데이트
    @Transactional
    public DrivingLogEndResDTO updateDrivingLogEnd(Long id, DrivingLogEndReqDTO drivingLogEndReqDTO) {
        // 운행일지 조회
        DrivingLogEntity drivingLogEntity = drivingLogRepository.findById(id).orElseThrow(DrivingLogException::drivingLogNotFoundException);

        // 현재 종료 기록을 업데이트하는 직원 조회
        String empEmail = getCurrentUserEmail();
        EmployeeEntity employeeEntity = employeeRepository.findByEmail(empEmail).orElseThrow(EmployeeException::employeeNotFoundException);

        // 종료 기록을 업데이트한 직원과 기존 작성자 비교
        if (!drivingLogEntity.getEmployeeEntity().getEmail().equals(empEmail)) {
            throw employeeNotMatchException();
        }

        // 종료 시간 검증
        if (!drivingLogEndReqDTO.getEndedAt().isAfter(drivingLogEntity.getStartedAt())) {
            throw invalidEndDateException();
        }

        // 종료 거리계 검증
        if (drivingLogEndReqDTO.getEndOdometer() <= drivingLogEntity.getStartOdometer()) {
            throw invalidEndOdometerException();
        }

        // 운행일지 제출 상태 검증
        if (Boolean.TRUE.equals(drivingLogEntity.getIsSaved()) || drivingLogEntity.getLogStatus().equals(LogStatus.COMPLETED)) {
            throw alreadySubmitsLogException();
        }

        // 운행일지 업데이트
        drivingLogEntity.updateDrivingLogEnd(drivingLogEndReqDTO, employeeEntity);

        // Entity -> DrivingLogEndResDTO
        DrivingLogEndResDTO drivingLogEndResDTO = drivingLogMapper.toDrivingLogEndResDTO(drivingLogEntity);

        return drivingLogEndResDTO;
    }

    // 운행 기록 전체 수정
    @Transactional
    public DrivingLogResDTO updateDrivingLogAll(Long id, DrivingLogReqDTO drivingLogReqDTO) {
        // 운행 일지 조회
        DrivingLogEntity drivingLogEntity = drivingLogRepository.findById(id).orElseThrow(DrivingLogException::drivingLogNotFoundException);

        // 현재 수정하려는 직원 조회 (관리자 권한 이상 및 작성자 본인만 수정 가능)
        String empEmail = getCurrentUserEmail();
        EmployeeEntity employeeEntity = employeeRepository.findByEmail(empEmail).orElseThrow(EmployeeException::employeeNotFoundException);
        if (employeeEntity.getRole().equals(Role.USER) && !drivingLogEntity.getEmployeeEntity().getEmail().equals(empEmail)) {
            throw employeeNotEnoughRole();
        }

        // 종료 시간 검증
        if (!drivingLogReqDTO.getEndedAt().isAfter(drivingLogEntity.getStartedAt())) {
            throw invalidEndDateException();
        }

        // 종료 거리계 검증
        if (drivingLogReqDTO.getEndOdometer() <= drivingLogEntity.getStartOdometer()) {
            throw invalidEndOdometerException();
        }

        // 운행일지 제출 상태 검증
        if (Boolean.TRUE.equals(drivingLogEntity.getIsSaved()) || drivingLogEntity.getLogStatus().equals(LogStatus.WRITING)) {
            throw alreadySubmitsLogException();
        }

        // 운행 일지 전체 업데이트
        drivingLogEntity.updateDrivingLogAll(drivingLogReqDTO, employeeEntity);

        // Entity -> DrivingLogResDTO
        DrivingLogResDTO drivingLogResDTO = drivingLogMapper.toDrivingLogResDTO(drivingLogEntity);

        return drivingLogResDTO;
    }

    // 운행 기록 최종 제출
    @Transactional
    public DrivingLogSubmitResDTO submitDrivingLog(Long id) {
        // 운행 일지 조회
        DrivingLogEntity drivingLogEntity = drivingLogRepository.findById(id).orElseThrow(DrivingLogException::drivingLogNotFoundException);

        // 작성자 본인만 제출 가능
        String empEmail = getCurrentUserEmail();
        if (!drivingLogEntity.getEmployeeEntity().getEmail().equals(empEmail)) {
            throw employeeNotMatchException();
        }

        // 운행일지 제출 상태 확인
        if (Boolean.TRUE.equals(drivingLogEntity.getIsSaved())) {
            throw alreadySubmitsLogException();
        }

        // 제출
        drivingLogEntity.submitDrivingLog();

        // Entity -> DrivingLogSubmitResDTO
        DrivingLogSubmitResDTO drivingLogSubmitResDTO = drivingLogMapper.toDrivingLogSubmitResDTO(drivingLogEntity);

        return drivingLogSubmitResDTO;
    }

    // 차량별 운행 일지 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<DrivingLogSummaryResDTO> getDrivingLogList(String empEmail, Long carId, LogStatus logStatus, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime updatedAt, String sortBy, String direction, Integer page, Integer size) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        // 기본 정렬: 최신 작성 순서
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(sortBy) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(sortDirection, sortBy);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Specification으로 동적 필터링
        Page<DrivingLogEntity> drivingLogEntityPage = drivingLogRepository.findAll((root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            if (carId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("carEntity").get("id"), carId));
            }

            if (empEmail != null && !empEmail.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("employeeEntity").get("email"), empEmail));
            }

            if (logStatus != null) {
                predicate = cb.and(predicate, cb.equal(root.get("logStatus"), logStatus));
            }

            if (startDate != null && endDate != null) {
                predicate = cb.and(predicate, cb.between(root.get("startedAt"), startDate, endDate));
            } else if (startDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startedAt"), startDate));
            } else if (endDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("startedAt"), endDate));
            }

            return predicate;
        },pageable);

        // Entity -> DrivingLogSummaryResDTO
        Page<DrivingLogSummaryResDTO> drivingLogSummaryResDTOPage = drivingLogEntityPage.map(drivingLogMapper::toDrivingLogSummaryResDTO);

        return PageResponse.of(drivingLogSummaryResDTOPage);
    }

    // 운행 일지 상세 조회
    @Transactional(readOnly = true)
    public DrivingLogDetailResDTO getDrivingLogDetail(Long id) {
        // 운행일지 조회
        DrivingLogEntity drivingLogEntity = drivingLogRepository.findById(id).orElseThrow(DrivingLogException::drivingLogNotFoundException);

        // 임직원 권한 조회
        String empEmail = getCurrentUserEmail();
        EmployeeEntity employeeEntity = employeeRepository.findByEmail(empEmail).orElseThrow(EmployeeException::employeeNotFoundException);
        Role role = employeeEntity.getRole();

        // 권한 확인 (작성자 본인 또는 관리자 이상 권한만 조회 가능)
        if (!drivingLogEntity.getEmployeeEntity().getEmail().equals(empEmail) && role.equals(Role.USER)) {
            throw employeeNotEnoughRole();
        }

        // Entity -> DrivingLogDetailResDTO
        DrivingLogDetailResDTO drivingLogDetailResDTO = drivingLogMapper.toDrivingLogDetailResDTO(drivingLogEntity);

        return drivingLogDetailResDTO;
    }

    // 임시 유저
    private String getCurrentUserEmail() {
        return "admin@wemade.com";
    }

}

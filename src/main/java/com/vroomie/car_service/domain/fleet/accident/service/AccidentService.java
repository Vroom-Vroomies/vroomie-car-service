package com.vroomie.car_service.domain.fleet.accident.service;

import com.vroomie.car_service.domain.employee.repository.EmployeeRepository;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentDetailDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentListResponseDTO;
import com.vroomie.car_service.domain.fleet.accident.entity.AccidentEntity;
import com.vroomie.car_service.domain.fleet.accident.enums.AccidentType;
import com.vroomie.car_service.domain.fleet.accident.mapper.AccidentMapper;
import com.vroomie.car_service.domain.fleet.accident.repository.AccidentRepository;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccidentService {

    private final AccidentRepository accidentRepository;
    private final CarRepository carRepository;
    private final EmployeeRepository employeeRepository;
    private final AccidentMapper accidentMapper;

    // 생성
    @Transactional
    public AccidentDetailDTO createAccident(AccidentCreateRequestDTO req) {
        AccidentEntity accident = AccidentEntity.builder()
                .car(carRepository.findById(req.getCarId()).orElseThrow())
                .employee(employeeRepository.findByEmail(req.getEmpEmail()).orElseThrow())
                .type(AccidentType.valueOf(req.getType()))
                .note(req.getNote())
                .detail(req.getDetail())
                .occurredAt(req.getDate())
                .cost(req.getCost())
                .isSaved(false) // 최초 생성 시 false -> 사용자 경험 개선 고민 필요
                .build();

        return accidentMapper.toDetailResponse(accidentRepository.save(accident));
    }

    // 상세 조회
    public AccidentDetailDTO getAccidentById(Long id) {
        AccidentEntity accident = accidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCIDENT_NOT_FOUND));

        checkFinalized(accident);

        return accidentMapper.toDetailResponse(accident);
    }

    // 전체 조회
    public Page<AccidentListResponseDTO> getAllAccidents(Long carId, boolean save, Pageable pageable) {
        Page<AccidentEntity> accidents = accidentRepository.findAll(pageable);
        // TODO. 필터 처리 추가

        return accidents.map(accidentMapper::toListResponseDTO);
    }

    // 수정
    public AccidentDetailDTO updateAccident(Long id, AccidentCreateRequestDTO req) {
        AccidentEntity accident = accidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCIDENT_NOT_FOUND));

        checkFinalized(accident);

        accidentMapper.update(req, accident);

        return accidentMapper.toDetailResponse(accident);
    }

    // 최종 제출
    public AccidentDetailDTO finalizeAccident(Long id, boolean save) {

        if (!save) {
            throw new BusinessException(ErrorCode.CANT_FINALIZE_FALSE);
        }

        AccidentEntity accident = accidentRepository.findById(id)
                .orElseThrow();
        checkFinalized(accident);

        accident.finalize();

        return accidentMapper.toDetailResponse(accident);
    }

    // 헬퍼 메소드 - 최종 저장 여부 확인
    private void checkFinalized(AccidentEntity accident) {
        if (accident.isSaved()) {
            throw new BusinessException(ErrorCode.ACCIDENT_ALREADY_FINALIZED);
        }
    }
}

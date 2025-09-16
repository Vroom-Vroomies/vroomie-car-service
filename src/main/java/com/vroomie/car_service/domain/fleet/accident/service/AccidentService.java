package com.vroomie.car_service.domain.fleet.accident.service;

import com.vroomie.car_service.domain.fleet.accident.dto.AccidentDetailDTO;
import com.vroomie.car_service.domain.fleet.accident.entity.AccidentEntity;
import com.vroomie.car_service.domain.fleet.accident.enums.AccidentType;
import com.vroomie.car_service.domain.fleet.accident.repository.AccidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccidentService {

    private final AccidentRepository accidentRepository;
    // private final CarRepository carRepository;
    // private final EmployeeRepository employeeRepository;

    @Transactional
    public AccidentDetailDTO createAccident(AccidentDetailDTO req) {
        AccidentEntity accident = AccidentEntity.builder()
                .car(null) // carRepository.findById(dto.getCarId()).orElseThrow()
                .employee(null) // employeeRepository.findById(dto.getEmpEmail()).orElseThrow())
                .type(AccidentType.valueOf(req.getType()))
                .note(req.getNote())
                .detail(req.getDetail())
                .occurredAt(req.getDate())
                .cost(req.getCost())
                .isSaved(false) // 최초 생성 시 false -> 사용자 경험 개선 고민 필요
                .build();

        AccidentEntity savedAccident = accidentRepository.save(accident);
        return null;
    }
}

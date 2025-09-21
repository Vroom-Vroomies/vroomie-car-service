package com.vroomie.car_service.domain.contract.insurance_contract.service;

import com.vroomie.car_service.domain.contract.insurance_contract.dto.request.InsuranceRegistRequest;
import com.vroomie.car_service.domain.contract.insurance_contract.dto.response.InsuranceDetailResponse;
import com.vroomie.car_service.domain.contract.insurance_contract.dto.response.InsuranceSimpleResponse;
import com.vroomie.car_service.domain.contract.insurance_contract.entity.InsuContractEntity;
import com.vroomie.car_service.domain.contract.insurance_contract.mapper.InsuranceMapper;
import com.vroomie.car_service.domain.contract.insurance_contract.repository.InsuContractRepository;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuContractRepository insuranceRepository;
    private final InsuranceMapper insuranceMapper;
    private final CarRepository carRepository;

    public List<InsuranceSimpleResponse> getAllInsuranceList(Long carId){
        try{
            log.info(">>>> [InsuranceService] 보험 목록 조회 시작 - carId: {}", carId);
            List<InsuContractEntity> insuranceList = insuranceRepository.findAllByCarId(carId);
            return insuranceMapper.toInsuranceResponseList(insuranceList);
        } catch (BusinessException e){
            throw new BusinessException(ErrorCode.INSURANCE_NOT_FOUND, e.getMessage());
        }
    }

    public InsuranceDetailResponse getInsuranceDetail(Long insuranceId) {
        try{
            log.info(">>>> [InsuranceService] 보험 상세 조회 시작 - insuranceId: {}", insuranceId);
            InsuContractEntity insuranceDetail = insuranceRepository.findById(insuranceId).orElseThrow(() -> BusinessException.of(ErrorCode.INSURANCE_NOT_FOUND));
            return insuranceMapper.toInsuranceResponse(insuranceDetail);
        } catch (BusinessException e){
            throw new BusinessException(ErrorCode.INSURANCE_NOT_FOUND, e.getMessage());
        }
    }

    @Transactional
    public InsuranceDetailResponse registNewInsurance(InsuranceRegistRequest registDTO) {
        try{
            log.info(">>>> [InsuranceService] 보험 등록 시작");
            CarEntity car = carRepository.findById(registDTO.getCarId()).orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));
            InsuContractEntity insurance = InsuContractEntity.builder()
                    .car(car)
                    .startDate(registDTO.getStartDate())
                    .endDate(registDTO.getEndDate())
                    .premium(registDTO.getPremium())
                    .companyName(registDTO.getCompanyName())
                    .insuranceName(registDTO.getInsuranceName())
                    .insuranceStatus(registDTO.getInsuranceStatus())
                    .paymentType(registDTO.getPaymentType())
                    .paymentDay(registDTO.getPaymentDay())
                    .firstPaymentDay(registDTO.getFirstPaymentDay())
                    .build();
            InsuContractEntity newInsurance = insuranceRepository.save(insurance);
            return insuranceMapper.toInsuranceResponse(newInsurance);
        } catch (BusinessException e){
            throw new BusinessException(ErrorCode.CANNOT_REGIST_INSURANCE, e.getMessage());
        }
    }

    @Transactional
    public InsuranceDetailResponse modifyInsuranceInfo(InsuranceRegistRequest updateDTO, Long insuranceId) {
        try{
            log.info(">>>> [InsuranceService] 보험 수정 시작 - insuranceId: {}", insuranceId);
            InsuContractEntity insurance = insuranceRepository.findById(insuranceId).orElseThrow(() -> new BusinessException(ErrorCode.INSURANCE_NOT_FOUND));
            insurance.updateInsuranceInfo(updateDTO);
            return insuranceMapper.toInsuranceResponse(insurance);
        } catch(BusinessException e){
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

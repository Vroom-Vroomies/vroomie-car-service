package com.vroomie.car_service.domain.contract.insurance_contract.service;

import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractStatus;
import com.vroomie.car_service.domain.contract.insurance_contract.dto.request.InsuranceRegistRequest;
import com.vroomie.car_service.domain.contract.insurance_contract.dto.response.InsuranceDetailResponse;
import com.vroomie.car_service.domain.contract.insurance_contract.dto.response.InsuranceSimpleResponse;
import com.vroomie.car_service.domain.contract.insurance_contract.entity.InsuContractEntity;
import com.vroomie.car_service.domain.contract.insurance_contract.enums.InsuranceStatus;
import com.vroomie.car_service.domain.contract.insurance_contract.mapper.InsuranceMapper;
import com.vroomie.car_service.domain.contract.insurance_contract.repository.InsuContractRepository;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarSimpleResponse;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.mapper.CarMapper;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuContractRepository insuranceRepository;
    private final InsuranceMapper insuranceMapper;
    private final CarRepository carRepository;
    private final CarMapper carMapper;

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

    public List<CarSimpleResponse> findCarListInsurable() {
        try{
            log.info(">>>> [InsuranceService] 보험 등록 가능 차량 조회");
            List<CarEntity> carList = carRepository.getCarListInsurable();
            return carMapper.toSimpleResponseList(carList);
        }catch(BusinessException e){
            throw new BusinessException(ErrorCode.NOT_EXIST_CAR_INSURABLE, e.getMessage());
        }
    }

    public void validateRequestRequest(InsuranceRegistRequest requestDTO, List<InsuContractEntity> insuranceList){

        if(!requestDTO.getStartDate().before(requestDTO.getEndDate())){
            throw new BusinessException(ErrorCode.INVALID_INSURANCE_PERIOD);
        }

        if(requestDTO.getPremium().signum() < 0){
            throw new BusinessException(ErrorCode.INVALID_PREMIUM_AMOUNT);
        }

        for(InsuContractEntity insurance: insuranceList){
            if(insurance.getInsuranceName().equals(requestDTO.getInsuranceName()) && insurance.getStartDate() == requestDTO.getStartDate() && insurance.getEndDate() == requestDTO.getEndDate()){
                throw new BusinessException(ErrorCode.DUPLICATED_INSURANCE);
            }
        }
    }

    @Transactional
    public InsuranceDetailResponse registNewInsurance(InsuranceRegistRequest registDTO) {

        List<InsuContractEntity> insuranceList = insuranceRepository.findAllByCarId(registDTO.getCarId());

        validateRequestRequest(registDTO, insuranceList);

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

    public void validateUpdateRequest(InsuranceRegistRequest requestDTO, InsuContractEntity insurance){

        if(insurance.getInsuranceStatus() == InsuranceStatus.EXPIRED){
            throw new BusinessException(ErrorCode.INSURANCE_EXPIRED);
        }

        if(!requestDTO.getStartDate().before(requestDTO.getEndDate())){
            throw new BusinessException(ErrorCode.INVALID_INSURANCE_PERIOD);
        }

        if(requestDTO.getPremium().signum() < 0){
            throw new BusinessException(ErrorCode.INVALID_PREMIUM_AMOUNT);
        }

        if(insurance.getInsuranceName().equals(requestDTO.getInsuranceName()) && insurance.getStartDate() == requestDTO.getStartDate() && insurance.getEndDate() == requestDTO.getEndDate()){
            throw new BusinessException(ErrorCode.DUPLICATED_INSURANCE);
        }

    }

    @Transactional
    public InsuranceDetailResponse modifyInsuranceInfo(InsuranceRegistRequest updateDTO, Long insuranceId) {
        try{
            log.info(">>>> [InsuranceService] 보험 수정 시작 - insuranceId: {}", insuranceId);

            InsuContractEntity insurance = insuranceRepository.findById(insuranceId).orElseThrow(() -> new BusinessException(ErrorCode.INSURANCE_NOT_FOUND));
            validateUpdateRequest(updateDTO, insurance);
            insurance.updateInsuranceInfo(updateDTO);
            return insuranceMapper.toInsuranceResponse(insurance);
        } catch(BusinessException e){
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateContractStatus(){
        try{
            log.info(">>>> [InsuranceService] 보험 상태 변경 시작");
            List<InsuContractEntity> activeInsurances = insuranceRepository.findAllActive();
            LocalDate today = LocalDate.now();
            Date todaySQL = Date.valueOf(today);

            List<InsuContractEntity> expiredInsurances = new ArrayList<>();
            int updateCount = 0;

            for(InsuContractEntity insurance : activeInsurances){
                if(insurance.getEndDate().before(todaySQL)){
                    insurance.updateInsuranceStatus(InsuranceStatus.EXPIRED);
                    expiredInsurances.add(insurance);
                    updateCount++;

                    log.info(">>>> 보험 계약 만료 처리 - ID : {}, 종료일 : {}", insurance.getId(), insurance.getEndDate());
                }
            }

            if (!expiredInsurances.isEmpty()) {
                insuranceRepository.saveAll(expiredInsurances);
                log.info(">>>> 보험 계약 만료 상태 업데이트 완료 - 총 {}건 처리", updateCount);
            } else {
                log.info(">>>> 만료된 보험 계약이 없습니다. 날짜 : {}", todaySQL);
            }
        } catch(BusinessException e){
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

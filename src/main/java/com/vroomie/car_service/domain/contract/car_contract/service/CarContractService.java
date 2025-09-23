package com.vroomie.car_service.domain.contract.car_contract.service;

import com.vroomie.car_service.domain.contract.car_contract.dto.request.*;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.LeaseContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.PurchaseContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.RentContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.LeaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.PurchaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.RentContract;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractStatus;
import com.vroomie.car_service.domain.contract.car_contract.mapper.CarContractMapper;
import com.vroomie.car_service.domain.contract.car_contract.repository.CarContractRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
// @RequiredArgsConstructor : final 키워드가 붙은 필드(필수적인 필드)들에 대한 의존성 주입 가능
@RequiredArgsConstructor
public class CarContractService {

    private final CarContractMapper carContractMapper;
    private final CarContractRepository contractRepository;
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    public Object getContractDetails(Long carId) {

        log.info(">>>> [CarContractService] 계약 상세 정보 조회 시작 - contractId: {}", carId);

        CarContract contract = contractRepository.findByCarId(carId).orElseThrow(() -> new BusinessException(ErrorCode.CONTRACT_NOT_FOUND));
//        System.out.println("😀😀😀contract = " + contract);

        if (contract instanceof LeaseContract) {
            LeaseContractResponse leaseDTO = carContractMapper.toLeaseContractResponse((LeaseContract) contract);
            leaseDTO.calculateAmounts();  // 매퍼가 채운 값 기준으로 계산
            return leaseDTO;
        } else if (contract instanceof RentContract) {
            RentContractResponse rentDTO = carContractMapper.toRentContractResponse((RentContract) contract);
            rentDTO.calculateAmounts();
            return rentDTO;
        } else if (contract instanceof PurchaseContract) {
            PurchaseContractResponse purchaseDTO = carContractMapper.toPurchaseContractResponse((PurchaseContract) contract);
            purchaseDTO.calculateAmounts();
            return purchaseDTO;
        } else {
            throw new BusinessException(ErrorCode.CONTRACT_TYPE_NOT_FOUND);
        }
    }

    public void validateContractRequest(ContractRegistRequest registDTO){

        // 계약 기간 검증
        if(!registDTO.getStartAt().before(registDTO.getEndAt())){
            throw new BusinessException(ErrorCode.INVALID_CONTRACT_PERIOD);
        }

        // 월 사용료 검증
        if(registDTO.getMonthlyFee() == null || registDTO.getMonthlyFee().signum() < 0){
            throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
        }

        if (registDTO instanceof LeaseRegistRequest) {
            if(((LeaseRegistRequest) registDTO).getMonthlyLease() == null || ((LeaseRegistRequest) registDTO).getMonthlyLease().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((LeaseRegistRequest) registDTO).getLeasePeriod() == null || ((LeaseRegistRequest) registDTO).getLeasePeriod() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((LeaseRegistRequest) registDTO).getExcessMileageRate().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((LeaseRegistRequest) registDTO).getOptionPrice().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((LeaseRegistRequest) registDTO).getMileageLimit() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
        }

        if (registDTO instanceof RentRegistRequest) {
            if(((RentRegistRequest) registDTO).getMonthlyRent() == null || ((RentRegistRequest) registDTO).getMonthlyRent().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((RentRegistRequest) registDTO).getDeposit().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
        }

        if (registDTO instanceof PurchaseRegistRequest) {
            if(((PurchaseRegistRequest) registDTO).getPurchasePrice() == null || ((PurchaseRegistRequest) registDTO).getPurchasePrice().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseRegistRequest) registDTO).getDownPayment().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseRegistRequest) registDTO).getLoanAmount().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseRegistRequest) registDTO).getLoanTerm() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseRegistRequest) registDTO).getInterestRate().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseRegistRequest) registDTO).getMonthlyRepayment().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
        }
    }

    public void registNewContract(ContractRegistRequest registDTO) {

        // 입력값 검증
        validateContractRequest(registDTO);
        CarContract contract = null;
        // 계약 시작 일자가 계약 종료 일자보다 빠른지/느린지 판단
        if (registDTO instanceof LeaseRegistRequest) {
            LeaseRegistRequest leaseDTO = (LeaseRegistRequest) registDTO;
            leaseDTO.calculateAmounts();
            contract = carContractMapper.toLeaseContract((LeaseRegistRequest) registDTO);
        } else if (registDTO instanceof RentRegistRequest) {
            RentRegistRequest rentDTO = (RentRegistRequest) registDTO;
            rentDTO.calculateAmount();
            contract = carContractMapper.toRentContract((RentRegistRequest) registDTO);
        } else if (registDTO instanceof PurchaseRegistRequest) {
            PurchaseRegistRequest purchaseDTO = (PurchaseRegistRequest)registDTO;
            purchaseDTO.calculateAmount();
            contract = carContractMapper.toPurchaseContract((PurchaseRegistRequest) registDTO);
        }

        if (contract != null) {
            contractRepository.save(contract);
        } else {
            throw new BusinessException(ErrorCode.CONTRACT_TYPE_NOT_FOUND);
        }

    }

    public void validUpdateRequest(ContractUpdateRequest updateDTO, CarContract existingContract){

        if(existingContract.getContractStatus().equals(ContractStatus.EXPIRED)){
            throw new BusinessException(ErrorCode.CONTRACT_EXPIRED);
        }

        if(updateDTO.getMonthlyFee() == null || updateDTO.getMonthlyFee().signum() < 0){
            throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
        }

        if (existingContract instanceof LeaseContract && updateDTO instanceof LeaseUpdateRequest) {
            if(((LeaseUpdateRequest) updateDTO).getMonthlyLease() == null || ((LeaseUpdateRequest) updateDTO).getMonthlyLease().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((LeaseUpdateRequest) updateDTO).getLeasePeriod() == null || ((LeaseUpdateRequest) updateDTO).getLeasePeriod() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((LeaseUpdateRequest) updateDTO).getExcessMileageRate().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((LeaseUpdateRequest) updateDTO).getOptionPrice().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((LeaseUpdateRequest) updateDTO).getMileageLimit() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
        }

        if (existingContract instanceof RentContract && updateDTO instanceof RentUpdateRequest) {
            if(((RentUpdateRequest) updateDTO).getMonthlyRent() == null || ((RentUpdateRequest) updateDTO).getMonthlyRent().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((RentUpdateRequest) updateDTO).getDeposit().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
        }

        if (existingContract instanceof PurchaseContract && updateDTO instanceof PurchaseUpdateRequest) {
            if(((PurchaseUpdateRequest) updateDTO).getPurchasePrice() == null || ((PurchaseUpdateRequest) updateDTO).getPurchasePrice().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseUpdateRequest) updateDTO).getDownPayment().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseUpdateRequest) updateDTO).getLoanAmount().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseUpdateRequest) updateDTO).getLoanTerm() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseUpdateRequest) updateDTO).getInterestRate().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }
            if(((PurchaseUpdateRequest) updateDTO).getMonthlyRepayment().signum() < 0){
                throw new BusinessException(ErrorCode.INVALID_CONTRACT_AMOUNT);
            }

        }
    }

    @Transactional
    public void modifyContractInfo(ContractUpdateRequest updateDTO, Long contractId) {

        /** 프론트에서 contract에 따라 입력하는 값이 달라지기때문에 애초에 contractType을 잘못입력할 일이 없으니 contractType 수정 안함. **/

        // 1. contractId로 기존 계약 엔티티를 조회합니다.
        CarContract existingContract = contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTRACT_NOT_FOUND));

        // 업데이트 데이터 유효성 검증
        validUpdateRequest(updateDTO, existingContract);

        if (existingContract instanceof LeaseContract && updateDTO instanceof LeaseUpdateRequest) {
            ((LeaseContract) existingContract).updateLeaseContract((LeaseUpdateRequest) updateDTO);
        } else if (existingContract instanceof RentContract && updateDTO instanceof RentUpdateRequest) {
            // RentContract에도 유사한 updateRentContract 메서드가 있다고 가정
            ((RentContract) existingContract).updateRentContract((RentUpdateRequest) updateDTO);
        } else if (existingContract instanceof PurchaseContract && updateDTO instanceof PurchaseUpdateRequest) {
            // PurchaseContract에도 유사한 updatePurchaseContract 메서드가 있다고 가정
            ((PurchaseContract) existingContract).updatePurchaseContract((PurchaseUpdateRequest) updateDTO);
        } else {
            log.warn("계약 ID {} 에 대한 업데이트 DTO 타입 불일치 또는 예상치 못한 계약 타입: existingContract type = {}, updateDTO type = {}",
                    contractId, existingContract.getClass().getSimpleName(), updateDTO.getClass().getSimpleName());
        }

        contractRepository.save(existingContract);
    }

    public List<CarSimpleResponse> findAllCarsContractable() {
        try{
            log.info(">>>> [CarContractService] 계약 가능한 차량 조회 시작");
            List<CarEntity> carList = carRepository.findAllCarsContractable();
            return carMapper.toSimpleResponseList(carList);
        } catch (BusinessException e) {
            throw new BusinessException(ErrorCode.NOT_EXIST_CAR_CONTRACTABLE);
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateContractStatus(){
        try{
            log.info(">>>> [CarContractService] 계약 상태 변경 시작");
            List<CarContract> activeContracts = contractRepository.findAllActive();
            LocalDate today = LocalDate.now();
            Date todaySQL = Date.valueOf(today);

            List<CarContract> expirecContracts = new ArrayList<>();
            int updateCount = 0;

            for(CarContract contract : activeContracts){
                if(contract.getEndAt().before(todaySQL)){
                    contract.updateContractStatus(ContractStatus.EXPIRED);
                    expirecContracts.add(contract);
                    updateCount++;

                    log.info(">>>> 차량 계약 만료 처리 - ID : {}, 종료일 : {}", contract.getId(), contract.getEndAt());
                }
            }

            if(!expirecContracts.isEmpty()){
                contractRepository.saveAll(expirecContracts);
                log.info(">>>> 차량 계약 만료 상태 없데이트 완료 - 총 {}건 처리", updateCount);
            } else {
                log.info(">>>> 만료된 차량 계약이 없습니다. 날짜 : {}", todaySQL);
            }
        } catch (BusinessException e){
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }


    /** 계약 삭제 시 차량 유지비용에 영향을 미치기 때문에 삭제 불가. **/
}
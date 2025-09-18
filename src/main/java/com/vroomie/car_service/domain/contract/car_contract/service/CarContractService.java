package com.vroomie.car_service.domain.contract.car_contract.service;

import com.vroomie.car_service.domain.contract.car_contract.dto.request.*;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.LeaseContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.PurchaseContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.dto.response.RentContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.LeaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.PurchaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.RentContract;
import com.vroomie.car_service.domain.contract.car_contract.mapper.CarContractMapper;
import com.vroomie.car_service.domain.contract.car_contract.repository.CarContractRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
// @RequiredArgsConstructor : final 키워드가 붙은 필드(필수적인 필드)들에 대한 의존성 주입 가능
@RequiredArgsConstructor
public class CarContractService {

    private final CarContractMapper carContractMapper;
    private final CarContractRepository contractRepository;

    public Object getContractDetails(Long carId) {

        log.info(">>>> [CarContractService] 계약 상세 정보 조회 시작 - contractId: {}", carId);

        CarContract contract = contractRepository.findByCarId(carId).orElseThrow(() -> new BusinessException(ErrorCode.CONTRACT_NOT_FOUND));
        System.out.println("😀😀😀contract = " + contract);

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

    public void registNewContract(ContractRegistRequest registDTO) {

        System.out.println("✅✅✅contract = " + registDTO);
        CarContract contract = null;
        if (registDTO instanceof LeaseRegistRequest) {
            contract = carContractMapper.toLeaseContract((LeaseRegistRequest) registDTO);
        } else if (registDTO instanceof RentRegistRequest) {
            contract = carContractMapper.toRentContract((RentRegistRequest) registDTO);
        } else if (registDTO instanceof PurchaseRegistRequest) {
            contract = carContractMapper.toPurchaseContract((PurchaseRegistRequest) registDTO);
        }

        if (contract != null) {
            contractRepository.save(contract);
        } else {
            throw new BusinessException(ErrorCode.CONTRACT_TYPE_NOT_FOUND);
        }

    }

    @Transactional
    public void modifyContractInfo(ContractUpdateRequest updateDTO, Long contractId) {

        // 1. contractId로 기존 계약 엔티티를 조회합니다.
        CarContract existingContract = contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTRACT_NOT_FOUND));

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
    }
}
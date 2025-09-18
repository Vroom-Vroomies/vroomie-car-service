package com.vroomie.car_service.domain.contract.car_contract.service;

import com.vroomie.car_service.domain.contract.car_contract.dto.request.ContractRegistRequest;
import com.vroomie.car_service.domain.contract.car_contract.dto.request.LeaseRegistRequest;
import com.vroomie.car_service.domain.contract.car_contract.dto.request.PurchaseRegistRequest;
import com.vroomie.car_service.domain.contract.car_contract.dto.request.RentRegistRequest;
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
import org.springframework.lang.Contract;
import org.springframework.stereotype.Service;

@Slf4j
@Service
// @RequiredArgsConstructor : final 키워드가 붙은 필드(필수적인 필드)들에 대한 의존성 주입 가능
@RequiredArgsConstructor
public class CarContractService {

    private final CarContractMapper carContractMapper;
    private final CarContractRepository contractRepository;

    public Object getContractDetails(Long carId) {

        log.info(">>>> [CarContractService] 계약 상세 정보 조회 시작 - contractId: {}",carId);

        CarContract contract = contractRepository.findByCarId(carId).orElseThrow(() -> new BusinessException(ErrorCode.CONTRACT_NOT_FOUND));
        System.out.println("😀😀😀contract = " + contract);

        if(contract instanceof LeaseContract){
            LeaseContractResponse leaseDTO = carContractMapper.toLeaseContractResponse((LeaseContract) contract);
            leaseDTO.calculateAmounts();  // 매퍼가 채운 값 기준으로 계산
            return leaseDTO;
        } else if(contract instanceof RentContract){
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
        if(registDTO instanceof LeaseRegistRequest){
            contract = carContractMapper.toLeaseContract((LeaseRegistRequest) registDTO);
        } else if(registDTO instanceof RentRegistRequest){
            contract = carContractMapper.toRentContract((RentRegistRequest) registDTO);
        } else if(registDTO instanceof PurchaseRegistRequest){
            contract = carContractMapper.toPurchaseContract((PurchaseRegistRequest) registDTO);
        }

        if(contract != null){
            contractRepository.save(contract);
        } else {
            throw new BusinessException(ErrorCode.CONTRACT_TYPE_NOT_FOUND);
        }

    }
}
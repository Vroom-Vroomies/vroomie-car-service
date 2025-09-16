package com.vroomie.car_service.domain.contract.car_contract.service;

import com.vroomie.car_service.domain.contract.car_contract.dto.response.CarContractResponse;
import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.LeaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.PurchaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.RentContract;
import com.vroomie.car_service.domain.contract.car_contract.mapper.CarContractMapper;
import com.vroomie.car_service.domain.contract.car_contract.repository.CarContractRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CarContractService {

    private final CarContractMapper carContractMapper;
    private CarContractRepository contractRepository;

    public CarContractService(CarContractRepository contractRepository, CarContractMapper carContractMapper){
        this.contractRepository = contractRepository;
        this.carContractMapper = carContractMapper;
    }

    public Object getContractDetails(Long contractId) {

        log.info(">>>> [CarContractService] 계약 상세 정보 조회 시작 - contractId: {}",contractId);

        CarContract contract = contractRepository.findById(contractId).orElseThrow(() -> new BusinessException(ErrorCode.CONTRACT_NOT_FOUND));

        if(contract instanceof LeaseContract){
            return carContractMapper.toLeaseContractResponse((LeaseContract) contract);
        } else if(contract instanceof RentContract){
            return carContractMapper.toRentContractResponse((RentContract) contract);
        } else if (contract instanceof PurchaseContract) {
            return carContractMapper.toPurchaseContractResponse((PurchaseContract) contract);
        } else {
            throw new BusinessException(ErrorCode.CONTRACT_TYPE_NOT_FOUND);
        }
    }
}


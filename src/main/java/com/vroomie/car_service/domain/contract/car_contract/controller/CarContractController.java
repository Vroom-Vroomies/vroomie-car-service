package com.vroomie.car_service.domain.contract.car_contract.controller;

import com.vroomie.car_service.domain.contract.car_contract.service.CarContractService;
import com.vroomie.car_service.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/contract")
public class CarContractController {

    private CarContractService contractService;

    public CarContractController(CarContractService contractService){
        this.contractService = contractService;
    }

    /** 차량 계약 상세 조회 **/
    @GetMapping("/{contractId}")
    public ApiResponse<?> selectCarContractDetails(@PathVariable(value = "contractId") Long contractId){

        log.info(">>>> [CarContractController] 차량 계약 상세 조회 시작 - contractId: {}", contractId);
        Object contractDTO = contractService.getContractDetails(contractId);

        return ApiResponse.success(contractDTO);
    }
}

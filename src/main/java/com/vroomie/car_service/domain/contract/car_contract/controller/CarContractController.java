package com.vroomie.car_service.domain.contract.car_contract.controller;

import com.vroomie.car_service.domain.contract.car_contract.dto.request.ContractRegistRequest;
import com.vroomie.car_service.domain.contract.car_contract.dto.request.ContractUpdateRequest;
import com.vroomie.car_service.domain.contract.car_contract.service.CarContractService;
import com.vroomie.car_service.domain.fleet.car.dto.response.CarSimpleResponse;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import com.vroomie.car_service.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/contract")
public class CarContractController {

    private final CarContractService contractService;

    /** 차량 계약 상세 조회 **/
    @GetMapping("/{carId}")
    public ApiResponse<?> selectCarContractDetails(@PathVariable(value = "carId") Long carId){

        log.info(">>>> [CarContractController] 차량 계약 상세 조회 시작 - carId: {}", carId);
        Object contractDTO = contractService.getContractDetails(carId);

        return ApiResponse.success(contractDTO);
    }

    /** 계약 등록 가능한 차량 조회 **/
    @GetMapping("")
    public ApiResponse<?> selectAllCarsContractable(){
        try{
            log.info(">>>> [CarContractController] 계약 가능한 차량 조회 시작");
            List<CarSimpleResponse> carList = contractService.findAllCarsContractable();
            return ApiResponse.success(carList);
        } catch (BusinessException e){
            throw e;
        }
    }

    /** 차량 계약 등록 **/
    @PostMapping("")
    public ApiResponse<?> insertCarContractDetails(@RequestBody ContractRegistRequest dto){

        log.info(">>>> [CarContractController] 차량 계약 등록 시작 - carId: {}", dto.getCarId());
        try {
            contractService.registNewContract(dto);
        } catch (BusinessException e) {
            throw e;
        }

        return ApiResponse.success("차량 계약 등록 성공");
    }

    /** 차량 계약 정보 수정 **/
    @PutMapping("/{contractId}")
    public ApiResponse<?> updateCarContractDetails(@RequestBody ContractUpdateRequest dto, @PathVariable("contractId") Long contractId){

        log.info(">>>> [CarContractController] 차량 계약 수정 시작 - contractId: {}", contractId);
        System.out.println("😀😀😀😀" + dto.getContractType());
        System.out.println("😀😀😀😀" + dto.getProvider());

        try{
            contractService.modifyContractInfo(dto, contractId);
        } catch(BusinessException e){
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return ApiResponse.success("차량 계약 정보 수정 성공");
    }

}

package com.vroomie.car_service.domain.contract.insurance_contract.controller;

import com.vroomie.car_service.domain.contract.insurance_contract.dto.request.InsuranceRegistRequest;
import com.vroomie.car_service.domain.contract.insurance_contract.dto.response.InsuranceDetailResponse;
import com.vroomie.car_service.domain.contract.insurance_contract.dto.response.InsuranceSimpleResponse;
import com.vroomie.car_service.domain.contract.insurance_contract.service.InsuranceService;
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
@RequestMapping("/insurance")
public class InsuranceController {

    private final InsuranceService insuranceService;

    /** 보험 리스트 조회 **/
    @GetMapping("/{carId}")
    public ApiResponse<?> getCarInsuranceList(@PathVariable("carId") Long carId){

        try{
            log.info(">>>> [InsuranceController] 차량 보험 목록 조회 시작 - carId: {}", carId);
            List<InsuranceSimpleResponse> insuranceList =  insuranceService.getAllInsuranceList(carId);
            return ApiResponse.success(insuranceList);
        } catch(BusinessException e){
            throw e;
        }
    }

    /** 보험 상세 조회 **/
    @GetMapping("/detail/{insuranceId}")
    public ApiResponse<?> getCarInsuranceDetail(@PathVariable("insuranceId") Long insuranceId){
        try{
            log.info(">>>> [InsuranceController] 차량 보험 상세 조회 시간 - insuranceId: {}", insuranceId);
            InsuranceDetailResponse insuranceDetail = insuranceService.getInsuranceDetail(insuranceId);
            return ApiResponse.success(insuranceDetail);
        } catch (BusinessException e){
            throw e;
        }
    }

    /** 보험 등록 가능 차량 조회 - 차량 상태가 ACTIVE인 경우 **/
    @GetMapping("")
    public ApiResponse<?> getCarListInsurable(){
        try{
            log.info(">>>> [InsuranceController] 보험 등록 가능 차량 조회 시작");
            List<CarSimpleResponse> carList = insuranceService.findCarListInsurable();
            if(carList.isEmpty()){
                return ApiResponse.success("보험 등록 가능한 차량이 없습니다.");
            } else {
                return ApiResponse.success(carList);
            }
        } catch(BusinessException e){
            throw e;
        }
    }

    /** 보험 등록 **/
    @PostMapping("")
    public ApiResponse<?> registNewInsurance(@RequestBody InsuranceRegistRequest registDTO){
        try{
            log.info(">>>> [InsuranceController] 차량 보험 등록 시작");
            InsuranceDetailResponse insuranceDetail = insuranceService.registNewInsurance(registDTO);
            return ApiResponse.success(insuranceDetail);
        } catch (BusinessException e){
            throw e;
        }
    }

    /** 보험 수정 **/
    @PutMapping("/{insuranceId}")
    public ApiResponse<?> updateInsuranceInfo(@RequestBody InsuranceRegistRequest updateDTO, @PathVariable("insuranceId") Long insuranceId){
        try{
            log.info(">>>> [InsuranceController] 차량 보험 수정 시작 - insuranceId: {}", insuranceId);
            InsuranceDetailResponse updatedInsurance = insuranceService.modifyInsuranceInfo(updateDTO, insuranceId);

            if(updatedInsurance != null){
                return ApiResponse.success(updatedInsurance);
            } else {
                throw new BusinessException(ErrorCode.INSURANCE_NOT_FOUND);
            }
        } catch (BusinessException e){
            throw e;
        }
    }
}

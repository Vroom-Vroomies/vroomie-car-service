package com.vroomie.car_service.domain.contract.actual_cost.service;

import com.vroomie.car_service.domain.contract.actual_cost.dto.request.ActualCostRequestDTO;
import com.vroomie.car_service.domain.contract.actual_cost.dto.request.ActualInsuranceCostRequestDTO;
import com.vroomie.car_service.domain.contract.actual_cost.entity.ActualCost;
import com.vroomie.car_service.domain.contract.actual_cost.entity.ContractCostType;
import com.vroomie.car_service.domain.contract.actual_cost.repository.ActualCostRepository;
import com.vroomie.car_service.domain.contract.actual_cost.repository.CostTypeRepository;
import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.LeaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.PurchaseContract;
import com.vroomie.car_service.domain.contract.car_contract.entity.RentContract;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractStatus;
import com.vroomie.car_service.domain.contract.car_contract.repository.CarContractRepository;
import com.vroomie.car_service.domain.contract.insurance_contract.entity.InsuContractEntity;
import com.vroomie.car_service.domain.contract.insurance_contract.enums.InsuranceStatus;
import com.vroomie.car_service.domain.contract.insurance_contract.repository.InsuContractRepository;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ActualCostService {

    private final ActualCostRepository actualCostRepository;
    private final CarContractRepository contractRepository;
    private final InsuContractRepository insuranceRepository;
    private final InsuContractRepository insuContractRepository;
    private final CarRepository carRepository;
    private final CostTypeRepository costTypeRepository;

//    @Scheduled(fixedRate = 20000)
//    public void runTask() {
//        log.info("스케줄 실행됨!");
//    }

    /** 계약 정보 기반으로 자동 생성 **/
    // 크론 표현식
    // 이 표현식은 초, 분, 시, 일(월), 월, 요일 순서로,
    // 각각 0초, 0분, 1시에 1일에만 실행되도록 설정하고
    // 요일은 무시하는 ?로 지정하여 매월 1일 새벽 1시에 작동합니다.
    @Scheduled(cron = "0 0 2 * * ?")    // 매일 새벽 1시에 작업 실행
//    @Scheduled(cron = "0 */1 * * * ?")  // 테스트용 매 분
    public void generateMonthlyCosts(){
        List<CarContract> activeContracts = contractRepository.findAllActive();
        List<InsuContractEntity> activeInsurances = insuranceRepository.findAllActive();

        for(CarContract contract : activeContracts){
            if(contract.getContractStatus() != ContractStatus.EXPIRED){
                if (contract instanceof LeaseContract) {
                    generateLeaseCost((LeaseContract) contract);
                } else if (contract instanceof RentContract) {
                    generateRentCost((RentContract)contract);
                } else if (contract instanceof PurchaseContract){
                    if(((PurchaseContract) contract).getLoanAmount() != null){
                        generatePurchaseCost((PurchaseContract)contract);
                    }
                }
            }
        }

        for(InsuContractEntity insurance : activeInsurances){
            if(insurance.getInsuranceStatus() != InsuranceStatus.EXPIRED){
                generateInsuranceCost(insurance);
            }
        }
    }

    private void generateLeaseCost(LeaseContract contract) {
        // 리스 총 주행거리 초과 여부 확인 후 계산
        CarEntity car = carRepository.findById(contract.getCar().getId()).orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));
        Long totalMileage = car.getTotalMileage();
        Long mileageLimit = contract.getMileageLimit();
        BigDecimal totalMonthlyCost;
        ContractCostType contractType = costTypeRepository.findByContractTypeName("리스");
        Long costType = contractType.getId();

        if(totalMileage != null && mileageLimit != null && totalMileage > mileageLimit){
            Long excessMileage = totalMileage - mileageLimit;
            BigDecimal excessMileageCost = contract.getExcessMileageRate().multiply(new BigDecimal(excessMileage));
            totalMonthlyCost = contract.getMonthlyLease().add(excessMileageCost);
            log.info("초과 주행거리 발생 - 계약ID: {}, 초과거리: {}km, 추가비용: {}",
                    contract.getId(), excessMileage, excessMileageCost);
        } else {
            // 정상 주행거리
            totalMonthlyCost = contract.getMonthlyLease();
        }
        // monthlyFee
        ActualCostRequestDTO costDTO = ActualCostRequestDTO.builder()
            .carId(car.getId())
            .contractId(contract.getId())
            .costTypeId(costType)
            .amount(totalMonthlyCost)
            .costDate(convertPaymentDayToDateSafe(contract.getPaymentDay()))
            .description(contractType.getDescription())
            .build();
        ActualCost actualCost = convertDtoToEntity(costDTO, car, contract);
        actualCostRepository.save(actualCost);
        log.info("리스 비용 생성 - 계약ID: {}", contract.getId());
    }

    private void generateRentCost(RentContract contract) {
        CarEntity car = carRepository.findById(contract.getCar().getId()).orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));
        ContractCostType contractType = costTypeRepository.findByContractTypeName("렌탈");
        Long costType = contractType.getId();

        ActualCostRequestDTO costDTO = ActualCostRequestDTO.builder()
                .carId(car.getId())
                .contractId(contract.getId())
                .costTypeId(costType)
                .amount(contract.getMonthlyFee())
                .costDate(convertPaymentDayToDateSafe(contract.getPaymentDay()))
                .description(contractType.getDescription())
                .build();
        ActualCost actualCost = convertDtoToEntity(costDTO, car, contract);
        actualCostRepository.save(actualCost);
        log.info("렌트 비용 생성 - 계약ID: {}", contract.getId());

    }

    private void generatePurchaseCost(PurchaseContract contract) {
        CarEntity car = carRepository.findById(contract.getCar().getId()).orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));
        ContractCostType contractType = costTypeRepository.findByContractTypeName("대출");
        Long costType = contractType.getId();

        if(contract.getLoanAmount() != null){
            ActualCostRequestDTO costDTO = ActualCostRequestDTO.builder()
                    .carId(car.getId())
                    .contractId(contract.getId())
                    .costTypeId(costType)
                    .amount(contract.getMonthlyRepayment())
                    .costDate(convertPaymentDayToDateSafe(contract.getPaymentDay()))
                    .description(contractType.getDescription())
                    .build();
            ActualCost actualCost = convertDtoToEntity(costDTO, car, contract);
            actualCostRepository.save(actualCost);
            log.info("구매 비용 생성 - 계약ID: {}", contract.getId());

        }
    }

    private void generateInsuranceCost(InsuContractEntity insurance) {
        ContractCostType contractType = costTypeRepository.findByContractTypeName("보험료");
        Long costType = contractType.getId();

        ActualInsuranceCostRequestDTO costDTO = ActualInsuranceCostRequestDTO.builder()
                .carId(insurance.getCar().getId())
                .insuranceId(insurance.getId())
                .costTypeId(costType)
                .amount(insurance.getPremium())
                .costDate(convertPaymentDayToDateSafe(insurance.getPaymentDay()))
                .description(contractType.getDescription())
                .build();
        ActualCost actualCost = convertDtoToEntity(costDTO, insurance.getCar(), insurance);
        actualCostRepository.save(actualCost);
    }

    private Date convertPaymentDayToDateSafe(Integer paymentDay) {
        YearMonth currentMonth = YearMonth.now();
        int lastDayOfMonth = currentMonth.lengthOfMonth();

        // paymentDay가 해당 월의 마지막 일을 초과하면 마지막 일로 설정
        int actualDay = Math.min(paymentDay, lastDayOfMonth);

        LocalDate paymentDate = LocalDate.now().withDayOfMonth(actualDay);
        return Date.valueOf(paymentDate);
    }

    // DTO를 Entity로 변환하는 메서드 (추가 필요)
    private ActualCost convertDtoToEntity(ActualCostRequestDTO dto, CarEntity car, CarContract contract) {
        ContractCostType costType = costTypeRepository.findById(dto.getCostTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COST_TYPE_NOT_FOUND));

        return ActualCost.builder()
                .car(car)
                .contract(contract)
                .costType(costType)
                .amount(dto.getAmount())
                .costDate(dto.getCostDate())
                .description(dto.getDescription())
                .build();
    }

    private ActualCost convertDtoToEntity(ActualInsuranceCostRequestDTO dto, CarEntity car, InsuContractEntity insurance) {
        ContractCostType costType = costTypeRepository.findById(dto.getCostTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COST_TYPE_NOT_FOUND));

        return ActualCost.builder()
                .car(car)
                .insurance(insurance)
                .costType(costType)
                .amount(dto.getAmount())
                .costDate(dto.getCostDate())
                .description(dto.getDescription())
                .build();
    }
}

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
    public void generateDailyCosts(){
        LocalDate today = LocalDate.now();
        int todayDayOfMonth = today.getDayOfMonth();

        // 오늘이 납부일인 계약/보험들만 처리
        List<CarContract> todayPaymentContracts  = contractRepository.findActiveContractsByPaymentDay(todayDayOfMonth);
        List<InsuContractEntity> todayPaymentInsurances  = insuranceRepository.findAllActiveInsurancesByPaymentDay(todayDayOfMonth);

        for(CarContract contract : todayPaymentContracts){
            // 이미 이번 달에 생성했는지 확인
            if(!isAlreadyGeneratedThisMonth(contract.getId(), today)){
                if (contract instanceof LeaseContract) {
                    generateLeaseCost((LeaseContract) contract);
                } else if (contract instanceof RentContract) {
                    generateRentCost((RentContract)contract);
                } else if (contract instanceof PurchaseContract){
                    PurchaseContract purchaseContract = (PurchaseContract) contract;
                    if(purchaseContract.getLoanAmount() != null){
                        // 대출 있는 경우: 매월 상환금
                        generatePurchaseCost(purchaseContract);
                    } else {
                        // 대출 없는 경우: firstPaymentDay에 구매가격 한번만
                        generatePurchaseOneTimeCost(purchaseContract);
                    }
                }
            }
        }

        for(InsuContractEntity insurance : todayPaymentInsurances){
            if(!isAlreadyGeneratedThisMonth(insurance.getId(), today)){
                generateInsuranceCost(insurance);
            }
        }
    }

    // 이번 달에 이미 생성했는지 확인
    private boolean isAlreadyGeneratedThisMonth(Long id, LocalDate today) {
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        return actualCostRepository.existsByContractIdAndCostDateBetween(id, Date.valueOf(startOfMonth), Date.valueOf(endOfMonth));
    }

    // 리스
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

    // 렌트
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

    // 구매 - 대출금 있는 경우
    private void generatePurchaseCost(PurchaseContract contract) {
        CarEntity car = carRepository.findById(contract.getCar().getId()).orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));
        ContractCostType contractType = costTypeRepository.findByContractTypeName("대출");
        Long costType = contractType.getId();

        LocalDate today = LocalDate.now();
        LocalDate firstPaymentDate = contract.getFirstPaymentDay().toLocalDate();

        BigDecimal totalAmount;
        String description;

        // 첫 달인지 확인 (연-월이 같은지 비교)
        if (today.getYear() == firstPaymentDate.getYear() && today.getMonth() == firstPaymentDate.getMonth()){

            // 첫 달 : 계약금 + 월상환금
            totalAmount = contract.getMonthlyRepayment();
            if(contract.getDownPayment() != null){
                totalAmount = totalAmount.add(contract.getDownPayment());
                description = "대출 월 상환금 + 계약금";
            } else {
                description = "대출 월 상환금";
            }
            log.info("첫 달 구매 비용 생성 - 계약ID : {}, 계약금 포함: {}", contract.getId(), totalAmount);

        // 두 번째 달 부터 : 월 상환금만
        } else {
            totalAmount = contract.getMonthlyRepayment();
            description = "대출 월 상환금";
        }

        ActualCostRequestDTO costDTO = ActualCostRequestDTO.builder()
                .carId(car.getId())
                .contractId(contract.getId())
                .costTypeId(costType)
                .amount(totalAmount)
                .costDate(convertPaymentDayToDateSafe(contract.getPaymentDay()))
                .description(contractType.getDescription())
                .build();
        ActualCost actualCost = convertDtoToEntity(costDTO, car, contract);
        actualCostRepository.save(actualCost);
        log.info("대출 구매 비용 생성 - 계약ID: {}, 금액: {}", contract.getId(), totalAmount);

    }

    // 구매 - 대출금 없는 경우(일회성)
    private void generatePurchaseOneTimeCost(PurchaseContract contract) {

        // 이미 이번 달에 일회성 결제를 했는지 확인
        LocalDate today = LocalDate.now();
        if(!isAlreadyGeneratedThisMonth(contract.getId(), today)){
            log.info("이미 일회성 구매 비용이 생성됨 - 계약ID: {}", contract.getId());
        }

        CarEntity car = carRepository.findById(contract.getCar().getId()).orElseThrow(() -> new BusinessException(ErrorCode.CAR_NOT_FOUND));
        ContractCostType contractType = costTypeRepository.findByContractTypeName("대출");
        Long costType = contractType.getId();

        BigDecimal totalAmount = contract.getPurchasePrice();
        String descriptiption = "구매 가격";

        // 계약금이 있을 경우 - 합산
        if(contract.getDownPayment() != null){
            totalAmount = totalAmount.add(contract.getDownPayment());
            descriptiption = "계약금 + 구매 가격";
        }

        ActualCostRequestDTO costDTO = ActualCostRequestDTO.builder()
                .carId(car.getId())
                .contractId(contract.getId())
                .costTypeId(costType)
                .amount(totalAmount)
                .costDate(convertPaymentDayToDateSafe(contract.getPaymentDay()))
                .description(descriptiption)
                .build();
        ActualCost actualCost = convertDtoToEntity(costDTO, car, contract);
        actualCostRepository.save(actualCost);
        log.info("일회성 구매 비용 생성 - 계약ID : {}", contract.getId());
    }

    // 보험
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

    // 월말 처리 (29, 30, 31일 납부일 케이스)
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

    // 보정 스케줄러 -> 누락된 건이 있을 경우
    @Scheduled(cron = "0 0 3 1 * ?")
    public void correctMissedCosts(){
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        YearMonth lastYearMonth = YearMonth.from(lastMonth);
        LocalDate checkDate = lastYearMonth.atDay(1);

        // 지날 달에 생성되지 않은 활성 계약들을 찾아서 보정
        List<CarContract> activeContracts = contractRepository.findAllActive();
        List<InsuContractEntity> activeInsurances = insuranceRepository.findAllActive();

        for(CarContract contract : activeContracts){
            if(!isAlreadyGeneratedThisMonth(contract.getId(), checkDate)){
                log.warn("누락된 비용 발견 - 계약ID : {}, 대상월 : {}", contract.getId(), checkDate);
                if (contract instanceof LeaseContract) {
                    generateLeaseCost((LeaseContract) contract);
                } else if (contract instanceof RentContract) {
                    generateRentCost((RentContract)contract);
                } else if (contract instanceof PurchaseContract){
                    PurchaseContract purchaseContract = (PurchaseContract) contract;
                    if(purchaseContract.getLoanAmount() != null){
                        // 대출 있는 경우: 매월 상환금
                        generatePurchaseCost(purchaseContract);
                    } else {
                        // 대출 없는 경우: firstPaymentDay에 구매가격 한번만
                        generatePurchaseOneTimeCost(purchaseContract);
                    }
                }
            }
        }

        for(InsuContractEntity insurance : activeInsurances){
            if(!isAlreadyGeneratedThisMonth(insurance.getId(), checkDate)){
                log.warn("누락된 비용 발견 - 보험ID : {}, 대상월 : {}", insurance.getId(), checkDate);
                generateInsuranceCost(insurance);
            }
        }
    }
}

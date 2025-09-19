package com.vroomie.car_service.domain.contract.insurance_contract.mapper;

import com.vroomie.car_service.domain.contract.insurance_contract.dto.response.InsuranceDetailResponse;
import com.vroomie.car_service.domain.contract.insurance_contract.dto.response.InsuranceSimpleResponse;
import com.vroomie.car_service.domain.contract.insurance_contract.entity.InsuContractEntity;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InsuranceMapper {

    // MapStruct에서 리스트를 매핑할 때 단일 객체 매핑 메서드를 참조함.
    // toInsuranceResponseList()로 데이터를 잘 매핑하기 위해선 toInsuranceSimpleResponse()가 필수로 있어야 함.
    @Mapping(source = "id", target = "insuranceId")
    @Mapping(source = "car.id", target = "carId")
    InsuranceSimpleResponse toInsuranceSimpleResponse(InsuContractEntity entity);

    // @InheritConfiguration : name 속성으로 지정한 메소드의 매핑 설정을 그대로 사용할 수 있음.
    @InheritConfiguration(name = "toInsuranceSimpleResponse")
    @Mapping(source = "id", target = "insuranceId")
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(source = "paymentDay", target = "paymentDay")
    @Mapping(source = "firstPaymentDay", target = "firstPaymentDay")
    InsuranceDetailResponse toInsuranceResponse(InsuContractEntity entity);


    @InheritConfiguration(name = "toInsuranceSimpleResponse")
    @Mapping(target = "carNumber", ignore = true)
    List<InsuranceSimpleResponse> toInsuranceResponseList(List<InsuContractEntity> entityList);

}

package com.vroomie.car_service.domain.fleet.accident.mapper;

import com.vroomie.car_service.domain.fleet.accident.dto.AccidentCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentDetailDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentListResponseDTO;
import com.vroomie.car_service.domain.fleet.accident.entity.AccidentEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccidentMapper {

    @Mappings({
            @Mapping(source = "car.id", target = "carId"),
            @Mapping(source = "employee.email", target = "empEmail"),
            @Mapping(source = "occurredAt", target = "date"),
            @Mapping(source = "saved", target = "save")
    })
    AccidentDetailDTO toDetailResponse(AccidentEntity accident);

    @Mappings({
            @Mapping(source = "car.id", target = "carId"),
            @Mapping(source = "employee.email", target = "empEmail"),
            @Mapping(source = "occurredAt", target = "date"),
            @Mapping(source = "saved", target = "save")
    })
    AccidentListResponseDTO toListResponseDTO(AccidentEntity accident);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(AccidentCreateRequestDTO req, @MappingTarget AccidentEntity accident);
}

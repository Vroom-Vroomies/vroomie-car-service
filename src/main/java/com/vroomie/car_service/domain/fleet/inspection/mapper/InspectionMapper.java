package com.vroomie.car_service.domain.fleet.inspection.mapper;

import com.vroomie.car_service.domain.fleet.inspection.dto.InspectionCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.inspection.dto.InspectionDetailDTO;
import com.vroomie.car_service.domain.fleet.inspection.entity.InspectionEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InspectionMapper {

    @Mappings({
            @Mapping(source = "car.id", target = "carId"),
            @Mapping(source = "car.number", target = "carNumber"),
            @Mapping(source = "employee.name", target = "createdBy")
            })
    InspectionDetailDTO toDetailDTO(InspectionEntity inspection);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInspection(InspectionCreateRequestDTO req, @MappingTarget InspectionEntity inspection);
}

package com.vroomie.car_service.domain.fleet.inspection.mapper;

import com.vroomie.car_service.domain.fleet.inspection.dto.InspectionDetailDTO;
import com.vroomie.car_service.domain.fleet.inspection.entity.InspectionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface InspectionMapper {

    @Mappings({
            @Mapping(source = "car.id", target = "carId"),
            @Mapping(source = "car.number", target = "carNumber"),
            @Mapping(source = "createdBy.name", target = "createdByName")
            })
    InspectionDetailDTO toDetailDTO(InspectionEntity inspection);
}

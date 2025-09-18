package com.vroomie.car_service.domain.fleet.accident.mapper;

import com.vroomie.car_service.domain.fleet.accident.dto.AccidentCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentDetailDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentListResponseDTO;
import com.vroomie.car_service.domain.fleet.accident.entity.AccidentEntity;
import com.vroomie.car_service.domain.fleet.accident.entity.AccidentImageEntity;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AccidentMapper {

    @Mappings({
            @Mapping(source = "car.id", target = "carId"),
            @Mapping(source = "employee.email", target = "empEmail"),
            @Mapping(source = "occurredAt", target = "date"),
            @Mapping(source = "saved", target = "save"),
            @Mapping(source = "accidentImages", target = "accidentImages", qualifiedByName = "imagesToStrings")
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

    @Named("imagesToStrings")
    default List<String> imagesToStrings(List<AccidentImageEntity> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        return images.stream()
                .map(AccidentImageEntity::getImage)
                .collect(Collectors.toList());
    }
}

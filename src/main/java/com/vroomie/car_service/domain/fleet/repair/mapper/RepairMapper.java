package com.vroomie.car_service.domain.fleet.repair.mapper;

import com.vroomie.car_service.domain.fleet.repair.dto.RepairCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairDetailDTO;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairListResponseDTO;
import com.vroomie.car_service.domain.fleet.repair.entity.RepairEntity;
import com.vroomie.car_service.domain.fleet.repair.entity.RepairImageEntity;
import com.vroomie.car_service.domain.fleet.repair.enums.RepairImageType;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RepairMapper {

    @Mappings({
            @Mapping(source = "car.id", target = "carId"),
            @Mapping(source = "employee.name", target = "empName"),
            @Mapping(source = "saved", target = "save"),
            @Mapping(target = "beforeImages", expression = "java(mapImages(repair.getRepairImages(), com.vroomie.car_service.domain.fleet.repair.enums.RepairImageType.BEFORE))"),
            @Mapping(target = "afterImages", expression = "java(mapImages(repair.getRepairImages(), com.vroomie.car_service.domain.fleet.repair.enums.RepairImageType.AFTER))")
    })
    RepairDetailDTO toDetailResponse(RepairEntity repair);

    @Mappings({
            @Mapping(source = "car.id", target = "carId"),
            @Mapping(source = "employee.name", target = "empName"),
            @Mapping(source = "saved", target = "save")
    })
    RepairListResponseDTO toListResponseDTO(RepairEntity repair);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "repairImages", ignore = true)
    void update(RepairCreateRequestDTO req, @MappingTarget RepairEntity repair);

    default List<String> mapImages(List<RepairImageEntity> images, RepairImageType type) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        return images.stream()
                .filter(image -> image.getType() == type)
                .map(RepairImageEntity::getImage)
                .collect(Collectors.toList());
    }
}
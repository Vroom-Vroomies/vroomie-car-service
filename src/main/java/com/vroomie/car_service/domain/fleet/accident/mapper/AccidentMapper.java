package com.vroomie.car_service.domain.fleet.accident.mapper;

import com.vroomie.car_service.domain.fleet.accident.dto.AccidentDetailDTO;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentListResponseDTO;
import com.vroomie.car_service.domain.fleet.accident.entity.AccidentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper
public interface AccidentMapper {
    AccidentMapper INSTANCE = Mappers.getMapper(AccidentMapper.class);

    @Mappings({
            @Mapping(source = "car_id", target = "carId"),
            @Mapping(source = "emp_email", target = "empEmail"),
            @Mapping(source = "occurred_at", target = "date"),
            @Mapping(source = "is_saved", target = "save")
    })
    AccidentDetailDTO toDetailResponse(AccidentEntity accident);

    @Mappings({
            @Mapping(source = "car_id", target = "carId"),
            @Mapping(source = "emp_email", target = "empEmail"),
            @Mapping(source = "occurred_at", target = "date"),
            @Mapping(source = "is_saved", target = "save")
    })
    Page<AccidentListResponseDTO> toListResponse(Page<AccidentEntity> accidents);
}

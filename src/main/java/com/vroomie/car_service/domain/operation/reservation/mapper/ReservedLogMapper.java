package com.vroomie.car_service.domain.operation.reservation.mapper;

import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import com.vroomie.car_service.domain.operation.reservation.dto.member.ReservedLogResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.member.DetailReservedLogResponse;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservedLogMapper {

    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(target = "rentStatus", expression = "java(calculateRentStatus(reservedLog))")
    ReservedLogResponse toReservedLogResponse(ReservedLogEntity reservedLog);

    List<ReservedLogResponse> toReservedLogResponseList(List<ReservedLogEntity> reservedLogs);

    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(source = "car.model", target = "carModel")
    @Mapping(source = "car.image", target = "carImage")
    @Mapping(source = "car.type", target = "carType")
    @Mapping(source = "car.fuelType", target = "carFuelType")
    @Mapping(source = "car.allowableCapacity", target = "carAllowableCapacity")
    @Mapping(source = "car.gearType", target = "carGearType")
    @Mapping(source = "car.usageType", target = "carUsageType")
    @Mapping(source = "car.status", target = "carStatus")
    @Mapping(source = "reservation.purpose", target = "purpose")
    DetailReservedLogResponse toDetailReservedLogResponse(ReservedLogEntity reservedLog);

    default RentStatus calculateRentStatus(ReservedLogEntity reservedLog) {
        if (reservedLog.getStatus() != null) {
            // 연체 상태 체크: 현재 시간이 반납 예정일보다 늦고 아직 반납하지 않은 경우
            LocalDateTime endedAt = reservedLog.getEndedAt();
            if (endedAt != null && LocalDateTime.now().isAfter(endedAt)
                    && reservedLog.getStatus() == RentStatus.RENTED) {
                return RentStatus.OVERDUE;
            }

            // 반납일이 있고 반납 예정일보다 늦은 경우도 연체
            LocalDateTime returnDate = reservedLog.getReturnDate();
            if (returnDate != null && endedAt != null && returnDate.isAfter(endedAt)) {
                return RentStatus.OVERDUE;
            }

            return reservedLog.getStatus();
        }
        return RentStatus.RENTED;
    }
}
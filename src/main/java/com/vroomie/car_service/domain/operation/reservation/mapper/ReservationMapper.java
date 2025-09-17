package com.vroomie.car_service.domain.operation.reservation.mapper;

import com.vroomie.car_service.domain.operation.reservation.entity.ReservationEntity;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

    @Mapping(source = "car.number", target = "carNumber")
    @Mapping(source = "member.name", target = "memberName")
    @Mapping(source = "status", target = "reservationStatus")
    @Mapping(target = "rentStatus", expression = "java(getRentStatus(reservation))")
    @Mapping(target = "drivingLogStatus", expression = "java(getDrivingLogStatus(reservation))")
    @Mapping(target = "adminName", expression = "java(getAdminName(reservation))")
    @Mapping(target = "createdAt", expression = "java(java.sql.Timestamp.valueOf(reservation.getCreatedAt()))")
    AdminReservationResponse toAdminReservationResponse(ReservationEntity reservation);

    List<AdminReservationResponse> toAdminReservationResponseList(List<ReservationEntity> reservations);

    default RentStatus getRentStatus(ReservationEntity reservation) {
        // 예약 상태가 APPROVED인 경우
        if (reservation.getStatus() == ReservationStatus.APPROVED) {
            // 대여이력이 있는 경우
            if (reservation.getReservedLog() != null) {
                
                // 연체 여부 확인: returnDate가 endedAt보다 늦은 경우
                LocalDateTime endedAt = reservation.getReservedLog().getEndedAt();
                LocalDateTime returnDate = reservation.getReservedLog().getReturnDate();
                
                if (returnDate != null && endedAt != null && returnDate.isAfter(endedAt)) {
                    return RentStatus.OVERDUE;
                }
                
                // 현재 시간이 반납 예정일보다 늦고, 아직 반납하지 않은 경우도 연체
                if (endedAt != null && LocalDateTime.now().isAfter(endedAt) 
                    && reservation.getReservedLog().getStatus() == RentStatus.RENTED) {
                    return RentStatus.OVERDUE;
                }
                
                // 대여이력의 상태가 있는 경우 해당 상태 반환
                if (reservation.getReservedLog().getStatus() != null) {
                    return reservation.getReservedLog().getStatus();
                }
            }
            // 대여이력이 없어도 APPROVED이면 RENTED 상태
            return RentStatus.RENTED;
        }
        // 기본값은 null (대여 전 상태)
        return null;
    }

    default String getDrivingLogStatus(ReservationEntity reservation) {
        // APPROVED 상태이면 PREPARING 반환
        if (reservation.getStatus() == ReservationStatus.APPROVED) {
            return "PREPARING";
        }
        
        // REJECTED나 다른 상태는 NULL
        return null;
    }

    default String getAdminName(ReservationEntity reservation) {
        // 예약 상태가 APPROVED이고 대여이력이 있는 경우 담당자 이름 반환
        if (reservation.getStatus() == ReservationStatus.APPROVED 
            && reservation.getReservedLog() != null 
            && reservation.getReservedLog().getAdmin() != null 
            && reservation.getReservedLog().getAdmin().getName() != null) {
            return reservation.getReservedLog().getAdmin().getName();
        }
        return null;
    }
}
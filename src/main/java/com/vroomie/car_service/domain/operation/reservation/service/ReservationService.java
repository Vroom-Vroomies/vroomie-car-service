package com.vroomie.car_service.domain.operation.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationRequest;
import com.vroomie.car_service.domain.operation.reservation.entity.ReservationEntity;
import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import com.vroomie.car_service.domain.operation.reservation.repository.ReservationRepository;
import com.vroomie.car_service.domain.operation.reservation.repository.ReservedLogRepository;
import com.vroomie.car_service.domain.operation.reservation.mapper.ReservationMapper;
import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import com.vroomie.car_service.domain.operation.reservation.exception.AdminReservationException;
import com.vroomie.car_service.domain.employee.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import com.vroomie.car_service.global.response.PageResponse;
import org.springframework.data.domain.PageRequest;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservedLogRepository reservedLogRepository;
    private final EmployeeRepository employeeRepository;
    private final ReservationMapper reservationMapper;

    // [관리자] 대여 신청 목록 조회
    public PageResponse<AdminReservationResponse> getAdminReservationList(int currentPage, int size) {

        Page<ReservationEntity> reservations = reservationRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(currentPage - 1, size));

        List<AdminReservationResponse> responses = reservationMapper
                .toAdminReservationResponseList(reservations.getContent());

        return PageResponse.<AdminReservationResponse>builder()
                .data(responses)
                .currentPage(reservations.getNumber() + 1)
                .size(reservations.getSize())
                .totalPages(reservations.getTotalPages())
                .totalElements(reservations.getTotalElements())
                .hasNext(reservations.hasNext())
                .hasPrevious(reservations.hasPrevious())
                .build();
    }

    // [관리자] 대여 신청 상태 변경(승인 or 거절)
    @Transactional
    public AdminReservationResponse updateAdminReservationStatus(Long id, AdminReservationRequest request) {

        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(
                        () -> AdminReservationException.reservationNotFound(id));

        // 상태 변경 유효성 검증
        AdminReservationException.validateStatusChange(reservation.getStatus(), request.getReservationStatus());

        reservation.updateStatus(request.getReservationStatus());
        ReservationEntity updatedReservation = reservationRepository.save(reservation);

        // APPROVED 상태로 변경된 경우 대여 이력 생성
        if (request.getReservationStatus() == ReservationStatus.APPROVED) {
            createReservedLog(updatedReservation);
        }

        AdminReservationResponse response = reservationMapper.toAdminReservationResponse(updatedReservation);

        return response;
    }

    // 대여 이력 생성
    private void createReservedLog(ReservationEntity reservation) {

        // 현재 인증된 사용자 정보 가져오기 (관리자)
        String adminEmail = getCurrentUserEmail();

        // 관리자 정보 조회
        var admin = employeeRepository.findByEmail(adminEmail)
                .orElseThrow(() -> AdminReservationException.employeeNotFound(adminEmail));

        // 대여 이력 생성
        ReservedLogEntity reservedLog = ReservedLogEntity.builder()
                .car(reservation.getCar())
                .admin(admin)
                .reservation(reservation)
                .startedAt(reservation.getStartedAt())
                .endedAt(reservation.getEndedAt())
                .status(RentStatus.RENTED)
                .createdAt(LocalDateTime.now())
                .build();

        reservedLogRepository.save(reservedLog);
    }

    private String getCurrentUserEmail() {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // if (authentication != null && authentication.isAuthenticated()) {
        // return authentication.getName();
        // }
        return "admin@wemade.com";
    }
}

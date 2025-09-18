package com.vroomie.car_service.domain.operation.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationRequest;
import com.vroomie.car_service.domain.operation.reservation.dto.member.AvailableCarListResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.member.AvailableCarDetailResponse;
import com.vroomie.car_service.domain.operation.reservation.entity.ReservationEntity;
import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import com.vroomie.car_service.domain.operation.reservation.repository.ReservationRepository;
import com.vroomie.car_service.domain.operation.reservation.repository.ReservedLogRepository;
import com.vroomie.car_service.domain.operation.reservation.mapper.ReservationMapper;
import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import com.vroomie.car_service.domain.operation.reservation.exception.AdminReservationException;
import com.vroomie.car_service.domain.employee.repository.EmployeeRepository;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import org.springframework.data.domain.Page;
import com.vroomie.car_service.global.response.PageResponse;
import com.vroomie.car_service.global.util.DateTimeUtil;
import org.springframework.data.domain.PageRequest;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

        private final ReservationRepository reservationRepository;
        private final ReservedLogRepository reservedLogRepository;
        private final EmployeeRepository employeeRepository;
        private final CarRepository carRepository;
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

        // [사용자] 특정 시간대 대여 가능한 차량 목록 조회
        public PageResponse<AvailableCarListResponse> getAvailableCarsByTimeSlot(String requestedStartTimeStr,
                        String requestedEndTimeStr, int currentPage, int size) {

                // 문자열을 LocalDateTime으로 변환
                LocalDateTime requestedStartTime = DateTimeUtil.parseDateTime(requestedStartTimeStr);
                LocalDateTime requestedEndTime = DateTimeUtil.parseDateTime(requestedEndTimeStr);

                // 현재 시간보다 이전 시간대는 예약 불가
                if (requestedStartTime.isBefore(LocalDateTime.now())) {
                        throw AdminReservationException.invalidTimeSlot();
                }

                // 업무시간 체크 (9:00 ~ 18:00)
                boolean isValidStartHour = DateTimeUtil.isValidBusinessHour(requestedStartTime);
                boolean isValidEndHour = DateTimeUtil.isValidBusinessHour(requestedEndTime);

                if (!isValidStartHour || !isValidEndHour) {
                        throw AdminReservationException.invalidTimeSlot();
                }

                Page<CarEntity> availableCars = carRepository
                                .findAvailableCarsByTimeSlotWithReservedLog(CarStatus.ACTIVE, requestedStartTime,
                                                requestedEndTime,
                                                PageRequest.of(currentPage - 1, size));

                List<AvailableCarListResponse> responses = reservationMapper
                                .toAvailableCarListResponseList(availableCars.getContent());

                return PageResponse.<AvailableCarListResponse>builder()
                                .data(responses)
                                .currentPage(availableCars.getNumber() + 1)
                                .size(availableCars.getSize())
                                .totalPages(availableCars.getTotalPages())
                                .totalElements(availableCars.getTotalElements())
                                .hasNext(availableCars.hasNext())
                                .hasPrevious(availableCars.hasPrevious())
                                .build();
        }

        // [사용자] 대여 가능한 차량 상세 조회
        public AvailableCarDetailResponse getAvailableCarDetail(Long carId) {

                CarEntity car = carRepository.findAvailableCarById(carId, CarStatus.ACTIVE);

                if (car == null) {
                        throw AdminReservationException.carNotAvailable(carId);
                }

                return reservationMapper.toAvailableCarDetailResponse(car);
        }

}

package com.vroomie.car_service.domain.operation.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.admin.AdminReservationRequest;
import com.vroomie.car_service.domain.operation.reservation.dto.member.AvailableCarListResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.member.MemberCarDetailResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.member.MemberCarReservationRequest;
import com.vroomie.car_service.domain.operation.reservation.dto.member.CurrentCarResponse;
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
import com.vroomie.car_service.domain.fleet.car.exceptions.CarException;
import org.springframework.data.domain.Page;
import com.vroomie.car_service.global.response.PageResponse;
import com.vroomie.car_service.global.util.DateTimeUtil;
import com.vroomie.car_service.global.util.UserUtil;
import com.vroomie.car_service.global.constants.PaginationConstants;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Slf4j
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
                                .findAllByOrderByCreatedAtDesc(PageRequest.of(currentPage - PaginationConstants.PAGE_OFFSET, size));

                List<AdminReservationResponse> responses = reservationMapper
                                .toAdminReservationResponseList(reservations.getContent());

                return PageResponse.of(reservations, responses);
        }

        // [관리자] 차량별 대여 신청 목록 조회
        public PageResponse<AdminReservationResponse> getAdminReservationListByCar(Long carId, int currentPage,
                        int size) {

                Page<ReservationEntity> reservations = reservationRepository
                                .findByCarIdOrderByCreatedAtDesc(carId,
                                                PageRequest.of(currentPage - PaginationConstants.PAGE_OFFSET, size));

                List<AdminReservationResponse> responses = reservationMapper
                                .toAdminReservationResponseList(reservations.getContent());

                return PageResponse.of(reservations, responses);
        }

        // [관리자] 대여 신청 상태 변경(승인 or 거절) (비관적 락으로 동시성 처리)
        @Transactional
        public AdminReservationResponse updateAdminReservationStatus(Long id, AdminReservationRequest request) {

                ReservationEntity reservation = reservationRepository.findByIdWithLock(id);
                if (reservation == null) {
                        throw AdminReservationException.reservationNotFound(id);
                }

                // 상태 변경 유효성 검증
                AdminReservationException.validateStatusChange(reservation.getStatus(), request.getReservationStatus());

                reservation.updateStatus(request.getReservationStatus());
                ReservationEntity updatedReservation = reservationRepository.save(reservation);

                // REJECTED 상태로 변경된 경우 기존 RESERVED 상태를 REJECTED로 변경
                if (request.getReservationStatus() == ReservationStatus.REJECTED) {
                        updateReservedLogStatusToRejected(updatedReservation);
                }

                // APPROVED 상태로 변경된 경우 기존 RESERVED 상태를 RENTED로 변경
                if (request.getReservationStatus() == ReservationStatus.APPROVED) {
                        updateReservedLogStatusToRented(updatedReservation);
                }

                AdminReservationResponse response = reservationMapper.toAdminReservationResponse(updatedReservation);

                return response;
        }

        // RESERVED 상태의 대여 이력을 RENTED로 변경
        private void updateReservedLogStatusToRented(ReservationEntity reservation) {
                // 해당 예약의 RESERVED 상태 ReservedLog 조회
                List<ReservedLogEntity> reservedLogs = reservedLogRepository.findByReservationAndStatus(
                                reservation, RentStatus.RESERVED);

                if (!reservedLogs.isEmpty()) {
                        ReservedLogEntity reservedLog = reservedLogs.get(PaginationConstants.FIRST_ITEM_INDEX);

                        // 관리자 정보로 업데이트
                        String adminEmail = UserUtil.getCurrentAdminEmail();
                        var admin = employeeRepository.findByEmail(adminEmail)
                                        .orElseThrow(() -> AdminReservationException.employeeNotFound(adminEmail));

                        // 상태를 RENTED로 변경하고 관리자 정보 업데이트
                        reservedLog.updateStatus(RentStatus.RENTED);
                        reservedLog.updateAdmin(admin);
                        reservedLogRepository.save(reservedLog);
                }
        }

        // RESERVED 상태의 대여 이력을 REJECTED로 변경
        private void updateReservedLogStatusToRejected(ReservationEntity reservation) {
                // 해당 예약의 RESERVED 상태 ReservedLog 조회
                List<ReservedLogEntity> reservedLogs = reservedLogRepository.findByReservationAndStatus(
                                reservation, RentStatus.RESERVED);

                if (!reservedLogs.isEmpty()) {
                        ReservedLogEntity reservedLog = reservedLogs.get(PaginationConstants.FIRST_ITEM_INDEX);

                        // 관리자 정보로 업데이트
                        String adminEmail = UserUtil.getCurrentAdminEmail();
                        var admin = employeeRepository.findByEmail(adminEmail)
                                        .orElseThrow(() -> AdminReservationException.employeeNotFound(adminEmail));

                        // 상태를 REJECTED로 변경하고 관리자 정보 업데이트
                        reservedLog.updateStatus(RentStatus.REJECTED);
                        reservedLog.updateAdmin(admin);
                        reservedLogRepository.save(reservedLog);
                }
        }

        // PENDING 상태 예약에 대한 대여 이력 생성 (RESERVED 상태)
        private void createReservedLogForPendingReservation(ReservationEntity reservation, String memberEmail) {
                // 직원 정보 조회 (관리자 대신 신청자로 설정)(유저 dto가 없으므로 var 사용)
                var member = employeeRepository.findByEmail(memberEmail)
                                .orElseThrow(() -> AdminReservationException.employeeNotFound(memberEmail));

                // RESERVED 상태로 대여 이력 생성
                ReservedLogEntity reservedLog = ReservedLogEntity.builder()
                                .car(reservation.getCar())
                                .admin(member) // 신청자를 admin 필드에 임시 저장
                                .reservation(reservation)
                                .startedAt(reservation.getStartedAt())
                                .endedAt(reservation.getEndedAt())
                                .status(RentStatus.RESERVED)
                                .createdAt(LocalDateTime.now())
                                .build();

                reservedLogRepository.save(reservedLog);
        }

        // [사용자] 특정 시간대 대여 가능한 차량 목록 조회
        public PageResponse<AvailableCarListResponse> getAvailableCarsByTimeSlot(String requestedStartTimeStr,
                        String requestedEndTimeStr, int currentPage, int size) {

                // 문자열을 LocalDateTime으로 변환
                LocalDateTime requestedStartTime = DateTimeUtil.parseDateTime(requestedStartTimeStr);
                LocalDateTime requestedEndTime = DateTimeUtil.parseDateTime(requestedEndTimeStr);

                // 현재 시간보다 이전 시간대는 예약 불가
                if (requestedStartTime.isBefore(LocalDateTime.now())) {
                        throw AdminReservationException.pastDateTimeNotAllowed();
                }

                // 과거 시간 검증
                DateTimeUtil.validateNotPastTime(requestedStartTime);
                DateTimeUtil.validateNotPastTime(requestedEndTime);

                // 업무시간 체크 (24시간 운영)
                boolean isValidStartHour = DateTimeUtil.isValidBusinessHour(requestedStartTime);
                boolean isValidEndHour = DateTimeUtil.isValidBusinessHour(requestedEndTime);

                log.info("Time validation - Start: {} (valid: {}), End: {} (valid: {})",
                                requestedStartTime, isValidStartHour, requestedEndTime, isValidEndHour);

                if (!isValidStartHour || !isValidEndHour) {
                        throw AdminReservationException.invalidTimeSlot();
                }

                Page<CarEntity> availableCars = carRepository
                                .findAvailableCarsByTimeSlotWithReservedLog(CarStatus.ACTIVE, requestedStartTime,
                                                requestedEndTime,
                                                PageRequest.of(currentPage - PaginationConstants.PAGE_OFFSET, size));

                List<AvailableCarListResponse> responses = reservationMapper
                                .toAvailableCarListResponseList(availableCars.getContent());

                return PageResponse.of(availableCars, responses);
        }

        // [사용자] 차량 상세 조회
        public MemberCarDetailResponse getMemberCarDetail(Long carId) {

                // 사용자 이메일 가져오기
                String memberEmpEmail = UserUtil.getCurrentMemberEmail();

                CarEntity car = carRepository.findAvailableCarById(carId, CarStatus.ACTIVE);
                ReservationEntity reservation = reservationRepository.findLatestByCarAndMember(carId, memberEmpEmail);

                if (car == null) {
                        throw CarException.carNotFoundException();
                }

                return reservationMapper.toMemberCarDetailResponse(car, reservation);
        }

        // [사용자] 차량 대여 신청하기 (비관적 락으로 동시성 처리)
        @Transactional
        public MemberCarDetailResponse createMemberCarReservation(MemberCarReservationRequest request, Long carId) {

                // 사용자 이메일 가져오기
                String memberEmpEmail = UserUtil.getCurrentMemberEmail();

                // 차량 존재 여부 확인 (비관적 락 적용)
                CarEntity car = carRepository.findByIdAndStatusWithLock(carId, CarStatus.ACTIVE);
                if (car == null) {
                        throw CarException.carNotFoundException();
                }

                // 요청 시간 유효성 검증
                if (request.getStartedAt().isBefore(LocalDateTime.now())) {
                        throw AdminReservationException.pastDateTimeNotAllowed();
                }

                // 과거 시간 검증
                DateTimeUtil.validateNotPastTime(request.getStartedAt());
                DateTimeUtil.validateNotPastTime(request.getEndedAt());

                // 업무시간 체크 (24시간 운영)
                boolean isValidStartHour = DateTimeUtil.isValidBusinessHour(request.getStartedAt());
                boolean isValidEndHour = DateTimeUtil.isValidBusinessHour(request.getEndedAt());

                if (!isValidStartHour || !isValidEndHour) {
                        throw AdminReservationException.invalidTimeSlot();
                }

                // 해당 차량이 요청 시간대에 이미 예약되어 있는지 확인
                List<ReservationStatus> conflictStatuses = Arrays.asList(
                                ReservationStatus.PENDING,
                                ReservationStatus.APPROVED);

                long conflictCount = reservationRepository.countConflictingReservations(
                                carId,
                                request.getStartedAt(),
                                request.getEndedAt(),
                                conflictStatuses);

                if (conflictCount > PaginationConstants.MIN_COUNT_THRESHOLD) {
                        throw AdminReservationException.reservationTimeConflict();
                }

                // 사용자가 이미 반납하지 않은 대여가 있는지 확인
                long activeRentalCount = reservedLogRepository.countActiveRentalsByMemberEmail(memberEmpEmail);
                if (activeRentalCount > PaginationConstants.MIN_COUNT_THRESHOLD) {
                        throw AdminReservationException.memberAlreadyHasActiveRental();
                }

                // 사용자가 이미 예약 중인 차량이 있는지 확인
                long activeReservationCount = reservationRepository
                                .countActiveReservationsByMemberEmail(memberEmpEmail);
                if (activeReservationCount > PaginationConstants.MIN_COUNT_THRESHOLD) {
                        throw AdminReservationException.memberAlreadyHasActiveReservation();
                }

                // 직원 조회(유저 dto가 없으므로 var 사용)
                var member = employeeRepository.findByEmail(memberEmpEmail)
                                .orElseThrow(() -> AdminReservationException.employeeNotFound(memberEmpEmail));

                // 예약 생성
                ReservationEntity reservation = ReservationEntity.builder()
                                .car(car)
                                .member(member)
                                .startedAt(request.getStartedAt())
                                .endedAt(request.getEndedAt())
                                .purpose(request.getPurpose())
                                .detail(request.getDetail())
                                .status(ReservationStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .build();

                ReservationEntity savedReservation = reservationRepository.save(reservation);

                // 대여 이력에도 RESERVED 상태로 데이터 생성
                createReservedLogForPendingReservation(savedReservation, memberEmpEmail);

                return reservationMapper.toMemberCarDetailResponse(car, savedReservation);
        }

        // [사용자] 차량 대여 취소하기 (비관적 락으로 동시성 처리)
        @Transactional
        public MemberCarDetailResponse cancelMemberCarReservation(Long carId) {

                // 사용자 이메일 가져오기
                String memberEmpEmail = UserUtil.getCurrentMemberEmail();

                // 차량 존재 여부 확인
                CarEntity car = carRepository.findAvailableCarById(carId, CarStatus.ACTIVE);
                if (car == null) {
                        throw CarException.carNotFoundException();
                }

                // 사용자의 해당 차량에 대한 PENDING 상태 예약 조회
                ReservationEntity reservation = reservationRepository.findLatestByCarAndMemberAndStatus(
                                carId, memberEmpEmail, ReservationStatus.PENDING);

                if (reservation == null) {
                        throw AdminReservationException.memberReservationNotFound();
                }

                // PENDING 상태에서만 취소 가능
                if (reservation.getStatus() != ReservationStatus.PENDING) {
                        throw AdminReservationException.reservationNotCancellable();
                }

                // 상태를 CANCELLED로 변경
                reservation.updateStatus(ReservationStatus.CANCELLED);
                reservation.getReservedLog().updateStatus(RentStatus.CANCELLED);
                reservedLogRepository.save(reservation.getReservedLog());
                ReservationEntity updatedReservation = reservationRepository.save(reservation);

                return reservationMapper.toMemberCarDetailResponse(car, updatedReservation);
        }

        // [사용자] 현재 대여 중인 차량 조회
        public CurrentCarResponse getCurrentRentedCar() {
                String memberEmail = UserUtil.getCurrentMemberEmail();

                ReservedLogEntity activeRental = reservedLogRepository
                                .findByMemberEmailAndStatus(memberEmail,
                                                Arrays.asList(RentStatus.RENTED, RentStatus.OVERDUE,
                                                                RentStatus.RESERVED))
                                .stream()
                                .findFirst()
                                .orElse(null);

                if (activeRental != null) {
                        return new CurrentCarResponse(activeRental.getCar().getId(),
                                        activeRental.getStartedAt(),
                                        activeRental.getEndedAt(),
                                        activeRental.getReservation().getPurpose(),
                                        activeRental.getReservation().getDetail(),
                                        activeRental.getReservation().getStatus());
                }

                return new CurrentCarResponse(null, null, null, null, null, null);
        }

}

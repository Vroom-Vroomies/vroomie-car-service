package com.vroomie.car_service.domain.operation.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.vroomie.car_service.domain.operation.reservation.dto.member.ReservedLogResponse;
import com.vroomie.car_service.domain.operation.reservation.dto.member.DetailReservedLogResponse;
import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import com.vroomie.car_service.domain.operation.reservation.repository.ReservedLogRepository;
import com.vroomie.car_service.domain.operation.reservation.mapper.ReservedLogMapper;
import com.vroomie.car_service.domain.operation.reservation.exception.ReservedLogException;
import org.springframework.data.domain.Page;
import com.vroomie.car_service.global.response.PageResponse;
import org.springframework.data.domain.PageRequest;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservedLogService {

    private final ReservedLogRepository reservedLogRepository;
    private final ReservedLogMapper reservedLogMapper;

    // [사용자] 내 대여 이력 목록 조회 (RESERVED, RENTED, RETURNED, OVERDUE 상태 포함)
    public PageResponse<ReservedLogResponse> getReservedLogList(int currentPage, int size) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String currentUserEmail = authentication.getName();
        String currentUserEmail = "user01@wemade.com";

        Page<ReservedLogEntity> reservedLogs = reservedLogRepository
                .findByMemberEmailOrderByCreatedAtDesc(currentUserEmail, PageRequest.of(currentPage - 1, size));

        List<ReservedLogResponse> responses = reservedLogMapper
                .toReservedLogResponseList(reservedLogs.getContent());

        return PageResponse.<ReservedLogResponse>builder()
                .data(responses)
                .currentPage(reservedLogs.getNumber() + 1)
                .size(reservedLogs.getSize())
                .totalPages(reservedLogs.getTotalPages())
                .totalElements(reservedLogs.getTotalElements())
                .hasNext(reservedLogs.hasNext())
                .hasPrevious(reservedLogs.hasPrevious())    
                .build();
    }

    // [사용자] 내 대여 이력 상세 조회
    public DetailReservedLogResponse getReservedLog(Long id) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String currentUserEmail = authentication.getName();
        String currentUserEmail = "user01@wemade.com";

        ReservedLogEntity reservedLog = reservedLogRepository.findByIdAndMemberEmail(id, currentUserEmail);
        
        if (reservedLog == null) {
            throw ReservedLogException.reservedLogNotFound(id);
        }

        return reservedLogMapper.toDetailReservedLogResponse(reservedLog);
    }
}
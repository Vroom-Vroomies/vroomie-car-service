package com.vroomie.car_service.domain.operation.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import com.vroomie.car_service.domain.operation.reservation.repository.ReservedLogRepository;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OverdueUpdateService {

    private final ReservedLogRepository reservedLogRepository;

    /**
     * 매일 자정(00:00)에 연체 상태를 업데이트합니다.
     * 한국 시간 기준으로 실행됩니다.
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    @Transactional
    public void updateOverdueRentals() {
        log.info("Starting overdue rental status update job at {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        
        // 연체 대상 조회: RENTED 상태, 반납일 없음, 대여종료일이 현재보다 이전
        List<ReservedLogEntity> overdueRentals = reservedLogRepository.findOverdueRentals(currentTime);
        
        if (overdueRentals.isEmpty()) {
            log.info("No overdue rentals found.");
            return;
        }
        
        log.info("Found {} overdue rentals to update", overdueRentals.size());
        
        // 연체 상태로 업데이트
        int updatedCount = 0;
        for (ReservedLogEntity rental : overdueRentals) {
            try {
                rental.updateStatus(RentStatus.OVERDUE);
                reservedLogRepository.save(rental);
                updatedCount++;
                
                log.debug("Updated rental ID {} to OVERDUE status. Car: {}, EndedAt: {}", 
                         rental.getId(), 
                         rental.getCar().getNumber(), 
                         rental.getEndedAt());
            } catch (Exception e) {
                log.error("Failed to update rental ID {} to OVERDUE status: {}", 
                         rental.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Overdue rental status update job completed. Updated {} out of {} rentals", 
                updatedCount, overdueRentals.size());
    }
    
    /**
     * 수동으로 연체 상태 업데이트를 실행합니다.
     * 테스트나 즉시 실행이 필요한 경우 사용합니다.
     */
    @Transactional
    public void manualUpdateOverdueRentals() {
        log.info("Manual overdue rental status update triggered");
        updateOverdueRentals();
    }
}
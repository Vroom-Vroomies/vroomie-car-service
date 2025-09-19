package com.vroomie.car_service.domain.fleet.drivinglog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VariableCostService {

    // 임시 유저
    private String getCurrentUserEmail() {
        return "admin@wemade.com";
    }
}

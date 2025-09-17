package com.vroomie.car_service.domain.fleet.car.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageInfoResponse {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}

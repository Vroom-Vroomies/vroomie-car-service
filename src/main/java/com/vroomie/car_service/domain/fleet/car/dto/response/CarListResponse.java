package com.vroomie.car_service.domain.fleet.car.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CarListResponse {

    private PageInfoResponse pageInfo;
    private List<CarSimpleResponse> cars;
}

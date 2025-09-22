package com.vroomie.car_service.domain.fleet.drivinglog.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record KakaoResponse(
        List<Document> documents
) {
    public record Document(
            @JsonProperty("road_address") RoadAddress roadAddress,
            @JsonProperty("address") Address address
    ) {}

    public record RoadAddress(
            @JsonProperty("building_name") String buildingName
    ) {}

    public record Address(
            @JsonProperty("address_name") String addressName
    ) {}
}

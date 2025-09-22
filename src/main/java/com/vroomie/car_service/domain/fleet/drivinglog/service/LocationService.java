package com.vroomie.car_service.domain.fleet.drivinglog.service;

import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.LocationReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.KakaoResponse;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.LocationResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class LocationService {

    @Value("${kakao.rest-api.key}")
    private String kakaoRestApiKey;
    private final RestTemplate restTemplate;
    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json";

    // 좌표 -> 주소 변환
    public LocationResDTO getLocation(LocationReqDTO locationReqDTO) {
        String url = UriComponentsBuilder.fromHttpUrl(KAKAO_API_URL)
                .queryParam("x", locationReqDTO.getLongitude())
                .queryParam("y", locationReqDTO.getLatitude())
                .toUriString();

        // Authorization 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                KakaoResponse.class
        );

        KakaoResponse response = responseEntity.getBody();

        if (response != null && !response.documents().isEmpty()) {
            KakaoResponse.Document doc = response.documents().get(0);

            String baseAddress = doc.address() != null ? doc.address().addressName() : "주소 없음";
            String buildingName = (doc.roadAddress() != null && doc.roadAddress().buildingName() != null && !doc.roadAddress().buildingName().isBlank())
                    ? doc.roadAddress().buildingName()
                    : "";

            String combinedAddress = baseAddress + (buildingName.isEmpty() ? "" : " " + buildingName);

            return new LocationResDTO(combinedAddress);
        }


        return new LocationResDTO("주소를 가져올 수 없습니다.");
    }

}

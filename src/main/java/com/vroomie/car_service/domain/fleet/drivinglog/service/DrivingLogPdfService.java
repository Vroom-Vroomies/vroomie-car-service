package com.vroomie.car_service.domain.fleet.drivinglog.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.exceptions.CarException;
import com.vroomie.car_service.domain.fleet.car.repository.CarRepository;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogPdfReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogInfoDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.res.DrivingLogPdfResDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.entity.DrivingLogEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import com.vroomie.car_service.domain.fleet.drivinglog.repository.DrivingLogRepository;
import com.vroomie.car_service.domain.operation.reservation.enums.Purpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.vroomie.car_service.domain.fleet.drivinglog.exception.DrivingLogException.drivingLogListNotFoundException;

@Service
@RequiredArgsConstructor
public class DrivingLogPdfService {

    private final DrivingLogRepository drivingLogRepository;
    private final CarRepository carRepository;
    private final SpringTemplateEngine templateEngine;

    public byte[] generateDrivingLogPdf(Long carId, DrivingLogPdfReqDTO drivingLogPdfReqDTO) {
        /* 엔티티 조회 */
        // 차 엔티티 조회
        CarEntity carEntity = carRepository.findById(carId).orElseThrow(CarException::carNotFoundException);

        LocalDateTime startedAt = drivingLogPdfReqDTO.getStartDate().atStartOfDay();
        LocalDateTime endedAt = drivingLogPdfReqDTO.getEndDate().atTime(LocalTime.MAX);

        // 운행 일지 조회
        List<DrivingLogEntity> drivingLogEntityList = drivingLogRepository.findAllByCarAndPeriodAndStatus(carId, startedAt, endedAt, LogStatus.COMPLETED);
        if (drivingLogEntityList.isEmpty()) {
            throw drivingLogListNotFoundException();
        }

        // Entity -> DTO 매핑
        List<DrivingLogInfoDTO> drivingLogInfoDTOList = new ArrayList<>();
        for (DrivingLogEntity drivingLogEntity : drivingLogEntityList) {
            // 운행일지 시작 날짜
            String date = drivingLogEntity.getStartedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 목적별 사용 거리 분리
            BigDecimal commuteDistance = BigDecimal.ZERO;
            BigDecimal businessDistance = BigDecimal.ZERO;
            if (drivingLogEntity.getPurpose() == Purpose.COMMUTING) {
                commuteDistance = drivingLogEntity.getOdometerDistance();
            } else if (drivingLogEntity.getPurpose() == Purpose.BUSINESS) {
                businessDistance = drivingLogEntity.getOdometerDistance();
            }

            DrivingLogInfoDTO drivingLogInfoDTO = DrivingLogInfoDTO.builder()
                    .date(date)
                    .department(drivingLogEntity.getEmployeeEntity().getDepartment())
                    .name(drivingLogEntity.getEmployeeEntity().getName())
                    .startOdometer(drivingLogEntity.getStartOdometer())
                    .endOdometer(drivingLogEntity.getEndOdometer())
                    .distance(drivingLogEntity.getOdometerDistance().setScale(0, RoundingMode.HALF_UP))
                    .commuteDistance(commuteDistance.setScale(0, RoundingMode.HALF_UP))
                    .businessDistance(businessDistance.setScale(0, RoundingMode.HALF_UP))
                    .note(drivingLogEntity.getNote())
                    .build();
            drivingLogInfoDTOList.add(drivingLogInfoDTO);
        }

        // 주행거리 합산
        BigDecimal totalDistance = BigDecimal.ZERO;
        BigDecimal totalBusinessDistance = BigDecimal.ZERO;
        for (DrivingLogInfoDTO dto : drivingLogInfoDTOList) {
            BigDecimal distance = dto.getDistance() != null ? dto.getDistance() : BigDecimal.ZERO;
            BigDecimal commute = dto.getCommuteDistance() != null ? dto.getCommuteDistance() : BigDecimal.ZERO;
            BigDecimal business = dto.getBusinessDistance() != null ? dto.getBusinessDistance() : BigDecimal.ZERO;

            totalDistance = totalDistance.add(distance);
            totalBusinessDistance = totalBusinessDistance.add(business).add(commute);
        }

        // 소숫점 제거
        totalDistance = totalDistance.setScale(0, RoundingMode.HALF_UP);
        totalBusinessDistance = totalBusinessDistance.setScale(0, RoundingMode.HALF_UP);

        double businessRate = 0.0;
        if (totalDistance.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = totalBusinessDistance
                    .divide(totalDistance, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)); // 백분율 변환
            businessRate = rate.setScale(2, RoundingMode.HALF_UP).doubleValue(); // 반올림하여 소숫점 두자리까지만
        }

        // DrivingLogPdfResDTO 생성
        /* 회사 관련 더미 데이터
         Entity 생기면
         carEntity.getCompany().getName();
        * */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        String taxPeriod = drivingLogPdfReqDTO.getStartDate().format(formatter)
                + " ~ "
                + drivingLogPdfReqDTO.getEndDate().format(formatter);
        DrivingLogPdfResDTO drivingLogPdfResDTO = DrivingLogPdfResDTO.builder()
                .companyName("부르미")
                .businessNumber("111-11-1111")
                .taxPeriod(taxPeriod)
                .carType(carEntity.getType())
                .carNumber(carEntity.getNumber())
                .logs(drivingLogInfoDTOList)
                .totalDistance(totalDistance)
                .totalBusinessDistance(totalBusinessDistance)
                .businessRate(businessRate)
                .build();

        // DTO -> HTML 변환
        String html = generateHtml(drivingLogPdfResDTO);

        // HTML -> PDF 변환
        return convertHtmlToPdf(html);
    }

    // DTO -> HTML 변환
    private String generateHtml(DrivingLogPdfResDTO drivingLogPdfResDTO) {
        Context context = new Context();
        context.setVariables(Map.of(
                "companyName", drivingLogPdfResDTO.getCompanyName(),
                "taxPeriod", drivingLogPdfResDTO.getTaxPeriod(),
                "businessNumber", drivingLogPdfResDTO.getBusinessNumber(),
                "carType", drivingLogPdfResDTO.getCarType(),
                "carNumber", drivingLogPdfResDTO.getCarNumber(),
                "logs", drivingLogPdfResDTO.getLogs(),
                "totalDistance", drivingLogPdfResDTO.getTotalDistance(),
                "totalBusinessDistance", drivingLogPdfResDTO.getTotalBusinessDistance(),
                "businessRate", drivingLogPdfResDTO.getBusinessRate()
        ));
        return templateEngine.process("driving-log-template", context); // resources/templates/driving-log-template.html
    }

    // HTML -> DTO 변환
    private byte[] convertHtmlToPdf(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/NanumGothic.ttf"), "Nanum Gothic");
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }
}

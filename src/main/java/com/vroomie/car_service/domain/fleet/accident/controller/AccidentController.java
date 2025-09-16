package com.vroomie.car_service.domain.fleet.accident.controller;

import com.vroomie.car_service.domain.fleet.accident.service.AccidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fleet/accidents")
@RequiredArgsConstructor
public class AccidentController {

    private final AccidentService service;


}

package com.agroproserver.serveragropro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroproserver.serveragropro.dto.request.LiquidacionRequest;
import com.agroproserver.serveragropro.service.LiquidacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/liquidacion")
public class LiquidacionController {

    @Autowired
    LiquidacionService liquidacionService;

    @PostMapping("/generarLiquidacion")
    public ResponseEntity<?> generarLiquidacion(@Valid @RequestBody LiquidacionRequest liquidacionDto, BindingResult bindingResult){
        return liquidacionService.generarLiquidacion(liquidacionDto, bindingResult);
    }
}

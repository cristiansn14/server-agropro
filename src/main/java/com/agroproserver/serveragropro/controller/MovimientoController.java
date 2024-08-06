package com.agroproserver.serveragropro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agroproserver.serveragropro.dto.request.MovimientoRequestDto;
import com.agroproserver.serveragropro.service.MovimientoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/movimiento")
public class MovimientoController {

    @Autowired
    MovimientoService movimientoService;

    @PostMapping("/crearMovimiento")
    public ResponseEntity<?> crearMovimiento(@RequestPart("movimiento") @Valid MovimientoRequestDto movimientoDto,
        @RequestPart(value = "documento", required = false) MultipartFile documento,
        BindingResult bindingResult) {
            return movimientoService.crearMovimiento(movimientoDto, documento, bindingResult);
    }
}

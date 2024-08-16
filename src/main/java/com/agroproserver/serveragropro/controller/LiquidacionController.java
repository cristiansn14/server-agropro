package com.agroproserver.serveragropro.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agroproserver.serveragropro.dto.request.LineaLiquidacionRequest;
import com.agroproserver.serveragropro.dto.request.LiquidacionRequest;
import com.agroproserver.serveragropro.service.LiquidacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/liquidacion")
public class LiquidacionController {

    @Autowired
    LiquidacionService liquidacionService;

    @PostMapping("/generarLiquidacion")
    public ResponseEntity<?> generarLiquidacion(@RequestPart("liquidacion") @Valid LiquidacionRequest liquidacionDto, 
        @RequestPart(value = "documento", required = false) MultipartFile documento,
        BindingResult bindingResult){
            return liquidacionService.generarLiquidacion(liquidacionDto, documento, bindingResult);
    }

    @GetMapping("/findLineasLiquidacionByLiquidacionId/{idLiquidacion}")
    public ResponseEntity<?> findLineasLiquidacionByLiquidacionId(@PathVariable UUID idLiquidacion) {
        return liquidacionService.findLineasLiquidacionByLiquidacionId(idLiquidacion);
    }

    @GetMapping("/findById/{idLiquidacion}")
    public ResponseEntity<?> findById(@PathVariable UUID idLiquidacion) {
        return liquidacionService.findById(idLiquidacion);
    }

    @GetMapping("/findByFincaId/{idFinca}")
    public ResponseEntity<?> findByFincaId(@PathVariable UUID idFinca) {
        return liquidacionService.findByFincaId(idFinca);
    }

    @PostMapping("/liquidacionRecibida")
    public ResponseEntity<?> liquidacionRecibida(@Valid @RequestBody LineaLiquidacionRequest lineaLiquidacionDto, BindingResult bindingResult){
        return liquidacionService.liquidacionRecibida(lineaLiquidacionDto, bindingResult);
    }

    @DeleteMapping("/eliminarLiquidacion/{idLiquidacion}")
    public ResponseEntity<?> eliminarLiquidacion(@PathVariable UUID idLiquidacion){
        return liquidacionService.eliminarLiquidacion(idLiquidacion);
    }
}

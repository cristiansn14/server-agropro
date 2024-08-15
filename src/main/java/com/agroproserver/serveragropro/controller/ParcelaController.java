package com.agroproserver.serveragropro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroproserver.serveragropro.dto.request.ParcelaConstruccionDto;
import com.agroproserver.serveragropro.dto.request.ParcelaDto;
import com.agroproserver.serveragropro.service.ParcelaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/parcela")
public class ParcelaController {

    @Autowired
    ParcelaService parcelaService;

    @PostMapping("/guardarParcela")
    public ResponseEntity<?> guardarParcela(@Valid @RequestBody ParcelaDto parcelaDto, BindingResult bindingResult){
        return parcelaService.guardarParcela(parcelaDto, bindingResult);
    }

    @PostMapping("/guardarParcelaConstruccion")
    public ResponseEntity<?> guardarParcela(@Valid @RequestBody ParcelaConstruccionDto parcelaConstruccionDto, BindingResult bindingResult){
        return parcelaService.guardarParcelaConstruccion(parcelaConstruccionDto, bindingResult);
    }

    @GetMapping("/findParcelaByReferenciaCatastral/{referenciaCatastral}")
    public ResponseEntity<?> findParcelaByReferenciaCatastral(@PathVariable String referenciaCatastral) {
        return parcelaService.findParcelaByReferenciaCatastral(referenciaCatastral);
    }

    @GetMapping("/findParcelaConstruccionByReferenciaCatastral/{referenciaCatastral}")
    public ResponseEntity<?> findParcelaConstruccionByReferenciaCatastral(@PathVariable String referenciaCatastral) {
        return parcelaService.findParcelaConstruccionByReferenciaCatastral(referenciaCatastral);
    }

    @GetMapping("/findSubparcelasByReferenciaCatastral/{referenciaCatastral}")
    public ResponseEntity<?> findSubparcelasByReferenciaCatastral(@PathVariable String referenciaCatastral) {
        return parcelaService.findSubparcelasByReferenciaCatastral(referenciaCatastral);
    }

    @GetMapping("/findUsuariosInParcela/{referenciaCatastral}")
    public ResponseEntity<?> findUsuariosInParcela(@PathVariable String referenciaCatastral) {
        return parcelaService.findUsuariosInParcela(referenciaCatastral);
    }
}

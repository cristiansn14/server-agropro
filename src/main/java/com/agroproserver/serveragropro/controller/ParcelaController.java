package com.agroproserver.serveragropro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

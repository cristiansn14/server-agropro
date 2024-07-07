package com.agroproserver.serveragropro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroproserver.serveragropro.dto.request.FincaRequestDto;
import com.agroproserver.serveragropro.dto.request.UsuarioFincaRequestDto;
import com.agroproserver.serveragropro.service.FincaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/finca")
public class FincaController {

    @Autowired
    FincaService fincaService;

    @PostMapping("/guardarFinca")
    public ResponseEntity<?> guardarFinca(@Valid @RequestBody FincaRequestDto fincaDto, BindingResult bindingResult){
        return fincaService.guardarFinca(fincaDto, bindingResult);
    }

    @PostMapping("/addUsuariosFinca")
    public ResponseEntity<?> addUsuariosFinca(@Valid @RequestBody List<UsuarioFincaRequestDto> usuariosFincaDto, BindingResult bindingResult){
        return fincaService.addUsuariosFinca(usuariosFincaDto, bindingResult);
    }

    @GetMapping("/findAllFincasByUsuarioId/{idUsuario}")
    public ResponseEntity<?> findAllFincasByUsuarioId(@PathVariable UUID idUsuario) {
        return fincaService.findAllFincasByUsuarioId(idUsuario);
    }

    @GetMapping("/findUsuarioFincaByUsuarioIdAndFincaId/{idUsuario}/{idFinca}")
    public ResponseEntity<?> findUsuarioFincaByUsuarioIdAndFincaId(@PathVariable UUID idUsuario, @PathVariable UUID idFinca) {
        return fincaService.findUsuarioFincaByUsuarioIdAndFincaId(idUsuario, idFinca);
    }

    @GetMapping("/getOnzasDisponibles/{idFinca}")
    public ResponseEntity<?> getOnzasDisponibles(@PathVariable UUID idFinca) {
        return fincaService.getOnzasDisponibles(idFinca);
    }
}

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
import com.agroproserver.serveragropro.dto.response.UsuarioFincaDto;
import com.agroproserver.serveragropro.service.FincaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/finca")
public class FincaController {

    @Autowired
    FincaService fincaService;

    @GetMapping("/findById/{idFinca}")
    public ResponseEntity<?> findById(@PathVariable UUID idFinca) {
        return fincaService.findById(idFinca);
    }

    @PostMapping("/guardarFinca")
    public ResponseEntity<?> guardarFinca(@Valid @RequestBody FincaRequestDto fincaDto, BindingResult bindingResult){
        return fincaService.guardarFinca(fincaDto, bindingResult);
    }

    @PostMapping("/editarFinca")
    public ResponseEntity<?> editarFinca(@Valid @RequestBody FincaRequestDto fincaDto, BindingResult bindingResult){
        return fincaService.editarFinca(fincaDto, bindingResult);
    }

    @PostMapping("/addUsuariosFinca")
    public ResponseEntity<?> addUsuariosFinca(@Valid @RequestBody List<UsuarioFincaRequestDto> usuariosFincaDto, BindingResult bindingResult){
        return fincaService.addUsuariosFinca(usuariosFincaDto, bindingResult);
    }

    @PostMapping("/editarUsuarioFinca")
    public ResponseEntity<?> editarUsuarioFinca(@Valid @RequestBody UsuarioFincaDto usuarioFincaDto, BindingResult bindingResult){
        return fincaService.editarUsuarioFinca(usuarioFincaDto, bindingResult);
    }

    @PostMapping("/eliminarUsuarioFinca")
    public ResponseEntity<?> eliminarUsuarioFinca(@Valid @RequestBody UsuarioFincaDto usuarioFincaDto){
        return fincaService.eliminarUsuarioFinca(usuarioFincaDto);
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

    @GetMapping("/getParcelasByIdFinca/{idFinca}")
    public ResponseEntity<?> getParcelasByIdFinca(@PathVariable UUID idFinca) {
        return fincaService.getParcelasByIdFinca(idFinca);
    }

    @GetMapping("/getParcelasBajaByIdFinca/{idFinca}")
    public ResponseEntity<?> getParcelasBajaByIdFinca(@PathVariable UUID idFinca) {
        return fincaService.getParcelasBajaByIdFinca(idFinca);
    }

    @GetMapping("/findUsuariosFincaByFincaId/{idFinca}")
    public ResponseEntity<?> findUsuariosFincaByFincaId(@PathVariable UUID idFinca) {
        return fincaService.findUsuariosFincaByFincaId(idFinca);
    }

    @GetMapping("/findUsuarioFincaById/{idUsuarioFinca}")
    public ResponseEntity<?> findUsuarioFincaById(@PathVariable UUID idUsuarioFinca) {
        return fincaService.findUsuarioFincaById(idUsuarioFinca);
    }
}

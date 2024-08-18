package com.agroproserver.serveragropro.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

import com.agroproserver.serveragropro.dto.request.ParcelaConstruccionDto;
import com.agroproserver.serveragropro.dto.request.ParcelaConstruccionRequestDto;
import com.agroproserver.serveragropro.dto.request.ParcelaDto;
import com.agroproserver.serveragropro.dto.request.ParcelaRequestDto;
import com.agroproserver.serveragropro.dto.request.UsuarioParcelaDto;
import com.agroproserver.serveragropro.dto.response.UsuarioParcelaResponseDto;
import com.agroproserver.serveragropro.service.ParcelaService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/parcela")
public class ParcelaController {

    @Autowired
    ParcelaService parcelaService;

    @PostMapping("/guardarParcela")
    public ResponseEntity<?> guardarParcela(@Valid @RequestBody ParcelaDto parcelaDto, BindingResult bindingResult){
        return parcelaService.guardarParcela(parcelaDto, bindingResult);
    }

    @PostMapping("/actualizarParcela")
    public ResponseEntity<?> actualizarParcela(@Valid @RequestBody ParcelaDto parcelaDto){
        return parcelaService.actualizarParcela(parcelaDto);
    }

    @PostMapping("/editarParcela")
    public ResponseEntity<?> editarParcela(@Valid @RequestBody ParcelaRequestDto parcelaDto, BindingResult bindingResult){
        return parcelaService.editarParcela(parcelaDto, bindingResult);
    }

    @PostMapping("/guardarParcelaConstruccion")
    public ResponseEntity<?> guardarParcelaConstruccion(@Valid @RequestBody ParcelaConstruccionDto parcelaConstruccionDto, BindingResult bindingResult){
        return parcelaService.guardarParcelaConstruccion(parcelaConstruccionDto, bindingResult);
    }

    @PostMapping("/editarParcelaConstruccion")
    public ResponseEntity<?> editarParcelaConstruccion(@Valid @RequestBody ParcelaConstruccionRequestDto parcelaConstruccionDto, BindingResult bindingResult){
        return parcelaService.editarParcelaConstruccion(parcelaConstruccionDto, bindingResult);
    }

    @PostMapping("/crearUsuarioParcela")
    public ResponseEntity<?> crearUsuarioParcela(@Valid @RequestBody List<UsuarioParcelaDto> usuarioParcelaDto, BindingResult bindingResult){
        return parcelaService.crearUsuarioParcela(usuarioParcelaDto, bindingResult);
    }

    @PostMapping("/editarUsuarioParcela")
    public ResponseEntity<?> editarUsuarioParcela(@Valid @RequestBody UsuarioParcelaResponseDto usuarioParcelaDto){
        return parcelaService.editarUsuarioParcela(usuarioParcelaDto);
    }

    @PostMapping("/eliminarUsuarioParcela")
    public ResponseEntity<?> eliminarUsuarioParcela(@Valid @RequestBody UsuarioParcelaResponseDto usuarioParcelaDto){
        return parcelaService.eliminarUsuarioParcela(usuarioParcelaDto);
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

    @GetMapping("/findUsuariosBajaInParcela/{referenciaCatastral}")
    public ResponseEntity<?> findUsuariosBajaInParcela(@PathVariable String referenciaCatastral) {
        return parcelaService.findUsuariosBajaInParcela(referenciaCatastral);
    }

    @GetMapping("/findUsuarioParcelaById/{idUsuarioParcela}")
    public ResponseEntity<?> findUsuarioParcelaById(@PathVariable UUID idUsuarioParcela) {
        return parcelaService.findUsuarioParcelaById(idUsuarioParcela);
    }

    @GetMapping("/getParticipacionesDisponibles/{referenciaCatastral}")
    public ResponseEntity<?> getParticipacionesDisponibles(@PathVariable String referenciaCatastral) {
        return parcelaService.getParticipacionesDisponibles(referenciaCatastral);
    }

    @PutMapping("/darAltaParcela")
    public ResponseEntity<?> darAltaParcela(@RequestBody String referenciaCatastral) {
        return parcelaService.darAltaParcela(referenciaCatastral);
    }

    @DeleteMapping("/darBajaParcela/{referenciaCatastral}")
    public ResponseEntity<?> darBajaParcela(@PathVariable String referenciaCatastral){
        return parcelaService.darBajaParcela(referenciaCatastral);
    }
}

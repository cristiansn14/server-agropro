package com.agroproserver.serveragropro.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agroproserver.serveragropro.dto.request.UsuarioRequestDto;
import com.agroproserver.serveragropro.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/findUsuariosNotInFinca/{idFinca}")
    public ResponseEntity<?> findUsuariosNotInFinca(@PathVariable UUID idFinca) {
        return usuarioService.findUsuariosNotInFinca(idFinca);
    }

    @GetMapping("/getFotoPerfil/{idUsuario}")
    public ResponseEntity<byte[]> getFotoPerfil(@PathVariable UUID idUsuario) {
        return usuarioService.getFotoPerfil(idUsuario);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        return usuarioService.findById(id);
    }

    @PostMapping("/editarUsuario")
    public ResponseEntity<?> editarUsuario(@RequestPart("usuario") @Valid UsuarioRequestDto usuario,
        @RequestPart(value = "foto", required = false) MultipartFile foto,
        BindingResult bindingResult) {
            return usuarioService.editarUsuario(usuario, foto, bindingResult);
    }

    @GetMapping("/findUsuariosInFinca/{idFinca}")
    public ResponseEntity<?> findUsuariosInFinca(@PathVariable UUID idFinca) {
        return usuarioService.findUsuariosInFinca(idFinca);
    }
}

package com.agroproserver.serveragropro.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroproserver.serveragropro.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/findUsuariosNotInFinca/{idFinca}")
    public ResponseEntity<?> findUsuariosNotInFinca(@PathVariable UUID idFinca) {
        return usuarioService.findUsuariosNotInFinca(idFinca);
    }

}

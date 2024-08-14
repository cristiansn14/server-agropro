package com.agroproserver.serveragropro.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agroproserver.serveragropro.dto.request.RepresentanteRequest;
import com.agroproserver.serveragropro.dto.request.UsuarioRequestDto;
import com.agroproserver.serveragropro.dto.response.RepresentanteResponse;
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

    @PostMapping("/añadirRepresentante")
    public ResponseEntity<?> añadirRepresentante(@Valid @RequestBody RepresentanteRequest representanteRequest, BindingResult bindingResult) {
            return usuarioService.añadirRepresentante(representanteRequest, bindingResult);
    }

    @PostMapping("/editarRepresentante")
    public ResponseEntity<?> editarRepresentante(@Valid @RequestBody RepresentanteResponse representante, BindingResult bindingResult) {
            return usuarioService.editarRepresentante(representante, bindingResult);
    }

    @PostMapping("/eliminarRepresentante")
    public ResponseEntity<?> eliminarRepresentante(@Valid @RequestBody RepresentanteResponse representante) {
            return usuarioService.eliminarRepresentante(representante);
    }

    @GetMapping("/findRepresentantesByIdUsuario/{idUsuario}")
    public ResponseEntity<?> findRepresentantesByIdUsuario(@PathVariable UUID idUsuario) {
        return usuarioService.findRepresentantesByIdUsuario(idUsuario);
    }

    @GetMapping("/findRepresentanteById/{idRepresentante}")
    public ResponseEntity<?> findRepresentanteById(@PathVariable UUID idRepresentante) {
        return usuarioService.findRepresentanteById(idRepresentante);
    }
}

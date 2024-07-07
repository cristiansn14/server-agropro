package com.agroproserver.serveragropro.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.agroproserver.serveragropro.dto.response.UsuarioResponseDto;
import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    UsuarioRepository usuarioRepository;

    public Optional<Usuario> getByUsername(String username){
        return usuarioRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username){
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public void save(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public ResponseEntity<?> findUsuariosNotInFinca(UUID fincaId) {
        List<UsuarioResponseDto> usuariosDto = usuarioRepository.findUsuariosNotInFinca(fincaId).stream()
        .map(usuario -> new UsuarioResponseDto(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido1(),
            usuario.getApellido2(),
            usuario.getUsername()
        ))
        .collect(Collectors.toList());

        return ResponseEntity.ok(usuariosDto);
    }
}

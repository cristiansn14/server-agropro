package com.agroproserver.serveragropro.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agroproserver.serveragropro.model.UsuarioFinca;

public interface UsuarioFincaRepository extends JpaRepository<UsuarioFinca, UUID>{
    
    UsuarioFinca findByUsuarioIdAndFincaId(UUID usuarioId, UUID fincaId);
}

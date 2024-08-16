package com.agroproserver.serveragropro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.agroproserver.serveragropro.model.UsuarioFinca;

public interface UsuarioFincaRepository extends JpaRepository<UsuarioFinca, UUID>{
    
    UsuarioFinca findByUsuarioIdAndFincaId(UUID usuarioId, UUID fincaId);

    Boolean existsByUsuarioIdAndFincaId(UUID usuarioId, UUID fincaId);

    @Query("SELECT uf FROM UsuarioFinca uf WHERE uf.finca.id = :fincaId AND uf.rol.rol <> 'SUPERUSUARIO' AND uf.fechaBaja IS NULL")
    List<UsuarioFinca> findUsuariosFincaByFincaId(@Param("fincaId") UUID fincaId);
}
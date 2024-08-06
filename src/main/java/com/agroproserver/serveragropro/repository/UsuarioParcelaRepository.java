package com.agroproserver.serveragropro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.dto.response.UsuarioParcelaResponseDto;
import com.agroproserver.serveragropro.model.UsuarioParcela;


@Repository
@Transactional
public interface UsuarioParcelaRepository extends JpaRepository<UsuarioParcela, UUID> {

    @Query("SELECT new com.agroproserver.serveragropro.dto.response.UsuarioParcelaResponseDto(" +
           "u.id, u.nombre, u.apellido1, u.apellido2, up.participacion, up.fechaAlta, up.fechaModificacion, up.fechaBaja) " +
           "FROM UsuarioParcela up JOIN up.usuario u " +
           "WHERE up.parcela.referenciaCatastral = :referenciaCatastral")
    List<UsuarioParcelaResponseDto> findUsuarioParcelaByReferenciaCatastral(@Param("referenciaCatastral") String referenciaCatastral);
}

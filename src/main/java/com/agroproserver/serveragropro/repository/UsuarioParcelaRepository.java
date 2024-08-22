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
           "up.id, u.nombre, u.apellido1, u.apellido2, up.participacion, u.id, up.parcela.referenciaCatastral, up.fechaAlta, up.fechaModificacion, up.fechaBaja) " +
           "FROM UsuarioParcela up JOIN up.usuario u " +
           "WHERE up.parcela.referenciaCatastral = :referenciaCatastral AND up.fechaBaja IS NULL")
    List<UsuarioParcelaResponseDto> findUsuarioParcelaByReferenciaCatastral(@Param("referenciaCatastral") String referenciaCatastral);

    @Query("SELECT new com.agroproserver.serveragropro.dto.response.UsuarioParcelaResponseDto(" +
           "up.id, u.nombre, u.apellido1, u.apellido2, up.participacion, u.id, up.parcela.referenciaCatastral, up.fechaAlta, up.fechaModificacion, up.fechaBaja) " +
           "FROM UsuarioParcela up JOIN up.usuario u " +
           "WHERE up.parcela.referenciaCatastral = :referenciaCatastral AND up.fechaBaja IS NOT NULL")
    List<UsuarioParcelaResponseDto> findUsuarioParcelaBajaByReferenciaCatastral(@Param("referenciaCatastral") String referenciaCatastral);

    @Query("SELECT up FROM UsuarioParcela up JOIN up.usuario u " +
           "WHERE up.parcela.referenciaCatastral = :referenciaCatastral AND up.fechaBaja IS NULL")
    List<UsuarioParcela> findUsuariosParcelaByReferenciaCatastral(@Param("referenciaCatastral") String referenciaCatastral);

    @Query("SELECT new com.agroproserver.serveragropro.dto.response.UsuarioParcelaResponseDto(" +
           "up.id, u.nombre, u.apellido1, u.apellido2, up.participacion, u.id, up.parcelaConstruccion.referenciaCatastral, up.fechaAlta, up.fechaModificacion, up.fechaBaja) " +
           "FROM UsuarioParcela up JOIN up.usuario u " +
           "WHERE up.parcelaConstruccion.referenciaCatastral = :referenciaCatastral AND up.fechaBaja IS NULL")
    List<UsuarioParcelaResponseDto> findUsuarioParcelaConstruccionByReferenciaCatastral(@Param("referenciaCatastral") String referenciaCatastral);

    @Query("SELECT new com.agroproserver.serveragropro.dto.response.UsuarioParcelaResponseDto(" +
           "up.id, u.nombre, u.apellido1, u.apellido2, up.participacion, u.id, up.parcelaConstruccion.referenciaCatastral, up.fechaAlta, up.fechaModificacion, up.fechaBaja) " +
           "FROM UsuarioParcela up JOIN up.usuario u " +
           "WHERE up.parcelaConstruccion.referenciaCatastral = :referenciaCatastral AND up.fechaBaja IS NOT NULL")
    List<UsuarioParcelaResponseDto> findUsuarioParcelaConstruccionBajaByReferenciaCatastral(@Param("referenciaCatastral") String referenciaCatastral);
    
    @Query("SELECT up FROM UsuarioParcela up JOIN up.usuario u " +
           "WHERE up.parcelaConstruccion.referenciaCatastral = :referenciaCatastral AND up.fechaBaja IS NULL")
    List<UsuarioParcela> findUsuariosParcelaConstruccionByReferenciaCatastral(@Param("referenciaCatastral") String referenciaCatastral);

    @Query("SELECT up FROM UsuarioParcela up " +
        "JOIN up.parcela p " +
        "WHERE p.finca.id = :fincaId AND up.fechaBaja IS NULL")
    List<UsuarioParcela> findUsuariosParcelaByFincaId(@Param("fincaId") UUID fincaId);

    @Query("SELECT 100 - COALESCE(SUM(up.participacion), 0) FROM UsuarioParcela up WHERE up.parcela.referenciaCatastral = :referenciaCatastral AND up.fechaBaja IS NULL")
    Long getParticipacionDisponibleParcela(@Param("referenciaCatastral") String referenciaCatastral);

    @Query("SELECT 100 - COALESCE(SUM(up.participacion), 0) FROM UsuarioParcela up WHERE up.parcelaConstruccion.referenciaCatastral = :referenciaCatastral AND up.fechaBaja IS NULL")
    Long getParticipacionDisponibleParcelaConstruccion(@Param("referenciaCatastral") String referenciaCatastral);

    Boolean existsByUsuarioIdAndParcelaReferenciaCatastral (UUID idUsuario, String referenciaCatastral);

    Boolean existsByUsuarioIdAndParcelaConstruccionReferenciaCatastral (UUID idUsuario, String referenciaCatastral);

    UsuarioParcela findByUsuarioIdAndParcelaReferenciaCatastral (UUID idUsuario, String referenciaCatastral);

    UsuarioParcela findByUsuarioIdAndParcelaConstruccionReferenciaCatastral (UUID idUsuario, String referenciaCatastral);

    List<UsuarioParcela> findByParcelaReferenciaCatastral (@Param("referenciaCatastral")String referenciaCatastral);

    List<UsuarioParcela> findByParcelaConstruccionReferenciaCatastral (String referenciaCatastral);
}

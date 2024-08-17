package com.agroproserver.serveragropro.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Parcela;

@Repository
@Transactional
public interface ParcelaRepository extends JpaRepository<Parcela, String> {

    @Query("SELECT p FROM Parcela p WHERE p.finca.id = :fincaId AND p.fechaBaja IS NULL")
    List<Parcela> findByFincaId(@Param("fincaId")UUID idFinca);

    @Query("SELECT COALESCE(SUM(p.superficie), 0) FROM Parcela p WHERE p.finca.id = :fincaId")
    BigDecimal sumSuperficieByFincaId(@Param("fincaId") UUID fincaId);

    @Query("SELECT p FROM Parcela p WHERE p.finca.id = :fincaId AND p.fechaBaja IS NOT NULL")
    List<Parcela> findParcelasBajaByFincaId(@Param("fincaId") UUID fincaId);

    @Query("SELECT up.parcela FROM UsuarioParcela up " +
       "WHERE up.usuario.id = :usuarioId " +
       "AND up.fechaBaja IS NULL")
    List<Parcela> findParcelasByUsuarioIdAndFechaBajaIsNull(@Param("usuarioId") UUID usuarioId);
}

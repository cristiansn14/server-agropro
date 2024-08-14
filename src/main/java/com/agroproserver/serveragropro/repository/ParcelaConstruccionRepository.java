package com.agroproserver.serveragropro.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.ParcelaConstruccion;


@Repository
@Transactional
public interface ParcelaConstruccionRepository extends JpaRepository<ParcelaConstruccion, String> {

    List<ParcelaConstruccion> findByFincaId(UUID idFinca);

    @Query("SELECT COALESCE(SUM(p.superficie), 0) FROM ParcelaConstruccion p WHERE p.finca.id = :fincaId")
    BigDecimal sumSuperficieByFincaId(@Param("fincaId") UUID fincaId);
}

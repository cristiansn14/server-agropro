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

    List<Parcela> findByFincaId(UUID idFinca);

    @Query("SELECT COALESCE(SUM(p.superficie), 0) FROM Parcela p WHERE p.finca.id = :fincaId")
    BigDecimal sumSuperficieByFincaId(@Param("fincaId") UUID fincaId);
}

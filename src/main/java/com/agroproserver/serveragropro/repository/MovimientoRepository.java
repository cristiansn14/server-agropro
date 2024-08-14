package com.agroproserver.serveragropro.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Movimiento;

@Repository
@Transactional
public interface MovimientoRepository extends JpaRepository<Movimiento, UUID>{

    List<Movimiento> findByFincaId(UUID idFinca);

    @Query("SELECT SUM(m.importe) FROM Movimiento m WHERE m.fecha BETWEEN :fechaDesde AND :fechaHasta")
    BigDecimal sumImportesBetweenDates(@Param("fechaDesde") Date fechaDesde, @Param("fechaHasta") Date fechaHasta);
}

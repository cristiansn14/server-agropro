package com.agroproserver.serveragropro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agroproserver.serveragropro.model.LineaLiquidacion;

public interface LineaLiquidacionRepository extends JpaRepository<LineaLiquidacion, UUID>{

    List<LineaLiquidacion> findByLiquidacionId(UUID idLiquidacion);
}

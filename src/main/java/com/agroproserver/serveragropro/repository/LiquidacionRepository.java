package com.agroproserver.serveragropro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agroproserver.serveragropro.model.Liquidacion;

public interface LiquidacionRepository extends JpaRepository<Liquidacion, UUID>{

    List<Liquidacion> findByFincaId(UUID idFinca);
}

package com.agroproserver.serveragropro.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agroproserver.serveragropro.model.Liquidacion;

public interface LiquidacionRepository extends JpaRepository<Liquidacion, UUID>{

}

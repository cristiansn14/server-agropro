package com.agroproserver.serveragropro.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Movimiento;

@Repository
@Transactional
public interface MovimientoRepository extends JpaRepository<Movimiento, UUID>{

}

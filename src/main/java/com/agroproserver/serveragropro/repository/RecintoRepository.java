package com.agroproserver.serveragropro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Recinto;


@Repository
@Transactional
public interface RecintoRepository extends JpaRepository<Recinto, UUID>{

    List<Recinto> findByParcelaReferenciaCatastral(String referenciaCatastral);
    
}

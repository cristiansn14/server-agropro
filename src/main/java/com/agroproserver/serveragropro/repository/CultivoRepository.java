package com.agroproserver.serveragropro.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Cultivo;


@Repository
@Transactional
public interface CultivoRepository extends JpaRepository<Cultivo, UUID> {

    Boolean existsByCodigo (String codigo);

    Cultivo findByCodigo (String codigo);
}

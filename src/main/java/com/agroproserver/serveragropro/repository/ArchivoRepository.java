package com.agroproserver.serveragropro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Archivo;

@Repository
@Transactional
public interface ArchivoRepository extends JpaRepository<Archivo, UUID>{

    List<Archivo> findByFincaId(UUID idFinca);
}

package com.agroproserver.serveragropro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Representante;

@Repository
@Transactional
public interface RepresentanteRepository extends JpaRepository<Representante, UUID> {

    List<Representante> findByUsuarioId (UUID idUsuario);
}

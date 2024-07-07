package com.agroproserver.serveragropro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Comunidad;

@Repository
@Transactional
public interface ComunidadRepository extends JpaRepository<Comunidad, Long>{

}

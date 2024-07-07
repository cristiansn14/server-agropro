package com.agroproserver.serveragropro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Provincia;

@Repository
@Transactional
public interface ProvinciaRepository extends JpaRepository<Provincia, Long>{

    List<Provincia> findByComunidadId(Long idComunidad);

}

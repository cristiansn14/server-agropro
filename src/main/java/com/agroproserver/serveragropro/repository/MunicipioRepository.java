package com.agroproserver.serveragropro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Municipio;

@Repository
@Transactional
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

    List<Municipio> findByProvinciaId(Long idProvincia);

}

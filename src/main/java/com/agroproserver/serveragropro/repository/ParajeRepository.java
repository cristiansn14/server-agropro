package com.agroproserver.serveragropro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Paraje;


@Repository
@Transactional
public interface ParajeRepository extends JpaRepository<Paraje, Long> {

    List<Paraje> findByProvinciaIdAndMunicipioId(Long idProvincia, Long idMunicipio);

}

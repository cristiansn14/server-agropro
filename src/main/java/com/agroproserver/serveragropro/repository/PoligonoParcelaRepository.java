package com.agroproserver.serveragropro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.PoligonoParcela;

@Repository
@Transactional
public interface PoligonoParcelaRepository extends JpaRepository<PoligonoParcela, Long>{

    List<PoligonoParcela> findByProvinciaIdAndMunicipioId(Long idProvincia, Long idMunicipio);
}

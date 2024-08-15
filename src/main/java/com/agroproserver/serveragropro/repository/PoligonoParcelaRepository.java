package com.agroproserver.serveragropro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.PoligonoParcela;

@Repository
@Transactional
public interface PoligonoParcelaRepository extends JpaRepository<PoligonoParcela, UUID>{

    List<PoligonoParcela> findByProvinciaIdAndMunicipioId(Long idProvincia, Long idMunicipio);

    Boolean existsByProvinciaIdAndMunicipioIdAndPoligonoAndParcela(Long idProvincia, Long idMunicipio, String poligono, String parcela);

    PoligonoParcela findByProvinciaIdAndMunicipioIdAndPoligonoAndParcela(Long idProvincia, Long idMunicipio, String poligono, String parcela);

}

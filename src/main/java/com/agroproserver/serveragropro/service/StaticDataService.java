package com.agroproserver.serveragropro.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.agroproserver.serveragropro.dto.response.ComunidadDto;
import com.agroproserver.serveragropro.dto.response.MunicipioDto;
import com.agroproserver.serveragropro.dto.response.ProvinciaDto;
import com.agroproserver.serveragropro.repository.ComunidadRepository;
import com.agroproserver.serveragropro.repository.MunicipioRepository;
import com.agroproserver.serveragropro.repository.ProvinciaRepository;

@Service
public class StaticDataService {

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private ComunidadRepository comunidadRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    public ResponseEntity<?> findAllComunidades () {
        List<ComunidadDto> comunidades = comunidadRepository.findAll().stream()
            .map(comunidad -> new ComunidadDto(
                comunidad.getId(),
                comunidad.getNombre()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(comunidades);
    }

    public ResponseEntity<?> findAllProvincias () {
        List<ProvinciaDto> provincias = provinciaRepository.findAll().stream()
            .map(provincia -> new ProvinciaDto(
                provincia.getId(),
                provincia.getNombre(),
                provincia.getComunidad() != null ? provincia.getComunidad().getId() : null
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(provincias);
    }

    public ResponseEntity<?> findAllProvinciasByIdComunidad (Long idComunidad) {
        List<ProvinciaDto> provincias = provinciaRepository.findByComunidadId(idComunidad).stream()
            .map(provincia -> new ProvinciaDto(
                provincia.getId(),
                provincia.getNombre(),
                provincia.getComunidad() != null ? provincia.getComunidad().getId() : null
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(provincias);
    }

    public ResponseEntity<?> findAllMunicipiosByIdProvincia (Long idProvincia) {
        List<MunicipioDto> municipios = municipioRepository.findByProvinciaId(idProvincia).stream()
            .map(municipio -> new MunicipioDto(
                municipio.getId(),
                municipio.getNombre(),
                municipio.getIdMunicipio(),
                municipio.getProvincia() != null ? municipio.getProvincia().getId() : null,
                municipio.getComunidad() != null ? municipio.getComunidad().getId() : null
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(municipios);
    }
}

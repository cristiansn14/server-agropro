package com.agroproserver.serveragropro.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.dto.response.ComunidadDto;
import com.agroproserver.serveragropro.dto.response.MunicipioDto;
import com.agroproserver.serveragropro.dto.response.ProvinciaDto;
import com.agroproserver.serveragropro.model.Municipio;
import com.agroproserver.serveragropro.model.Provincia;
import com.agroproserver.serveragropro.model.Comunidad;
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

    @Transactional
    public ResponseEntity<?> findAllComunidades () {
        List<ComunidadDto> comunidades = comunidadRepository.findAll().stream()
            .map(comunidad -> new ComunidadDto(
                comunidad.getId(),
                comunidad.getNombre()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(comunidades);
    }

    @Transactional
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

    @Transactional
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

    @Transactional
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

    @Transactional
    public ResponseEntity<?> findMunicipioByNombreAndProvincia (String municipio, String provincia) {
        Municipio foundMunicipio = municipioRepository.findByNombreAndProvinciaNombre(municipio, provincia);
        MunicipioDto municipioDto = new MunicipioDto(
            foundMunicipio.getId(),
            foundMunicipio.getNombre(),
            foundMunicipio.getComunidad().getId(),
            foundMunicipio.getProvincia().getId(),
            foundMunicipio.getIdMunicipio()
        );
        return ResponseEntity.ok(municipioDto);
    }

    @Transactional
    public ResponseEntity<?> getNombreComunidadById (long idComunidad) {
        Comunidad comunidad = comunidadRepository.findById(idComunidad)
            .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));

        ComunidadDto comunidadDto = new ComunidadDto(
            comunidad.getId(),
            comunidad.getNombre()
        );

        return ResponseEntity.ok(comunidadDto);
    }

    @Transactional
    public ResponseEntity<?> getNombreProvinciaById (long idProvincia) {
        Provincia provincia = provinciaRepository.findById(idProvincia)
            .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));

        ProvinciaDto provinciaDto = new ProvinciaDto(
            provincia.getId(),
            provincia.getNombre(),
            provincia.getComunidad().getId()
        );

        return ResponseEntity.ok(provinciaDto);
    }

    @Transactional
    public ResponseEntity<?> getNombreMunicipioById (long idMunicipio) {
        Municipio municipio = municipioRepository.findById(idMunicipio)
            .orElseThrow(() -> new RuntimeException("Municipio no encontrada"));
        
        MunicipioDto municipioDto = new MunicipioDto(
            municipio.getId(),
            municipio.getNombre(),
            municipio.getComunidad().getId(),
            municipio.getProvincia().getId(),
            municipio.getIdMunicipio()
        );

        return ResponseEntity.ok(municipioDto);
    }
}

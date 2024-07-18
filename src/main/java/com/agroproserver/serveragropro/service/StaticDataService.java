package com.agroproserver.serveragropro.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.agroproserver.serveragropro.dto.response.ComunidadDto;
import com.agroproserver.serveragropro.dto.response.MunicipioDto;
import com.agroproserver.serveragropro.dto.response.PoligonoParcelaResponseDto;
import com.agroproserver.serveragropro.dto.response.ParajeResponseDto;
import com.agroproserver.serveragropro.dto.response.ProvinciaDto;
import com.agroproserver.serveragropro.model.Finca;
import com.agroproserver.serveragropro.model.Municipio;
import com.agroproserver.serveragropro.model.Cultivo;
import com.agroproserver.serveragropro.repository.ComunidadRepository;
import com.agroproserver.serveragropro.repository.CultivoRepository;
import com.agroproserver.serveragropro.repository.FincaRepository;
import com.agroproserver.serveragropro.repository.MunicipioRepository;
import com.agroproserver.serveragropro.repository.ParajeRepository;
import com.agroproserver.serveragropro.repository.PoligonoParcelaRepository;
import com.agroproserver.serveragropro.repository.ProvinciaRepository;

@Service
public class StaticDataService {

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private ComunidadRepository comunidadRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Autowired
    private FincaRepository fincaRepository;

    @Autowired
    private PoligonoParcelaRepository poligonoParcelaRepository;

    @Autowired
    private ParajeRepository parajeRepository;

    @Autowired
    private CultivoRepository cultivoRepository;

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

    public ResponseEntity<?> findPoligonoParcelaByFinca (UUID idFinca) {
        Finca finca = fincaRepository.findById(idFinca)
                .orElseThrow(() -> new RuntimeException("Finca no encontrada")); 
        List<PoligonoParcelaResponseDto> poligonosParcela = poligonoParcelaRepository.findByProvinciaIdAndMunicipioId(finca.getProvincia().getId(), finca.getMunicipio().getIdMunicipio())
            .stream().map(poligonoParcela -> new PoligonoParcelaResponseDto(
                poligonoParcela.getId(), 
                poligonoParcela.getPoligono(), 
                poligonoParcela.getParcela()
            ))
            .collect(Collectors.toList());
            return ResponseEntity.ok(poligonosParcela);
    }

    public ResponseEntity<?> findParajeByFinca (UUID idFinca) {
        Finca finca = fincaRepository.findById(idFinca)
                .orElseThrow(() -> new RuntimeException("Finca no encontrada")); 
        List<ParajeResponseDto> parajes = parajeRepository.findByProvinciaIdAndMunicipioId(finca.getProvincia().getId(), finca.getMunicipio().getIdMunicipio())
            .stream().map(paraje -> new ParajeResponseDto(
                paraje.getId(), 
                paraje.getNombre()
            ))
            .collect(Collectors.toList());
            return ResponseEntity.ok(parajes);
    }

    public ResponseEntity<?> findCultivos() {
        List<Cultivo> cultivos = cultivoRepository.findAll();
        return ResponseEntity.ok(cultivos);
    }
}

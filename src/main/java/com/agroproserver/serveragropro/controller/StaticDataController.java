package com.agroproserver.serveragropro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroproserver.serveragropro.service.StaticDataService;


@RestController
@RequestMapping("/data")
public class StaticDataController {

    @Autowired
    StaticDataService staticDataService;

    @GetMapping("/findAllComunidades")
    public ResponseEntity<?> findAllComunidades() {
        return staticDataService.findAllComunidades();
    }

    @GetMapping("/findAllProvincias")
    public ResponseEntity<?> findAllProvincias() {
        return staticDataService.findAllProvincias();
    }

    @GetMapping("/findAllProvinciasByIdComunidad/{idComunidad}")
    public ResponseEntity<?> findAllProvinciasByIdComunidad(@PathVariable Long idComunidad) {
        return staticDataService.findAllProvinciasByIdComunidad(idComunidad);
    }

    @GetMapping("/findAllMunicipiosByIdProvincia/{idProvincia}")
    public ResponseEntity<?> findAllMunicipiosByIdProvincia(@PathVariable Long idProvincia) {
        return staticDataService.findAllMunicipiosByIdProvincia(idProvincia);
    }

    @GetMapping("/findMunicipioByNombreAndProvincia/{municipio}/{provincia}")
    public ResponseEntity<?> findMunicipioByNombreAndProvincia(@PathVariable String municipio, @PathVariable String provincia) {
        return staticDataService.findMunicipioByNombreAndProvincia(municipio, provincia);
    }

    @GetMapping("/getNombreComunidadById/{idComunidad}")
    public ResponseEntity<?> getNombreComunidadById(@PathVariable long idComunidad) {
        return staticDataService.getNombreComunidadById(idComunidad);
    }

    @GetMapping("/getNombreProvinciaById/{idProvincia}")
    public ResponseEntity<?> getNombreProvinciaById(@PathVariable long idProvincia) {
        return staticDataService.getNombreProvinciaById(idProvincia);
    }

    @GetMapping("/getNombreMunicipioById/{idMunicipio}")
    public ResponseEntity<?> getNombreMunicipioById(@PathVariable long idMunicipio) {
        return staticDataService.getNombreMunicipioById(idMunicipio);
    }
}

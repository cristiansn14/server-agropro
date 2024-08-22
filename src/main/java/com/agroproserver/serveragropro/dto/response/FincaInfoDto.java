package com.agroproserver.serveragropro.dto.response;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class FincaInfoDto {

    private UUID id;

    private String nombre;

    private long onzas;

    private long idComunidad;

    private long idProvincia;

    private long idMunicipio;

    private String nombreComunidad;

    private String nombreProvincia;

    private String nombreMunicipio;

    private Timestamp fechaAlta;

    private Timestamp fechaModificacion;

    private Timestamp fechaBaja;
}

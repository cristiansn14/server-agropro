package com.agroproserver.serveragropro.dto.response;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FincaResponseDto {

    private UUID id;

    private String nombre;

    private long onzas;

    private long comunidad;

    private long provincia;

    private long municipio;

    private Timestamp fechaAlta;

    private Timestamp fechaModificacion;

    private Timestamp fechaBaja;

    public FincaResponseDto (UUID id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}

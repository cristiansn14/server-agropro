package com.agroproserver.serveragropro.dto.response;

import java.util.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ConstruccionDto {

    private String referenciaCatastral;

    private String clase;

    private String usoPrincipal;

    private long superficie;

    private long escalera;

    private long planta;

    private long puerta;

    private String tipoReforma;

    private Date fechaReforma;

    private Timestamp fechaAlta;

    private Timestamp fechaModificacion;

    private Timestamp fechaBaja;
}

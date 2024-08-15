package com.agroproserver.serveragropro.dto.response;

import java.math.BigDecimal;
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
public class ParcelaResponseDto {

    private String referenciaCatastral;

    private String clase;

    private String usoPrincipal;

    private BigDecimal superficie;

    private BigDecimal valorSuelo;

    private BigDecimal valorConstruccion;

    private BigDecimal valorCatastral;

    private String anoValor;
   
    private String poligono;

    private String parcela;

    private UUID idFinca;

    private String paraje;

    private Timestamp fechaAlta;

    private Timestamp fechaModificacion;

    private Timestamp fechaBaja;
}

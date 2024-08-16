package com.agroproserver.serveragropro.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LiquidacionResponse {

    private UUID id;

    private String concepto;

    private String tipo;

    private BigDecimal importeTotal;

    private Date fechaDesde;

    private Date fechaHasta;

    private UUID idFinca;

    private Timestamp fecha;

    private UUID idArchivo;

    private String nombreArchivo;

    private String tipoArchivo;
}

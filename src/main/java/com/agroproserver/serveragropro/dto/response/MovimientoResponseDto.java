package com.agroproserver.serveragropro.dto.response;

import java.math.BigDecimal;
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
public class MovimientoResponseDto {

    private UUID id;
    
    private String concepto;

    private BigDecimal importe;

    private Date fecha;

    private UUID idArchivo;

    private String nombreArchivo;

    private String tipoArchivo;

}

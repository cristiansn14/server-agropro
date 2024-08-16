package com.agroproserver.serveragropro.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LineaLiquidacionResponse {

    private UUID id;

    private BigDecimal importe;

    private String nombre;

    private String apellido1;

    private String apellido2;

    private UUID idLiquidacion;

    private UUID IdUsuario;

    private boolean recibida;
}

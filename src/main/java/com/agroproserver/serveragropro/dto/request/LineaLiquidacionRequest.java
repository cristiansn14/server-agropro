package com.agroproserver.serveragropro.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LineaLiquidacionRequest {

    @NotNull
    private UUID id;

    @NotNull
    private BigDecimal importe;

    @NotNull
    private UUID idUsuario;
    
    @NotNull
    private UUID idLiquidacion;

    @NotNull
    private boolean recibida;
    
}

package com.agroproserver.serveragropro.dto.request;

import java.util.Date;
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
public class LiquidacionRequest {

    @NotNull
    private String concepto;

    @NotNull
    private String tipo;

    @NotNull
    private Date fechaDesde;

    @NotNull
    private Date fechaHasta;

    @NotNull
    private UUID idFinca;
}

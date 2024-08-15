package com.agroproserver.serveragropro.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ParcelaRequestDto {

    @NotBlank
    private String referenciaCatastral;

    @NotBlank
    private String poligono;

    @NotBlank
    private String parcela;

    @NotNull
    private String paraje;

    @NotNull
    private UUID idFinca;

    @NotBlank
    private String clase;

    @NotBlank
    private String usoPrincipal;

    @NotNull
    private BigDecimal superficie;

    private BigDecimal valorSuelo;

    private BigDecimal valorConstruccion;

    private BigDecimal valorCatastral;

    private String anoValor;
}

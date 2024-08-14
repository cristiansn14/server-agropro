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
    private String clase;

    @NotBlank
    private String usoPrincipal;

    @NotNull
    private BigDecimal superficie;

    private Long valorSuelo;

    private Long valorConstruccion;

    private Long valorCatastral;

    private String añoValor;

    @NotNull    
    private Long poligonoParcela;

    @NotNull
    private UUID idFinca;

    @NotNull
    private Long paraje;

}

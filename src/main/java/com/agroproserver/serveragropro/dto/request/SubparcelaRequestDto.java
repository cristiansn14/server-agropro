package com.agroproserver.serveragropro.dto.request;

import java.math.BigDecimal;

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
public class SubparcelaRequestDto {

    @NotBlank
    private String referenciaCatastral;

    @NotBlank
    private String subparcela;

    @NotBlank
    private String codigoCultivo;

    @NotBlank
    private String descripcionCultivo;  
    
    @NotBlank
    private String intensidadProductiva;

    @NotNull
    private BigDecimal superficie;

    

}

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
public class RecintoRequestDto {

    @NotNull
    private BigDecimal superficie;

    private String pendiente;

    private BigDecimal altitud; 

    @NotBlank
    private Long cultivo;

    private String porcentajeSubvencion;

    private Long superficieSubvencion;

    private Long coeficienteRegadio;

    private String incidencias;

    private String region;
}

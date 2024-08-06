package com.agroproserver.serveragropro.dto.request;

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
public class ParcelaConstruccionRequestDto {

    @NotBlank
    private String referenciaCatastral;

    @NotBlank
    private String usoPrincipal;

    @NotNull
    private Long superficie;

    @NotNull
    private Long escalera;

    @NotNull
    private Long planta;

    @NotNull
    private Long puerta;

    @NotNull
    private UUID idFinca;

    private String tipoReforma;

    private String fechaReforma;

}

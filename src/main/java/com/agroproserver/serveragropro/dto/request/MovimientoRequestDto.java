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
public class MovimientoRequestDto {

    @NotNull
    private long importe;

    @NotBlank
    private String concepto;

    @NotBlank
    private String tipo;

    @NotNull
    private UUID idFinca;
}

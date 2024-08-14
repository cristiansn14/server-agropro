package com.agroproserver.serveragropro.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FincaRequestDto {

    private UUID id;

    @NotBlank
    @Size(min = 3, max = 20)
    private String nombre;

    @NotNull
    private long onzas;

    @NotNull
    private long comunidad;

    @NotNull
    private long provincia;

    @NotNull
    private long municipio;
    
}

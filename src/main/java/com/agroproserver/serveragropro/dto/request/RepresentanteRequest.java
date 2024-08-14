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
public class RepresentanteRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido1;

    @NotBlank
    private String apellido2;

    @NotBlank
    private String dni;

    @NotBlank
    private String email;

    @NotBlank
    private String telefono;

    @NotNull
    private UUID idUsuario;
}

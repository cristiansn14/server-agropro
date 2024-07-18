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
public class UsuarioRequestDto {

    @NotNull
    private UUID id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido1;

    @NotBlank
    private String apellido2;

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    private String dni;

    private String telefono;

    private Long comunidad;

    private Long provincia;

    private Long municipio;

    private String direccion;

    private String codigoPostal;

    private String cuenta;

}

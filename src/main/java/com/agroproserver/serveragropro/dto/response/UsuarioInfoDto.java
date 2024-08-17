package com.agroproserver.serveragropro.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsuarioInfoDto {

    private UUID id;

    private String nombre;

    private String apellido1;

    private String apellido2;

    private String username;

    private String email;

    private String dni;

    private String rol;

}

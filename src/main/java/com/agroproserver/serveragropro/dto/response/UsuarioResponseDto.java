package com.agroproserver.serveragropro.dto.response;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsuarioResponseDto {

    private UUID id;

    private String nombre;

    private String apellido1;

    private String apellido2;

    private String username;

    private String email;

    private String dni;

    private String telefono;

    private String direccion;

    private String codigoPostal;

    private String cuenta;

    private Timestamp fechaAlta;

    private Timestamp fechaBaja;

    public UsuarioResponseDto (UUID id, String nombre, String apellido1, String apellido2, String username) {
        this.id = id;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.username = username;
    }
}

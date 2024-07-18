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

    private String comunidad;

    private String provincia;

    private String municipio;

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

    public UsuarioResponseDto (UUID id, String nombre, String apellido1, String apellido2, String username, 
                            String email, String dni, String telefono, String comunidad, String provincia, String municipio,
                            String direccion, String codigoPostal, String cuenta, Timestamp fechaAlta) {
        this.id = id;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.username = username;
        this.email = email;
        this.dni = dni;
        this.telefono = telefono;
        this.comunidad = comunidad;
        this.provincia = provincia;
        this.municipio = municipio;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.cuenta = cuenta;
        this.fechaAlta = fechaAlta;
    }
}

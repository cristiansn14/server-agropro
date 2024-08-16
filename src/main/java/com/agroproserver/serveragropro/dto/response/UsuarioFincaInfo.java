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
public class UsuarioFincaInfo {

    private UUID id;

    private long onzas;

    private String nombre;

    private String apellido1;

    private String apellido2;

    private String rol;

    private UUID idUsuario;

    private Timestamp fechaAlta;

    private Timestamp fechaModificacion;

    private Timestamp fechaBaja;

}

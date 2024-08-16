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
public class UsuarioParcelaResponseDto {

    private UUID id;
    
    private String nombre;

    private String apellido1;

    private String apellido2;

    private long participacion;

    private UUID idUsuario;

    private String referenciaCatastral;

    private Timestamp fechaAlta;

    private Timestamp fechaModificacion;

    private Timestamp fechaBaja;
}

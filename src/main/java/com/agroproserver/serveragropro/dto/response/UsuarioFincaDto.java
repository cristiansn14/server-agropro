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
public class UsuarioFincaDto {

    private UUID id;

    private long onzas;

    private UUID usuario;

    private UUID finca;

    private String rol;

    private Timestamp fechaAlta;

    private Timestamp fechaBaja;
}

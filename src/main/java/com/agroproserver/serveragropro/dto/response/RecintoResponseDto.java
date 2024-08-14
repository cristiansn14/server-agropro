package com.agroproserver.serveragropro.dto.response;

import java.math.BigDecimal;
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
public class RecintoResponseDto {

    private UUID id;

    private String recinto;

    private BigDecimal superficie;

    private String pendiente;

    private BigDecimal altitud; 

    private String cultivo;

    private String porcentajeSubvencion;

    private long superficieSubvencion;

    private long coeficienteRegadio;

    private String incidencias;

    private String region;

    private Timestamp fechaAlta;

    private Timestamp fechaModificacion;

    private Timestamp fechaBaja;

}

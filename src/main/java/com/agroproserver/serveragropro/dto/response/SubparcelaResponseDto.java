package com.agroproserver.serveragropro.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SubparcelaResponseDto {

    private UUID id;

    private String subparcela;

    private String intensidad;

    private BigDecimal superficie;

    private String cultivo;

}

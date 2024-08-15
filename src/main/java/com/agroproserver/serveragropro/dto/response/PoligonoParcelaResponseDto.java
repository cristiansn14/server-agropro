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
public class PoligonoParcelaResponseDto {

    private UUID id;
    
    private String poligono;

    private String parcela;

}

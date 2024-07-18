package com.agroproserver.serveragropro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PoligonoParcelaResponseDto {

    private long id;
    
    private long poligono;

    private String parcela;

}

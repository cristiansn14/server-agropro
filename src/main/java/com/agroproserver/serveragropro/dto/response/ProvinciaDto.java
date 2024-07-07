package com.agroproserver.serveragropro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProvinciaDto {

    private long id;
    
    private String nombre;

    private long idComunidad;

}

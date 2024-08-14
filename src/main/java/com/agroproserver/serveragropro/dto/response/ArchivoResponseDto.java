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
public class ArchivoResponseDto {

    private UUID id;

    private String name;

    private String type;
    
    private byte[] data;
}

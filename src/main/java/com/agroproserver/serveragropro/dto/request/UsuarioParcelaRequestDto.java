package com.agroproserver.serveragropro.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsuarioParcelaRequestDto {

    @NotNull
    private UUID usuario;

    @NotNull
    private long participacion;

}

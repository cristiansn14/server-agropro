package com.agroproserver.serveragropro.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ParcelaConstruccionDto {

    @NotNull
    private ParcelaConstruccionRequestDto parcelaConstruccion;

    @NotNull
    private List<UsuarioParcelaRequestDto> usuariosParcela;
}

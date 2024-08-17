package com.agroproserver.serveragropro.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ParcelaDto {

    private ParcelaRequestDto parcela;

    private List<SubparcelaRequestDto> subparcelas;

    private List<UsuarioParcelaRequestDto> usuariosParcela;

}

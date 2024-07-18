package com.agroproserver.serveragropro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SubparcelaRequestDto {

    @NotBlank
    private String intensidad;

    @NotNull
    private Long superficie;

    @NotBlank
    private Long cultivo;

}

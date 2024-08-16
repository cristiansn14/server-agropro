package com.agroproserver.serveragropro.payload.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangePasswordRequest {

    @NotNull
    private UUID idUsuario;
    
    @NotBlank
    private String password1;

    @NotBlank
    private String password2;

}

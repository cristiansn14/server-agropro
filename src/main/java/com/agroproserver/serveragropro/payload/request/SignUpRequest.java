package com.agroproserver.serveragropro.payload.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignUpRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String nombre;

    @NotBlank
    @Size(min = 3, max = 20)
    private String apellido1;

    @NotBlank
    @Size(min = 3, max = 20)
    private String apellido2;
    
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private String rol;

}

package com.agroproserver.serveragropro.payload.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtResponse {

    private String token;

    private UUID id;

    private String username;

    private String email;

    private List<String> roles;
    
}

package com.agroproserver.serveragropro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.payload.request.LoginRequest;
import com.agroproserver.serveragropro.payload.request.SignUpRequest;
import com.agroproserver.serveragropro.payload.response.JwtResponse;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.UsuarioRepository;
import com.agroproserver.serveragropro.security.jwt.JwtProvider;
import com.agroproserver.serveragropro.security.service.UserDetailsImpl;

import jakarta.validation.Valid;


@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<?> signup(@Valid SignUpRequest signUpRequest) {

        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("El nombre de usuario ya existe"));
        }
        if (usuarioRepository.existByEmail(signUpRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("El email ya está en uso"));
        }

        Usuario usuario = new Usuario(signUpRequest.getUsername(), passwordEncoder.encode(signUpRequest.getPassword()), signUpRequest.getEmail());

        usuarioRepository.save(usuario);

        emailService.sendSimpleMailMessage(signUpRequest.getEmail(), "Usuario registrado correctamente", 
        "Has sido registrado en nuestra plataforma, \nUsuario: " + signUpRequest.getUsername() +
        "\nContraseña: " + signUpRequest.getPassword() + 
        "\n\nSe recomienda rellenar los datos personales y modificar la contraseña.");

        return ResponseEntity.ok(new MessageResponse("Usuario registrado correctamente"));
    }

    public ResponseEntity<?> login(@Valid LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (usuario == null || usuario.getFechaBaja() != null){
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Su usuario ha sido eliminado de nuestra página, por favor póngase en contacto con el administrador"));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(
            new JwtResponse(jwt, userDetailsImpl.getId(), userDetailsImpl.getUsername(), userDetailsImpl.getEmail())
        );
    }
}

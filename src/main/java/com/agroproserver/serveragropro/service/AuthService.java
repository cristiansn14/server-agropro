package com.agroproserver.serveragropro.service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.agroproserver.serveragropro.model.ERol;
import com.agroproserver.serveragropro.model.Rol;
import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.payload.request.LoginRequest;
import com.agroproserver.serveragropro.payload.request.SignUpRequest;
import com.agroproserver.serveragropro.payload.response.JwtResponse;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.RolRepository;
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
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<?> signup(@Valid List<SignUpRequest> signUpRequests, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        for (SignUpRequest signUpRequest : signUpRequests) {
            if (usuarioRepository.existsByUsername(signUpRequest.getUsername())){
                return ResponseEntity.badRequest().body(new MessageResponse("El nombre de usuario ya existe"));               
            }
            if (usuarioRepository.existsByEmail(signUpRequest.getEmail())){
                return ResponseEntity.badRequest().body(new MessageResponse("El email ya está en uso"));
            }
    
            Usuario usuario = new Usuario(
                signUpRequest.getUsername(), 
                passwordEncoder.encode(signUpRequest.getPassword()), 
                signUpRequest.getEmail(),
                signUpRequest.getNombre(),
                signUpRequest.getApellido1(),
                signUpRequest.getApellido2()
            );
    
            String signUpRol = signUpRequest.getRol();
            Set<Rol> roles = new HashSet<>();
    
            if (signUpRol == null){           
                Rol userRole = rolRepository.findByRol(ERol.USUARIO)
                    .orElseThrow(() -> new RuntimeException("Error: El rol no ha sido encontrado."));
    
                roles.add(userRole);
            } else {
                switch (signUpRol) {
                    case "SUPERUSUARIO":
                        Rol superRol = rolRepository.findByRol(ERol.SUPERUSUARIO)
                            .orElseThrow(() -> new RuntimeException("Error: El rol no ha sido encontrado."));
                        
                        roles.add(superRol);
                        break;
                
                    default:
                        Rol userRole = rolRepository.findByRol(ERol.USUARIO)
                            .orElseThrow(() -> new RuntimeException("Error: El rol no ha sido encontrado."));
                        
                        roles.add(userRole);
                        break;
                }
            }
            usuario.setFechaAlta(new Timestamp(System.currentTimeMillis()));
            usuario.setRoles(roles);
            usuarioRepository.save(usuario);
    
            emailService.sendSimpleMailMessage(signUpRequest.getEmail(), "Usuario registrado correctamente", 
                "Has sido registrado en nuestra plataforma \n\nUsuario: " + signUpRequest.getUsername() +
                "\nContraseña: " + signUpRequest.getPassword() + 
                "\n\nSe recomienda rellenar los datos personales y modificar la contraseña.");           
        }

        return ResponseEntity.ok(new MessageResponse("Usuarios registrados correctamente"));
    }

    public ResponseEntity<?> login(@Valid LoginRequest loginRequest, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        
        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (usuario == null || usuario.getFechaBaja() != null){
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Su usuario ha sido eliminado de nuestra página, por favor póngase en contacto con el administrador"));
        }

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetailsImpl.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        return ResponseEntity.ok(
            new JwtResponse(jwt, userDetailsImpl.getId(), userDetailsImpl.getUsername(), userDetailsImpl.getEmail(), roles)
        );
    }
}

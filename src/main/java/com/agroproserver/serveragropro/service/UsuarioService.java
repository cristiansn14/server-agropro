package com.agroproserver.serveragropro.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.agroproserver.serveragropro.dto.request.UsuarioRequestDto;
import com.agroproserver.serveragropro.dto.response.UsuarioResponseDto;
import com.agroproserver.serveragropro.model.Comunidad;
import com.agroproserver.serveragropro.model.Municipio;
import com.agroproserver.serveragropro.model.Provincia;
import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.ComunidadRepository;
import com.agroproserver.serveragropro.repository.MunicipioRepository;
import com.agroproserver.serveragropro.repository.ProvinciaRepository;
import com.agroproserver.serveragropro.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ComunidadRepository comunidadRepository;

    @Autowired
    ProvinciaRepository provinciaRepository;

    @Autowired
    MunicipioRepository municipioRepository;


    public ResponseEntity<?> findById(UUID id) {
        
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se ha encontrado ningun usuario."));

        Comunidad comunidad = new Comunidad();
        Provincia provincia = new Provincia();
        Municipio municipio = new Municipio();        

        if (usuario.getComunidad() != null) {
            comunidad = comunidadRepository.findById(usuario.getComunidad().getId())
                .orElseThrow(() -> new RuntimeException("No se ha encontrado ninguna comunidad."));
        }
        if (usuario.getProvincia() != null) {
            provincia = provinciaRepository.findById(usuario.getProvincia().getId())
                .orElseThrow(() -> new RuntimeException("No se ha encontrado ninguna provincia."));
        }
        if (usuario.getMunicipio() != null) {
            municipio = municipioRepository.findById(usuario.getMunicipio().getId())
                .orElseThrow(() -> new RuntimeException("No se ha encontrado ningun municipio."));
        }
        
        UsuarioResponseDto usuarioDto = new UsuarioResponseDto (
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido1(),
            usuario.getApellido2(),
            usuario.getUsername(),
            usuario.getEmail(),
            usuario.getDni(),
            usuario.getTelefono(),
            comunidad.getNombre(),
            provincia.getNombre(),
            municipio.getNombre(),
            usuario.getDireccion(),
            usuario.getCodigoPostal(),
            usuario.getCuenta(),
            usuario.getFechaAlta()
        );  
        
        return ResponseEntity.ok(usuarioDto);
    }
    
    public Optional<Usuario> getByUsername(String username){
        return usuarioRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username){
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public void save(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public ResponseEntity<?> findUsuariosNotInFinca(UUID fincaId) {
        List<UsuarioResponseDto> usuariosDto = usuarioRepository.findUsuariosNotInFinca(fincaId).stream()
        .map(usuario -> new UsuarioResponseDto(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido1(),
            usuario.getApellido2(),
            usuario.getUsername()
        ))
        .collect(Collectors.toList());

        return ResponseEntity.ok(usuariosDto);
    }

    public ResponseEntity<?> findUsuariosInFinca(UUID fincaId) {
        List<UsuarioResponseDto> usuariosDto = usuarioRepository.findUsuariosInFinca(fincaId).stream()
        .map(usuario -> new UsuarioResponseDto(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido1(),
            usuario.getApellido2(),
            usuario.getUsername()
        ))
        .collect(Collectors.toList());

        return ResponseEntity.ok(usuariosDto);
    }

    public ResponseEntity<?> editarUsuario(UsuarioRequestDto usuarioRequestDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        } else if (usuarioRequestDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha modificado ningun usuario"));
        } else {
            Usuario usuario = usuarioRepository.findById(usuarioRequestDto.getId())
                .orElseThrow(() -> new RuntimeException("No se ha encontrado ningun usuario."));
            
            if (!usuarioRequestDto.getNombre().equals(usuario.getNombre())) {
                usuario.setNombre(usuarioRequestDto.getNombre());
            }
            if (!usuarioRequestDto.getApellido1().equals(usuario.getApellido1())) {
                usuario.setApellido1(usuarioRequestDto.getApellido1());
            }
            if (!usuarioRequestDto.getApellido2().equals(usuario.getApellido2())) {
                usuario.setApellido2(usuarioRequestDto.getApellido2());
            }
            if (!usuarioRequestDto.getUsername().equals(usuario.getUsername())) {
                if (usuarioRepository.existsByUsername(usuarioRequestDto.getUsername())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error, el nombre de usuario ya existe."));
                }
                usuario.setUsername(usuarioRequestDto.getUsername());         
            }
            if (!usuarioRequestDto.getEmail().equals(usuario.getEmail())) {
                if (usuarioRepository.existsByEmail(usuarioRequestDto.getEmail())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error, el email ya esta registrado."));
                }
                usuario.setEmail(usuarioRequestDto.getEmail());
            }
            if (!usuarioRequestDto.getDni().isBlank() && !usuarioRequestDto.getDni().equals(usuario.getDni())) {
                usuario.setDni(usuarioRequestDto.getDni());
            }
            if (!usuarioRequestDto.getTelefono().isBlank() && !usuarioRequestDto.getTelefono().equals(usuario.getTelefono())) {
                usuario.setTelefono(usuarioRequestDto.getTelefono());
            }
            if (!usuarioRequestDto.getDireccion().isBlank() && !usuarioRequestDto.getDireccion().equals(usuario.getDireccion())) {
                usuario.setDireccion(usuarioRequestDto.getDireccion());
            }
            if (!usuarioRequestDto.getCodigoPostal().isBlank() && !usuarioRequestDto.getCodigoPostal().equals(usuario.getCodigoPostal())) {
                usuario.setCodigoPostal(usuarioRequestDto.getCodigoPostal());
            }
            if (usuarioRequestDto.getComunidad() != null) {
                Comunidad comunidadDto = comunidadRepository.findById(usuarioRequestDto.getComunidad())
                    .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));
                if (comunidadDto != usuario.getComunidad()) {
                    usuario.setComunidad(comunidadDto);
                }               
            }
            if (usuarioRequestDto.getProvincia() != null) {
                Provincia provinciaDto = provinciaRepository.findById(usuarioRequestDto.getProvincia())
                    .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));
                if (provinciaDto != usuario.getProvincia()) {
                    usuario.setProvincia(provinciaDto);
                }               
            }
            if (usuarioRequestDto.getMunicipio() != null) {
                Municipio municipioDto = municipioRepository.findById(usuarioRequestDto.getMunicipio())
                    .orElseThrow(() -> new RuntimeException("Municipio no encontrado"));
                if (municipioDto != usuario.getMunicipio()) {
                    usuario.setMunicipio(municipioDto);
                }               
            }

            usuarioRepository.save(usuario);
            return ResponseEntity.ok(new MessageResponse("El usuario " + usuario.getUsername() + " se ha añadido correctamente"));
        }
    }
}

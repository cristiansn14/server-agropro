package com.agroproserver.serveragropro.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.agroproserver.serveragropro.dto.request.FincaRequestDto;
import com.agroproserver.serveragropro.dto.request.UsuarioFincaRequestDto;
import com.agroproserver.serveragropro.dto.response.FincaResponseDto;
import com.agroproserver.serveragropro.dto.response.UsuarioFincaDto;
import com.agroproserver.serveragropro.model.Comunidad;
import com.agroproserver.serveragropro.model.ERol;
import com.agroproserver.serveragropro.model.Finca;
import com.agroproserver.serveragropro.model.Municipio;
import com.agroproserver.serveragropro.model.Provincia;
import com.agroproserver.serveragropro.model.Rol;
import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.model.UsuarioFinca;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.ComunidadRepository;
import com.agroproserver.serveragropro.repository.FincaRepository;
import com.agroproserver.serveragropro.repository.MunicipioRepository;
import com.agroproserver.serveragropro.repository.ProvinciaRepository;
import com.agroproserver.serveragropro.repository.RolRepository;
import com.agroproserver.serveragropro.repository.UsuarioFincaRepository;
import com.agroproserver.serveragropro.repository.UsuarioRepository;

@Service
public class FincaService {

    @Autowired
    FincaRepository fincaRepository;

    @Autowired
    RolRepository rolRepository;
    
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioFincaRepository usuarioFincaRepository;

    @Autowired
    ComunidadRepository comunidadRepository;

    @Autowired
    ProvinciaRepository provinciaRepository;

    @Autowired
    MunicipioRepository municipioRepository;

    public ResponseEntity<?> guardarFinca(FincaRequestDto fincaDto, BindingResult bindingResult) {
        
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (fincaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha creado ninguna finca"));
        } else {
            if (fincaRepository.existsByNombre(fincaDto.getNombre())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error, la finca ya existe"));
            }

            Comunidad comunidad = comunidadRepository.findById(fincaDto.getComunidad())
                .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));
            Provincia provincia = provinciaRepository.findById(fincaDto.getProvincia())
                .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));
            Municipio municipio = municipioRepository.findById(fincaDto.getMunicipio())
                .orElseThrow(() -> new RuntimeException("Municipio no encontrado"));

            Finca finca = new Finca (
                fincaDto.getNombre(),
                fincaDto.getOnzas(),
                comunidad,
                provincia,
                municipio,
                new Timestamp(System.currentTimeMillis()) 
            );

            Finca nuevaFinca = fincaRepository.save(finca);

            Rol superUsuarioRol = rolRepository.findByRol(ERol.SUPERUSUARIO)
                .orElseThrow(() -> new RuntimeException("Rol SUPERUSUARIO no encontrado"));
            HashSet<Usuario> superUsuarios = usuarioRepository.findAllByRoles(superUsuarioRol);
            if (!superUsuarios.isEmpty()){
                superUsuarios.forEach(superUsuario -> {
                    UsuarioFinca usuarioFinca = new UsuarioFinca(
                        superUsuario,
                        nuevaFinca, 
                        superUsuarioRol,
                        0,
                        new Timestamp(System.currentTimeMillis())
                    );
                    usuarioFincaRepository.save(usuarioFinca);
                });
            }     
            
            return ResponseEntity.ok(new MessageResponse("La finca " + finca.getNombre() + " se ha añadido correctamente"));
        }
    }

    public ResponseEntity<?> addUsuariosFinca (List<UsuarioFincaRequestDto> usuariosFincaDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (usuariosFincaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningún usuario a la finca"));
        } else {
            usuariosFincaDto.forEach(usuarioFincaDto -> {               
                Usuario usuario = usuarioRepository.findById(usuarioFincaDto.getUsuario())
                    .orElseThrow(() -> new RuntimeException("No se ha encontrado ningun usuario"));
                Rol rol = new Rol();
                Finca finca = fincaRepository.findById(usuarioFincaDto.getFinca())
                    .orElseThrow(() -> new RuntimeException("No se ha encontrado ninguna finca."));

                switch (usuarioFincaDto.getRol()) {
                    case "ADMINISTRADOR":
                        rol = rolRepository.findByRol(ERol.ADMINISTRADOR)
                            .orElseThrow(() -> new RuntimeException("Rol ADMINISTRADOR no encontrado"));
                    break;
                
                    default:
                        rol = rolRepository.findByRol(ERol.PROPIETARIO)
                            .orElseThrow(() -> new RuntimeException("Rol ADMINISTRADOR no encontrado"));
                    break;
                }
                    
                UsuarioFinca usuarioFinca = new UsuarioFinca(
                    usuario,
                    finca,
                    rol,
                    usuarioFincaDto.getOnzas(),
                    new Timestamp(System.currentTimeMillis())
                );

                usuarioFincaRepository.save(usuarioFinca);
            });
            
            return ResponseEntity.ok(new MessageResponse("Se han añadido los usuarios introducidos correctamente"));
        }
    }

    public ResponseEntity<?> findAllFincasByUsuarioId (UUID idUsuario) {
        List<Finca> fincas = fincaRepository.findByUsuarioId(idUsuario);
        if (fincas != null) {
            List<FincaResponseDto> fincasDto = new ArrayList<>();
            fincas.forEach(finca -> {
                FincaResponseDto fincaDto = new FincaResponseDto(
                    finca.getId(),
                    finca.getNombre()
                );
                fincasDto.add(fincaDto);
            });

            return ResponseEntity.ok(fincasDto);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Usted no se encuentra registrado en ninguna finca"));
        }       
    }

    @Transactional
    public ResponseEntity<?> findUsuarioFincaByUsuarioIdAndFincaId (UUID idUsuario, UUID idFinca) {
        UsuarioFinca usuarioFinca = usuarioFincaRepository.findByUsuarioIdAndFincaId(idUsuario, idFinca);
        UsuarioFincaDto usuarioFincaDto = new UsuarioFincaDto(
            usuarioFinca.getId(),
            usuarioFinca.getOnzas(),
            usuarioFinca.getUsuario().getId(),
            usuarioFinca.getFinca().getId(),
            usuarioFinca.getRol().getRol().toString(),
            usuarioFinca.getFechaAlta(),
            usuarioFinca.getFechaBaja()
        );
        return ResponseEntity.ok(usuarioFincaDto);
    }

    public ResponseEntity<?> getOnzasDisponibles (UUID idFinca) {
        return ResponseEntity.ok(fincaRepository.getOnzasDisponibles(idFinca));
    }
}

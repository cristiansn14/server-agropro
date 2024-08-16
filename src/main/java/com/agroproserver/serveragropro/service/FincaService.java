package com.agroproserver.serveragropro.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.agroproserver.serveragropro.dto.request.FincaRequestDto;
import com.agroproserver.serveragropro.dto.request.UsuarioFincaRequestDto;
import com.agroproserver.serveragropro.dto.response.FincaResponseDto;
import com.agroproserver.serveragropro.dto.response.UsuarioFincaDto;
import com.agroproserver.serveragropro.dto.response.UsuarioFincaInfo;
import com.agroproserver.serveragropro.model.Comunidad;
import com.agroproserver.serveragropro.model.ERol;
import com.agroproserver.serveragropro.model.Finca;
import com.agroproserver.serveragropro.model.Municipio;
import com.agroproserver.serveragropro.model.Parcela;
import com.agroproserver.serveragropro.model.ParcelaConstruccion;
import com.agroproserver.serveragropro.model.Provincia;
import com.agroproserver.serveragropro.model.Rol;
import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.model.UsuarioFinca;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.ComunidadRepository;
import com.agroproserver.serveragropro.repository.FincaRepository;
import com.agroproserver.serveragropro.repository.MunicipioRepository;
import com.agroproserver.serveragropro.repository.ParcelaConstruccionRepository;
import com.agroproserver.serveragropro.repository.ParcelaRepository;
import com.agroproserver.serveragropro.repository.ProvinciaRepository;
import com.agroproserver.serveragropro.repository.RolRepository;
import com.agroproserver.serveragropro.repository.UsuarioFincaRepository;
import com.agroproserver.serveragropro.repository.UsuarioRepository;

@Service
public class FincaService {

    @Autowired
    FincaRepository fincaRepository;

    @Autowired
    ParcelaRepository parcelaRepository;

    @Autowired
    ParcelaConstruccionRepository parcelaConstruccionRepository;

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

    @Transactional
    public ResponseEntity<?> findById(UUID idFinca) {
        
        Finca finca = fincaRepository.findById(idFinca)
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

        FincaResponseDto fincaDto = new FincaResponseDto(
            finca.getId(),
            finca.getNombre(),
            finca.getOnzas(),
            finca.getComunidad().getId(),
            finca.getProvincia().getId(),
            finca.getMunicipio().getId(),
            finca.getFechaAlta(),
            finca.getFechaModificacion(),
            finca.getFechaBaja()
        );

        return ResponseEntity.ok(fincaDto);
    }

    @Transactional
    public ResponseEntity<?> getParcelasByIdFinca(UUID idFinca) {
        
        List<Parcela> parcelas = parcelaRepository.findByFincaId(idFinca);
        List<ParcelaConstruccion> parcelasConstruccion = parcelaConstruccionRepository.findByFincaId(idFinca);
        List<String> referenciaParcelas = new ArrayList<>();

        if(!parcelas.isEmpty()){           
            for (Parcela parcela : parcelas) {
                referenciaParcelas.add(parcela.getReferenciaCatastral());
            }           
        } 
        if(!parcelasConstruccion.isEmpty()){
            for (ParcelaConstruccion parcelaConstruccion : parcelasConstruccion) {
                referenciaParcelas.add(parcelaConstruccion.getReferenciaCatastral());
            }
        }
        return ResponseEntity.ok(referenciaParcelas);               
    }

    @Transactional
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
            if (fincaDto.getOnzas() <= 0) {
                return ResponseEntity.badRequest().body(new MessageResponse("Las onzas no pueden ser 0"));
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

            fincaRepository.save(finca);

            Rol superUsuarioRol = rolRepository.findByRol(ERol.SUPERUSUARIO)
                .orElseThrow(() -> new RuntimeException("Rol SUPERUSUARIO no encontrado"));
            HashSet<Usuario> superUsuarios = usuarioRepository.findAllByRoles(superUsuarioRol);
            if (!superUsuarios.isEmpty()){
                superUsuarios.forEach(superUsuario -> {
                    UsuarioFinca usuarioFinca = new UsuarioFinca(
                        superUsuario,
                        finca, 
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

    @Transactional
    public ResponseEntity<?> editarFinca (FincaRequestDto fincaDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        } else if (fincaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha modificado ninguna finca"));
        } else {
            if (fincaDto.getOnzas() <= 0) {
                return ResponseEntity.badRequest().body(new MessageResponse("Las onzas no pueden ser 0"));
            }

            Boolean actF = false;
            Finca finca = fincaRepository.findById(fincaDto.getId())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
            
            if (!fincaDto.getNombre().isEmpty() && !fincaDto.getNombre().equals(finca.getNombre())) {
                finca.setNombre(fincaDto.getNombre());
                actF = true;               
            }

            if (fincaDto.getOnzas() != finca.getOnzas()) {
                finca.setOnzas(fincaDto.getOnzas());
                actF = true; 
            }

            Comunidad comunidad = comunidadRepository.findById(fincaDto.getComunidad())
                .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));
            if (comunidad != finca.getComunidad()) {
                finca.setComunidad(comunidad);
                actF = true; 
            }

            Provincia provincia = provinciaRepository.findById(fincaDto.getProvincia())
                    .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));
            if (provincia != finca.getProvincia()) {
                finca.setProvincia(provincia);
                actF = true; 
            }    
            
            Municipio municipio = municipioRepository.findById(fincaDto.getMunicipio())
                    .orElseThrow(() -> new RuntimeException("Municipio no encontrado"));
            if (municipio != finca.getMunicipio()) {
                finca.setMunicipio(municipio);
                actF = true; 
            }

            if (actF) { 
                finca.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                fincaRepository.save(finca);
                return ResponseEntity.ok(new MessageResponse("La finca " + finca.getNombre() + " se ha modificado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }   
            
        }
    }    

    @Transactional
    public ResponseEntity<?> addUsuariosFinca (List<UsuarioFincaRequestDto> usuariosFincaDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (usuariosFincaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningún usuario a la finca"));
        } else {
            for (UsuarioFincaRequestDto usuarioFincaDto : usuariosFincaDto) {
                if (usuarioFincaDto.getOnzas() <= 0) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Las onzas no pueden ser 0"));
                }               
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
                            .orElseThrow(() -> new RuntimeException("Rol PROPIETARIO no encontrado"));
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
            }
            
            return ResponseEntity.ok(new MessageResponse("Se han añadido los usuarios introducidos correctamente"));
        }
    }

    @Transactional
    public ResponseEntity<?> editarUsuarioFinca (UsuarioFincaDto usuarioFincaDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (usuarioFincaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningún usuario a la finca"));
        } else {
            if (usuarioFincaDto.getOnzas() <= 0) {
                return ResponseEntity.badRequest().body(new MessageResponse("Las onzas no pueden ser 0"));
            }

            Boolean actUF = false;
            UsuarioFinca usuarioFinca = usuarioFincaRepository.findById(usuarioFincaDto.getId())
                .orElseThrow(() -> new RuntimeException("Usuario finca no encontrado"));
            
            if (usuarioFincaDto.getOnzas() != usuarioFinca.getOnzas()) {
                usuarioFinca.setOnzas(usuarioFincaDto.getOnzas());
                actUF = true;
            }

            if (!usuarioFincaDto.getRol().equals(usuarioFinca.getRol().getRol().toString())) {
                Rol rol = new Rol();

                switch (usuarioFincaDto.getRol()) {
                    case "ADMINISTRADOR":
                        rol = rolRepository.findByRol(ERol.ADMINISTRADOR)
                            .orElseThrow(() -> new RuntimeException("Rol ADMINISTRADOR no encontrado"));
                    break;
                
                    default:
                        rol = rolRepository.findByRol(ERol.PROPIETARIO)
                            .orElseThrow(() -> new RuntimeException("Rol PROPIETARIO no encontrado"));
                    break;
                }

                usuarioFinca.setRol(rol);
                actUF = true;
            }

            if (actUF) {
                usuarioFinca.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                usuarioFincaRepository.save(usuarioFinca);
                return ResponseEntity.ok(new MessageResponse("El usuario " + usuarioFinca.getUsuario().getUsername() + " de la finca " + usuarioFinca.getFinca().getNombre() + " se ha modificado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
            
        }   
    }

    @Transactional
    public ResponseEntity<?> eliminarUsuarioFinca (UsuarioFincaDto usuarioFincaDto) {

        UsuarioFinca usuarioFinca = usuarioFincaRepository.findById(usuarioFincaDto.getId())
            .orElseThrow(() -> new RuntimeException("Usuario finca no encontrado"));
        
        usuarioFinca.setFechaBaja(new Timestamp(System.currentTimeMillis()));
        usuarioFincaRepository.save(usuarioFinca);
        
        return ResponseEntity.ok(new MessageResponse("El usuario " + usuarioFinca.getUsuario().getUsername() + " de la finca " + usuarioFinca.getFinca().getNombre() + " se ha eliminado correctamente"));
    }

    @Transactional
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

    @Transactional
    public ResponseEntity<?> findUsuariosFincaByFincaId (UUID idFinca) {

        List<UsuarioFinca> usuariosFinca = usuarioFincaRepository.findUsuariosFincaByFincaId(idFinca);
        List<UsuarioFincaInfo> usuariosFincaInfo = new ArrayList<>();

        for (UsuarioFinca usuarioFinca : usuariosFinca) {
            UsuarioFincaInfo usuarioFincaInfo = new UsuarioFincaInfo(
                usuarioFinca.getId(),
                usuarioFinca.getOnzas(),                
                usuarioFinca.getUsuario().getNombre(),
                usuarioFinca.getUsuario().getApellido1(),
                usuarioFinca.getUsuario().getApellido2(),
                usuarioFinca.getRol().getRol().toString(),
                usuarioFinca.getUsuario().getId(),
                usuarioFinca.getFechaAlta(),
                usuarioFinca.getFechaModificacion(),
                usuarioFinca.getFechaBaja()
            );

            usuariosFincaInfo.add(usuarioFincaInfo);
        }

        return ResponseEntity.ok(usuariosFincaInfo);
    }

    @Transactional
    public ResponseEntity<?> findUsuarioFincaById (UUID idUsuarioFinca) {
        
        UsuarioFinca usuarioFinca = usuarioFincaRepository.findById(idUsuarioFinca)
            .orElseThrow(() -> new RuntimeException("No se ha encontrado ningun usuario asociado a la finca"));
        
        UsuarioFincaInfo usuarioFincaInfo = new UsuarioFincaInfo(
            usuarioFinca.getId(),
            usuarioFinca.getOnzas(),                
            usuarioFinca.getUsuario().getNombre(),
            usuarioFinca.getUsuario().getApellido1(),
            usuarioFinca.getUsuario().getApellido2(),
            usuarioFinca.getRol().getRol().toString(),
            usuarioFinca.getUsuario().getId(),
            usuarioFinca.getFechaAlta(),
            usuarioFinca.getFechaModificacion(),
            usuarioFinca.getFechaBaja()
        );

        return ResponseEntity.ok(usuarioFincaInfo);
    }

    @Transactional
    public ResponseEntity<?> getOnzasDisponibles (UUID idFinca) {
        return ResponseEntity.ok(fincaRepository.getOnzasDisponibles(idFinca));
    }
}

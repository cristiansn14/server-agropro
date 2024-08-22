package com.agroproserver.serveragropro.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import com.agroproserver.serveragropro.dto.request.RepresentanteRequest;
import com.agroproserver.serveragropro.dto.request.UsuarioRequestDto;
import com.agroproserver.serveragropro.dto.response.RepresentanteResponse;
import com.agroproserver.serveragropro.dto.response.UsuarioInfoDto;
import com.agroproserver.serveragropro.dto.response.UsuarioResponseDto;
import com.agroproserver.serveragropro.model.Archivo;
import com.agroproserver.serveragropro.model.Comunidad;
import com.agroproserver.serveragropro.model.Municipio;
import com.agroproserver.serveragropro.model.Parcela;
import com.agroproserver.serveragropro.model.ParcelaConstruccion;
import com.agroproserver.serveragropro.model.Provincia;
import com.agroproserver.serveragropro.model.Representante;
import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.model.UsuarioFinca;
import com.agroproserver.serveragropro.model.UsuarioParcela;
import com.agroproserver.serveragropro.model.Finca;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.ArchivoRepository;
import com.agroproserver.serveragropro.repository.ComunidadRepository;
import com.agroproserver.serveragropro.repository.FincaRepository;
import com.agroproserver.serveragropro.repository.MunicipioRepository;
import com.agroproserver.serveragropro.repository.ParcelaConstruccionRepository;
import com.agroproserver.serveragropro.repository.ParcelaRepository;
import com.agroproserver.serveragropro.repository.ProvinciaRepository;
import com.agroproserver.serveragropro.repository.RepresentanteRepository;
import com.agroproserver.serveragropro.repository.RolRepository;
import com.agroproserver.serveragropro.repository.UsuarioFincaRepository;
import com.agroproserver.serveragropro.repository.UsuarioParcelaRepository;
import com.agroproserver.serveragropro.repository.UsuarioRepository;
import com.agroproserver.serveragropro.utils.ImageUtils;

import java.io.IOException;
import java.sql.Timestamp;

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

    @Autowired
    ArchivoRepository archivoRepository;

    @Autowired
    RepresentanteRepository representanteRepository;

    @Autowired
    FincaRepository fincaRepository;

    @Autowired
    ParcelaRepository parcelaRepository;

    @Autowired
    ParcelaConstruccionRepository parcelaConstruccionRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    UsuarioFincaRepository usuarioFincaRepository;

    @Autowired
    UsuarioParcelaRepository usuarioParcelaRepository;

    @Transactional
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
            usuario.getFechaAlta(),
            usuario.getFechaBaja()
        );  
        
        return ResponseEntity.ok(usuarioDto);
    }
    
    @Transactional
    public Optional<Usuario> getByUsername(String username){
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public ResponseEntity<byte[]> getFotoPerfil(UUID idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("No se ha encontrado ningun usuario."));

        if (usuario.getFoto() != null) {
            Archivo archivo = usuario.getFoto();
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getName() + "\"")
                .body(ImageUtils.decompressImage(archivo.getData()));
        } else {
            try {
                Resource imgFile = new ClassPathResource("static/default-avatar.png");
                byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"default-avatar.png\"")
                    .body(bytes);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Transactional
    public boolean existsByUsername(String username){
        return usuarioRepository.existsByUsername(username);
    }

    @Transactional
    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
    public void save(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    public ResponseEntity<?> editarUsuario(UsuarioRequestDto usuarioRequestDto, MultipartFile foto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        } else if (usuarioRequestDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha modificado ningun usuario"));
        } else {
            Boolean actU = false;
            Usuario usuario = usuarioRepository.findById(usuarioRequestDto.getId())
                .orElseThrow(() -> new RuntimeException("No se ha encontrado ningun usuario."));
            
            if (!usuarioRequestDto.getNombre().isBlank() && !usuarioRequestDto.getNombre().equals(usuario.getNombre())) {
                usuario.setNombre(usuarioRequestDto.getNombre());
                actU = true;
            }

            if (!usuarioRequestDto.getApellido1().isBlank() && !usuarioRequestDto.getApellido1().equals(usuario.getApellido1())) {
                usuario.setApellido1(usuarioRequestDto.getApellido1());
                actU = true;
            }

            if (!usuarioRequestDto.getApellido2().isBlank() && !usuarioRequestDto.getApellido2().equals(usuario.getApellido2())) {
                usuario.setApellido2(usuarioRequestDto.getApellido2());
                actU = true;
            }
            
            if (!usuarioRequestDto.getUsername().isBlank() && !usuarioRequestDto.getUsername().equals(usuario.getUsername())) {
                if (usuarioRepository.existsByUsername(usuarioRequestDto.getUsername())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error, el nombre de usuario ya existe."));
                }
                usuario.setUsername(usuarioRequestDto.getUsername());
                actU = true;       
            }

            if (!usuarioRequestDto.getEmail().isBlank() && !usuarioRequestDto.getEmail().equals(usuario.getEmail())) {
                if (usuarioRepository.existsByEmail(usuarioRequestDto.getEmail())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error, el email ya esta registrado."));
                }
                usuario.setEmail(usuarioRequestDto.getEmail());
                actU = true;
            }

            if (usuarioRequestDto.getDni() != null) {
                if (!usuarioRequestDto.getDni().equals(usuario.getDni())) {
                    usuario.setDni(usuarioRequestDto.getDni());
                    actU = true;
                }
            } else if (usuario.getDni() != null) {
                usuario.setDni(null);
                actU = true;
            }

            if (usuarioRequestDto.getTelefono() != null) {
                if (!usuarioRequestDto.getTelefono().equals(usuario.getTelefono())) {
                    usuario.setTelefono(usuarioRequestDto.getTelefono());
                    actU = true;
                }
            } else if (usuario.getTelefono() != null) {
                usuario.setTelefono(null);
                actU = true;
            }           

            if (usuarioRequestDto.getDireccion() != null) {
                if (!usuarioRequestDto.getDireccion().equals(usuario.getDireccion())) {
                    usuario.setDireccion(usuarioRequestDto.getDireccion());
                    actU = true;
                }
            } else if (usuario.getDireccion() != null) {
                usuario.setDireccion(null);
                actU = true;
            }           

            if (usuarioRequestDto.getCodigoPostal() != null) {
                if (!usuarioRequestDto.getCodigoPostal().equals(usuario.getCodigoPostal())) {
                    usuario.setCodigoPostal(usuarioRequestDto.getCodigoPostal());
                    actU = true;
                }
            } else if (usuario.getCodigoPostal() != null) {
                usuario.setCodigoPostal(null);
                actU = true;
            }

            if (usuarioRequestDto.getCuenta() != null) {
                if (!usuarioRequestDto.getCuenta().equals(usuario.getCuenta())) {
                    usuario.setCuenta(usuarioRequestDto.getCuenta());
                    actU = true;
                }
            } else if (usuario.getCuenta() != null) {
                usuario.setCuenta(null);
                actU = true;
            }
            
            if (usuarioRequestDto.getComunidad() != null) {
                Comunidad comunidad = comunidadRepository.findById(usuarioRequestDto.getComunidad())
                    .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));
                if (comunidad != usuario.getComunidad()) {
                    usuario.setComunidad(comunidad);
                    actU = true;
                }               
            }

            if (usuarioRequestDto.getProvincia() != null) {
                Provincia provincia = provinciaRepository.findById(usuarioRequestDto.getProvincia())
                    .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));
                if (provincia != usuario.getProvincia()) {
                    usuario.setProvincia(provincia);
                    actU = true;
                }               
            }

            if (usuarioRequestDto.getMunicipio() != null) {
                Municipio municipio = municipioRepository.findById(usuarioRequestDto.getMunicipio())
                    .orElseThrow(() -> new RuntimeException("Municipio no encontrado"));
                if (municipio != usuario.getMunicipio()) {
                    usuario.setMunicipio(municipio);
                    actU = true;
                }               
            }

            if (foto != null && !foto.isEmpty()) {
                try {
                    byte[] data = foto.getBytes();
                    Archivo archivo = Archivo.builder()
                                .name(foto.getOriginalFilename())
                                .type(foto.getContentType())
                                .data(ImageUtils.compressImage(data))
                                .build();
                    if (usuario.getFoto() != null) {
                        archivoRepository.delete(usuario.getFoto());
                    }        
                    usuario.setFoto(archivo);
                    archivoRepository.save(archivo);
                    actU = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error al procesar la imagen", e);
                }
            }

            if (actU) {
                usuarioRepository.save(usuario);
                return ResponseEntity.ok(new MessageResponse("El usuario " + usuario.getUsername() + " se ha modificado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        }
    }

    @Transactional
    public ResponseEntity<?> añadirRepresentante (RepresentanteRequest representanteDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        } else if (representanteDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningun representante"));
        } else {
            Usuario usuario = usuarioRepository.findById(representanteDto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Representante representante = Representante.builder()
                                .nombre(representanteDto.getNombre())
                                .apellido1(representanteDto.getApellido1())
                                .apellido2(representanteDto.getApellido2())
                                .dni(representanteDto.getDni())
                                .email(representanteDto.getEmail())
                                .telefono(representanteDto.getTelefono())
                                .usuario(usuario)
                                .fechaAlta(new Timestamp(System.currentTimeMillis()))
                                .build();

            representanteRepository.save(representante);
            return ResponseEntity.ok(new MessageResponse("El representante " + representante.getNombre() + " se ha añadido correctamente"));
        }
    }

    @Transactional
    public ResponseEntity<?> editarRepresentante (RepresentanteResponse representanteDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        } else if (representanteDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningun representante"));
        } else {
            Boolean actR = false;
            Representante representante = representanteRepository.findById(representanteDto.getId())
                .orElseThrow(() -> new RuntimeException("Representante no encontrado"));

            if (representanteDto.getNombre() != null) {
                if (!representanteDto.getNombre().equals(representante.getNombre())) {
                    representante.setNombre(representanteDto.getNombre());
                    actR = true;
                }
            }

            if (representanteDto.getApellido1() != null) {
                if (!representanteDto.getApellido1().equals(representante.getApellido1())) {
                    representante.setApellido1(representanteDto.getApellido1());
                    actR = true;
                }
            }
            
            if (representanteDto.getApellido2() != null) {
                if (!representanteDto.getApellido2().equals(representante.getApellido2())) {
                    representante.setApellido2(representanteDto.getApellido2());
                    actR = true;
                }
            }
            
            if (representanteDto.getEmail() != null) {
                if (!representanteDto.getEmail().equals(representante.getEmail())) {
                    representante.setEmail(representanteDto.getEmail());
                    actR = true;
                }
            }
            
            if (representanteDto.getTelefono() != null) {
                if (!representanteDto.getTelefono().equals(representante.getEmail())) {
                    representante.setTelefono(representanteDto.getTelefono());
                    actR = true;
                }
            }
            
            if (representanteDto.getDni() != null) {
                if (!representanteDto.getDni().equals(representante.getDni())) {
                    representante.setDni(representanteDto.getDni());
                    actR = true;
                }  
            }

            if (actR) {
                representanteRepository.save(representante);
                return ResponseEntity.ok(new MessageResponse("El representante " + representante.getNombre() + " se ha editado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
            
        }
    }

    @Transactional
    public ResponseEntity<?> eliminarRepresentante (RepresentanteResponse representanteResponse) {

        Representante representante = representanteRepository.findById(representanteResponse.getId())
            .orElseThrow(() -> new RuntimeException("Representante no encontrado"));
        
        representante.setFechaBaja(new Timestamp(System.currentTimeMillis()));
        representanteRepository.save(representante);

        return ResponseEntity.ok(new MessageResponse("El representante " + representante.getNombre() + " ha sido eliminado correctamente"));
    }

    @Transactional
    public ResponseEntity<?> findRepresentantesByIdUsuario (UUID idUsuario) {

        List<RepresentanteResponse> representantes = representanteRepository.findByUsuarioId(idUsuario).stream()
            .map(representante -> new RepresentanteResponse(
                representante.getId(),
                representante.getNombre(),
                representante.getApellido1(),
                representante.getApellido2(),
                representante.getEmail(),
                representante.getDni(),
                representante.getTelefono(),
                representante.getUsuario().getId(),
                representante.getFechaAlta(),
                representante.getFechaBaja()
            ))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(representantes);
    }

    @Transactional
    public ResponseEntity<?> findRepresentanteById (UUID idRepresentante) {

        Representante representante = representanteRepository.findById(idRepresentante)
            .orElseThrow(() -> new RuntimeException("Representante no encontrado"));
        RepresentanteResponse representanteDto = new RepresentanteResponse(
                representante.getId(),
                representante.getNombre(),
                representante.getApellido1(),
                representante.getApellido2(),
                representante.getEmail(),
                representante.getDni(),
                representante.getTelefono(),
                representante.getUsuario().getId(),
                representante.getFechaAlta(),
                representante.getFechaBaja()
        );

        return ResponseEntity.ok(representanteDto);
    }

    @Transactional
    public ResponseEntity<?> findUsuariosByFincaAndNotInParcela (UUID idFinca, String referenciaCatastral) {

        List<UsuarioResponseDto> usuariosDto = usuarioRepository.findUsuariosByFincaAndNotInParcela(idFinca, referenciaCatastral).stream()
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

    @Transactional
    public ResponseEntity<?> findAllUsuariosAlta() {

        List<UsuarioInfoDto> usuariosInfo = usuarioRepository.findAllUsuariosAlta().stream()
            .map(usuario -> new UsuarioInfoDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido1(),
                usuario.getApellido2(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getDni(),
                usuario.obtenerNombreRol(usuario)
            ))
        .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosInfo);
    }

    @Transactional
    public ResponseEntity<?> findAllUsuariosBaja() {

        List<UsuarioInfoDto> usuariosInfo = usuarioRepository.findAllUsuariosBaja().stream()
            .map(usuario -> new UsuarioInfoDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido1(),
                usuario.getApellido2(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getDni(),
                usuario.obtenerNombreRol(usuario)
            ))
        .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosInfo);
    }

    @Transactional
    public ResponseEntity<?> darBajaUsuario (UUID idUsuario) {
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Finca> fincas = fincaRepository.findByUsuarioId(idUsuario);

        for (Finca finca : fincas) {
            UsuarioFinca usuarioFinca = usuarioFincaRepository.findByUsuarioIdAndFincaIdNotBaja(idUsuario, finca.getId());
            if (usuarioFinca != null) {
                usuarioFinca.setFechaBaja(new Timestamp(System.currentTimeMillis()));
                usuarioFincaRepository.save(usuarioFinca);
            }
        }

        List<Parcela> parcelas = parcelaRepository.findParcelasByUsuarioIdAndFechaBajaIsNull(idUsuario);
        for (Parcela parcela : parcelas) {
            UsuarioParcela usuarioParcela = usuarioParcelaRepository.findByUsuarioIdAndParcelaReferenciaCatastral(idUsuario, parcela.getReferenciaCatastral());
            if (usuarioParcela != null && usuarioParcela.getFechaBaja() == null) {
                usuarioParcela.setFechaBaja(new Timestamp(System.currentTimeMillis()));
                usuarioParcelaRepository.save(usuarioParcela);
            }
        }

        List<ParcelaConstruccion> parcelasConstruccion = parcelaConstruccionRepository.findParcelasConstruccionByUsuarioIdAndFechaBajaIsNull(idUsuario);
        for (ParcelaConstruccion parcela : parcelasConstruccion) {
            UsuarioParcela usuarioParcela = usuarioParcelaRepository.findByUsuarioIdAndParcelaReferenciaCatastral(idUsuario, parcela.getReferenciaCatastral());
            if (usuarioParcela != null && usuarioParcela.getFechaBaja() == null) {
                usuarioParcela.setFechaBaja(new Timestamp(System.currentTimeMillis()));
                usuarioParcelaRepository.save(usuarioParcela);
            }
        }

        usuario.setFechaBaja(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.ok(new MessageResponse("El usuario " + usuario.getUsername() + " ha sido eliminado correctamente"));
    }

    @Transactional
    public ResponseEntity<?> darAltaUsuario (UUID idUsuario) {
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setFechaBaja(null);
        return ResponseEntity.ok(new MessageResponse("El usuario " + usuario.getUsername() + " se ha dado de alta correctamente"));
    }
}

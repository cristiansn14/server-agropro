package com.agroproserver.serveragropro.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.agroproserver.serveragropro.dto.request.ParcelaConstruccionDto;
import com.agroproserver.serveragropro.dto.request.ParcelaDto;
import com.agroproserver.serveragropro.dto.request.RecintoRequestDto;
import com.agroproserver.serveragropro.dto.request.SubparcelaRequestDto;
import com.agroproserver.serveragropro.dto.response.ParcelaResponseDto;
import com.agroproserver.serveragropro.dto.response.RecintoResponseDto;
import com.agroproserver.serveragropro.dto.response.SubparcelaResponseDto;
import com.agroproserver.serveragropro.dto.response.UsuarioParcelaResponseDto;
import com.agroproserver.serveragropro.dto.response.ConstruccionDto;
import com.agroproserver.serveragropro.model.Finca;
import com.agroproserver.serveragropro.model.Paraje;
import com.agroproserver.serveragropro.model.Parcela;
import com.agroproserver.serveragropro.model.PoligonoParcela;
import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.model.UsuarioParcela;
import com.agroproserver.serveragropro.model.Subparcela;
import com.agroproserver.serveragropro.model.Recinto;
import com.agroproserver.serveragropro.model.Cultivo;
import com.agroproserver.serveragropro.model.ParcelaConstruccion;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.CultivoRepository;
import com.agroproserver.serveragropro.repository.FincaRepository;
import com.agroproserver.serveragropro.repository.ParajeRepository;
import com.agroproserver.serveragropro.repository.ParcelaConstruccionRepository;
import com.agroproserver.serveragropro.repository.ParcelaRepository;
import com.agroproserver.serveragropro.repository.PoligonoParcelaRepository;
import com.agroproserver.serveragropro.repository.RecintoRepository;
import com.agroproserver.serveragropro.repository.SubparcelaRepository;
import com.agroproserver.serveragropro.repository.UsuarioParcelaRepository;
import com.agroproserver.serveragropro.repository.UsuarioRepository;

import java.text.ParseException;

@Service
public class ParcelaService {

    @Autowired
    ParcelaRepository parcelaRepository;

    @Autowired
    FincaRepository fincaRepository;

    @Autowired
    PoligonoParcelaRepository poligonoParcelaRepository;

    @Autowired
    ParajeRepository parajeRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioParcelaRepository usuarioParcelaRepository;

    @Autowired
    CultivoRepository cultivoRepository;

    @Autowired
    SubparcelaRepository subparcelaRepository;

    @Autowired
    RecintoRepository recintoRepository;

    @Autowired
    ParcelaConstruccionRepository parcelaConstruccionRepository;


    public ResponseEntity<?> guardarParcela(ParcelaDto parcelaDto, BindingResult bindingResult) {
        
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (parcelaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha creado ninguna parcela"));
        } 
        if (parcelaDto.getUsuariosParcela().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningun propietario"));
        } 
        if (parcelaDto.getSubparcelas().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ninguna subparcela"));
        }if (parcelaDto.getRecintos().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningun recinto"));
        } else {
            if (parcelaRepository.existsById(parcelaDto.getParcela().getReferenciaCatastral())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error, la parcela ya esta registrada"));
            }

            Finca finca = fincaRepository.findById(parcelaDto.getParcela().getIdFinca())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

            PoligonoParcela poligonoParcela = poligonoParcelaRepository.findById(parcelaDto.getParcela().getPoligonoParcela())
                .orElseThrow(() -> new RuntimeException("Poligono-parcela no encontrado"));

            Paraje paraje = parajeRepository.findById(parcelaDto.getParcela().getParaje())
                .orElseThrow(() -> new RuntimeException("Paraje no encontrado"));

            Parcela parcela = new Parcela (
                parcelaDto.getParcela().getReferenciaCatastral(),
                parcelaDto.getParcela().getClase(),
                parcelaDto.getParcela().getUsoPrincipal(),
                parcelaDto.getParcela().getSuperficie(),
                parcelaDto.getParcela().getValorSuelo() != null ? parcelaDto.getParcela().getValorSuelo() : 0,
                parcelaDto.getParcela().getValorConstruccion() != null ? parcelaDto.getParcela().getValorConstruccion() : 0,
                parcelaDto.getParcela().getValorCatastral() != null ? parcelaDto.getParcela().getValorCatastral() : 0,
                parcelaDto.getParcela().getAñoValor() != null ? parcelaDto.getParcela().getAñoValor() : "",
                poligonoParcela,
                finca,
                paraje,
                new Timestamp(System.currentTimeMillis())
            );

            parcelaRepository.save(parcela);

            if (parcelaDto.getSubparcelas().size() == 1) {
                crearYGuardarSubparcela(parcelaDto.getSubparcelas().get(0), parcela, "0");
            } else {
                for (int i = 0; i < parcelaDto.getSubparcelas().size(); i++) {
                    SubparcelaRequestDto subparcelaDto = parcelaDto.getSubparcelas().get(i);
                    String identificador = indexToAlphabet(i);
                    crearYGuardarSubparcela(subparcelaDto, parcela, identificador);
                }
            }

            for (int i = 0; i < parcelaDto.getRecintos().size(); i++) {
                RecintoRequestDto recintoDto = parcelaDto.getRecintos().get(i);
                Cultivo cultivo = cultivoRepository.findById(recintoDto.getCultivo())
                    .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
                Recinto recinto = new Recinto(
                    parcela,
                    String.valueOf(i),
                    recintoDto.getSuperficie(),
                    recintoDto.getPendiente() != null ? recintoDto.getPendiente() : "",
                    recintoDto.getAltitud() != null ? recintoDto.getAltitud() : BigDecimal.ZERO, 
                    cultivo,
                    recintoDto.getPorcentajeSubvencion() != null ? recintoDto.getPorcentajeSubvencion() : "",
                    recintoDto.getSuperficieSubvencion() != null ? recintoDto.getSuperficieSubvencion() : 0L, 
                    recintoDto.getCoeficienteRegadio() != null ? recintoDto.getCoeficienteRegadio() : 0L,
                    recintoDto.getIncidencias() != null ? recintoDto.getIncidencias() : "",
                    recintoDto.getRegion() != null ? recintoDto.getRegion() : "",
                    new Timestamp(System.currentTimeMillis())
                );


                recintoRepository.save(recinto);
            }

            parcelaDto.getUsuariosParcela().forEach(usuarioParcelaDto -> {
                Usuario usuario = usuarioRepository.findById(usuarioParcelaDto.getUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                UsuarioParcela usuarioParcela = new UsuarioParcela (
                    usuario,
                    parcela,
                    usuarioParcelaDto.getParticipacion(),
                    new Timestamp(System.currentTimeMillis())
                );
                usuarioParcelaRepository.save(usuarioParcela);
            });
            
            

            return ResponseEntity.ok(new MessageResponse("La parcela se ha añadido correctamente."));
        }
    }

    private void crearYGuardarSubparcela(SubparcelaRequestDto dto, Parcela parcela, String identificador) {
        Cultivo cultivo = cultivoRepository.findById(dto.getCultivo())
            .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
    
        Subparcela subparcela = new Subparcela(
            parcela,
            identificador,
            dto.getIntensidad(),
            dto.getSuperficie(),
            cultivo,
            new Timestamp(System.currentTimeMillis())
        );
    
        subparcelaRepository.save(subparcela);      
    }

    private String indexToAlphabet(int index) {
        return String.valueOf((char) ('a' + index));
    }

    public ResponseEntity<?> guardarParcelaConstruccion(ParcelaConstruccionDto parcelaDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (parcelaDto.getParcelaConstruccion() == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha creado ninguna parcela construccion"));
        } 
        if (parcelaDto.getUsuariosParcela().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningun propietario"));
        } else {
            if (parcelaRepository.existsById(parcelaDto.getParcelaConstruccion().getReferenciaCatastral())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error, la parcela ya esta registrada"));
            }

            Finca finca = fincaRepository.findById(parcelaDto.getParcelaConstruccion().getIdFinca())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd--MM--yyyy");
            Date fechaReforma = new Date();
            try {
                fechaReforma = dateFormat.parse(parcelaDto.getParcelaConstruccion().getFechaReforma());
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("Formato de fecha no válido");
            }
            ParcelaConstruccion parcelaConstruccion = new ParcelaConstruccion (
                parcelaDto.getParcelaConstruccion().getReferenciaCatastral(),
                parcelaDto.getParcelaConstruccion().getUsoPrincipal(),
                parcelaDto.getParcelaConstruccion().getSuperficie(),
                parcelaDto.getParcelaConstruccion().getEscalera(),
                parcelaDto.getParcelaConstruccion().getPlanta(),
                parcelaDto.getParcelaConstruccion().getPuerta(),
                parcelaDto.getParcelaConstruccion().getTipoReforma(),
                fechaReforma,
                finca,
                new Timestamp(System.currentTimeMillis())
            );

            parcelaConstruccionRepository.save(parcelaConstruccion);

            parcelaDto.getUsuariosParcela().forEach(usuarioParcelaDto -> {
                Usuario usuario = usuarioRepository.findById(usuarioParcelaDto.getUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                UsuarioParcela usuarioParcela = new UsuarioParcela (
                    usuario,
                    parcelaConstruccion,
                    usuarioParcelaDto.getParticipacion(),
                    new Timestamp(System.currentTimeMillis())
                );
                usuarioParcelaRepository.save(usuarioParcela);
            });

            return ResponseEntity.ok(new MessageResponse("La parcela construccion se ha añadido correctamente."));
        }
    }

    public ResponseEntity<?> findParcelaByReferenciaCatastral(String referenciaCatastral) {
        if (parcelaRepository.existsById(referenciaCatastral)) {
            Parcela parcela = parcelaRepository.findById(referenciaCatastral)
                .orElseThrow(() -> new RuntimeException("Parcela no encontrada"));

            ParcelaResponseDto parcelaDto = new ParcelaResponseDto(
                parcela.getReferenciaCatastral(),
                parcela.getClase(),
                parcela.getUsoPrincipal(),
                parcela.getSuperficie(),
                parcela.getValorSuelo(),
                parcela.getValorConstruccion(),
                parcela.getValorCatastral(),
                parcela.getAñoValor(),
                parcela.getPoligonoParcela().getPoligono(),
                parcela.getPoligonoParcela().getParcela(),
                parcela.getFinca().getId(),
                parcela.getParaje().getNombre(),
                parcela.getFechaAlta(),
                parcela.getFechaModificacion(),
                parcela.getFechaBaja()               
            );

            return ResponseEntity.ok(parcelaDto);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("La parcela no esta registrada"));
        }
    }

    public ResponseEntity<?> findParcelaConstruccionByReferenciaCatastral(String referenciaCatastral) {
        if (parcelaConstruccionRepository.existsById(referenciaCatastral)) {
            ParcelaConstruccion parcelaConstruccion = parcelaConstruccionRepository.findById(referenciaCatastral)
                .orElseThrow(() -> new RuntimeException("Parcela construccion no encontrada"));

                ConstruccionDto parcelaConstruccionDto = new ConstruccionDto (
                parcelaConstruccion.getReferenciaCatastral(),
                parcelaConstruccion.getClase(),
                parcelaConstruccion.getUsoPrincipal(),
                parcelaConstruccion.getSuperficie(),
                parcelaConstruccion.getEscalera(),
                parcelaConstruccion.getPlanta(),
                parcelaConstruccion.getPuerta(),
                parcelaConstruccion.getTipoReforma(),
                parcelaConstruccion.getFechaReforma(),
                parcelaConstruccion.getFechaAlta(),
                parcelaConstruccion.getFechaModificacion(),
                parcelaConstruccion.getFechaBaja()               
            );

            return ResponseEntity.ok(parcelaConstruccionDto);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("La parcela construccion no esta registrada"));
        }
    }

    public ResponseEntity<?> findSubparcelasByReferenciaCatastral(String referenciaCatastral) {       
        List<SubparcelaResponseDto> subparcelas = subparcelaRepository.findByParcelaReferenciaCatastral(referenciaCatastral)
            .stream().map(subparcela -> new SubparcelaResponseDto(
                subparcela.getId(),
                subparcela.getSubparcela(),
                subparcela.getIntensidad(),
                subparcela.getSuperficie(),
                subparcela.getCultivo().getCodigo().concat(" - ").concat(subparcela.getCultivo().getDescripcion()),
                subparcela.getFechaAlta(),
                subparcela.getFechaModificacion(),
                subparcela.getFechaBaja()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(subparcelas);
    }

    public ResponseEntity<?> findRecintosByReferenciaCatastral(String referenciaCatastral) {       
        List<RecintoResponseDto> recintos = recintoRepository.findByParcelaReferenciaCatastral(referenciaCatastral)
            .stream().map(recinto -> new RecintoResponseDto(
                recinto.getId(),
                recinto.getRecinto(),
                recinto.getSuperficie(),
                recinto.getPendiente(),
                recinto.getAltitud(),
                recinto.getCultivo().getCodigo().concat(" - ").concat(recinto.getCultivo().getDescripcion()),
                recinto.getPorcentajeSubvencion(),
                recinto.getSuperficieSubvencion(),
                recinto.getCoeficienteRegadio(),
                recinto.getIncidencias(),
                recinto.getRegion(),
                recinto.getFechaAlta(),
                recinto.getFechaModificacion(),
                recinto.getFechaBaja()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(recintos);
    }

    public ResponseEntity<?> findUsuariosInParcela(String referenciaCatastral) {
        List<UsuarioParcelaResponseDto> usuariosParcelaDto = 
            usuarioParcelaRepository.findUsuarioParcelaByReferenciaCatastral(referenciaCatastral);

        return ResponseEntity.ok(usuariosParcelaDto);
    }
}

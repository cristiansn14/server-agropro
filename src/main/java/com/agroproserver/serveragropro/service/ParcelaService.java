package com.agroproserver.serveragropro.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.agroproserver.serveragropro.dto.request.ParcelaConstruccionRequestDto;
import com.agroproserver.serveragropro.dto.request.ParcelaDto;
import com.agroproserver.serveragropro.dto.request.RecintoRequestDto;
import com.agroproserver.serveragropro.dto.request.SubparcelaRequestDto;
import com.agroproserver.serveragropro.dto.request.UsuarioParcelaRequestDto;
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
                    recintoDto.getAltitud() != null ? recintoDto.getAltitud() : 0,
                    cultivo,
                    recintoDto.getPorcentajeSubvencion() != null ? recintoDto.getPorcentajeSubvencion() : "",
                    recintoDto.getSuperficieSubvencion() != null ? recintoDto.getSuperficieSubvencion() : 0,
                    recintoDto.getCoeficienteRegadio() != null ? recintoDto.getCoeficienteRegadio() : 0,
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

    public ResponseEntity<?> guardarParcelaConstruccion(ParcelaConstruccionRequestDto parcelaDto,  List<UsuarioParcelaRequestDto> usuariosParcelaDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (parcelaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha creado ninguna parcela construccion"));
        } 
        if (usuariosParcelaDto.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningun propietario"));
        } else {
            if (parcelaRepository.existsById(parcelaDto.getReferenciaCatastral())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error, la parcela ya esta registrada"));
            }

            Finca finca = fincaRepository.findById(parcelaDto.getIdFinca())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

            ParcelaConstruccion parcelaConstruccion = new ParcelaConstruccion (
                parcelaDto.getReferenciaCatastral(),
                parcelaDto.getUsoPrincipal(),
                parcelaDto.getSuperficie(),
                parcelaDto.getEscalera(),
                parcelaDto.getPlanta(),
                parcelaDto.getPuerta(),
                parcelaDto.getTipoReforma(),
                parcelaDto.getFechaReforma(),
                finca,
                new Timestamp(System.currentTimeMillis())
            );

            parcelaConstruccionRepository.save(parcelaConstruccion);

            usuariosParcelaDto.forEach(usuarioParcelaDto -> {
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
}

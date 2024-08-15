package com.agroproserver.serveragropro.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.agroproserver.serveragropro.dto.request.ParcelaConstruccionDto;
import com.agroproserver.serveragropro.dto.request.ParcelaDto;
import com.agroproserver.serveragropro.dto.request.SubparcelaRequestDto;
import com.agroproserver.serveragropro.dto.response.ParcelaResponseDto;
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
import com.agroproserver.serveragropro.model.Cultivo;
import com.agroproserver.serveragropro.model.ParcelaConstruccion;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.CultivoRepository;
import com.agroproserver.serveragropro.repository.FincaRepository;
import com.agroproserver.serveragropro.repository.ParajeRepository;
import com.agroproserver.serveragropro.repository.ParcelaConstruccionRepository;
import com.agroproserver.serveragropro.repository.ParcelaRepository;
import com.agroproserver.serveragropro.repository.PoligonoParcelaRepository;
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
        }
        if (parcelaDto.getParcela() == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ninguna parcela"));
        } else {
            if (parcelaRepository.existsById(parcelaDto.getParcela().getReferenciaCatastral())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error, la parcela ya esta registrada"));
            }

            Finca finca = fincaRepository.findById(parcelaDto.getParcela().getIdFinca())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

            PoligonoParcela poligonoParcela = new PoligonoParcela();
            if (poligonoParcelaRepository.existsByProvinciaIdAndMunicipioIdAndPoligonoAndParcela(finca.getProvincia().getId(), finca.getMunicipio().getIdMunicipio(), parcelaDto.getParcela().getPoligono(), parcelaDto.getParcela().getParcela())) {
                poligonoParcela = poligonoParcelaRepository.findByProvinciaIdAndMunicipioIdAndPoligonoAndParcela(finca.getProvincia().getId(), finca.getMunicipio().getIdMunicipio(), parcelaDto.getParcela().getPoligono(), parcelaDto.getParcela().getParcela());
            } else {
                poligonoParcela = PoligonoParcela.builder()
                    .poligono(parcelaDto.getParcela().getPoligono())
                    .parcela(parcelaDto.getParcela().getParcela())
                    .provincia(finca.getProvincia())
                    .municipio(finca.getMunicipio())
                    .build();
                poligonoParcelaRepository.save(poligonoParcela);
            }          

            Paraje paraje = new Paraje();
            if (parajeRepository.existsByNombre(parcelaDto.getParcela().getParaje())) {
                paraje = parajeRepository.findByNombre(parcelaDto.getParcela().getParaje());
            } else {
                paraje = Paraje.builder()
                    .nombre(parcelaDto.getParcela().getParaje())
                    .provincia(finca.getProvincia())
                    .municipio(finca.getMunicipio())
                    .build();
                parajeRepository.save(paraje);
            }

            Parcela parcela = new Parcela (
                parcelaDto.getParcela().getReferenciaCatastral(),
                parcelaDto.getParcela().getClase(),
                parcelaDto.getParcela().getUsoPrincipal(),
                parcelaDto.getParcela().getSuperficie(),
                poligonoParcela,
                finca,
                paraje,
                new Timestamp(System.currentTimeMillis())
            );

            parcelaRepository.save(parcela);

            for (SubparcelaRequestDto subparcelaDto : parcelaDto.getSubparcelas()) {
                Cultivo cultivo = new Cultivo();
                if (cultivoRepository.existsByCodigo(subparcelaDto.getCodigoCultivo())) {
                    cultivo = cultivoRepository.findByCodigo(subparcelaDto.getCodigoCultivo());
                } else {
                    cultivo = Cultivo.builder()
                        .codigo(subparcelaDto.getCodigoCultivo())
                        .descripcion(subparcelaDto.getDescripcionCultivo())
                        .build();
                    cultivoRepository.save(cultivo);
                }
                Subparcela subparcela = Subparcela.builder()
                    .parcela(parcela)
                    .subparcela(subparcelaDto.getSubparcela())
                    .cultivo(cultivo)
                    .intensidad(subparcelaDto.getIntensidadProductiva())
                    .superficie(subparcelaDto.getSuperficie())
                    .build();
                subparcelaRepository.save(subparcela);
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

    public ResponseEntity<?> actualizarParcela(ParcelaDto parcelaDto) {

        if (parcelaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha creado ninguna parcela"));
        }
        if (parcelaDto.getParcela() == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ninguna parcela"));
        }
        if (parcelaDto.getSubparcelas().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ninguna subparcela"));
        } else {
            Boolean actP = false;
            Boolean actS = false;

            Parcela parcela = parcelaRepository.findById(parcelaDto.getParcela().getReferenciaCatastral())
                .orElseThrow(() -> new RuntimeException("Parcela no encontrada"));

            if (!parcela.getPoligonoParcela().getPoligono().equals(parcelaDto.getParcela().getPoligono())) {
                PoligonoParcela poligonoParcela = poligonoParcelaRepository.findById(parcela.getPoligonoParcela().getId())
                    .orElseThrow(() -> new RuntimeException("PoligonoParcela no encontrado"));
                poligonoParcela.setPoligono(parcelaDto.getParcela().getPoligono());
                poligonoParcelaRepository.save(poligonoParcela);

                parcela.setPoligonoParcela(poligonoParcela);
                actP = true;
            }
            if (!parcela.getPoligonoParcela().getParcela().equals(parcelaDto.getParcela().getParcela())) {
                PoligonoParcela poligonoParcela = poligonoParcelaRepository.findById(parcela.getPoligonoParcela().getId())
                    .orElseThrow(() -> new RuntimeException("PoligonoParcela no encontrado"));
                poligonoParcela.setPoligono(parcelaDto.getParcela().getParcela());
                poligonoParcelaRepository.save(poligonoParcela);
                
                parcela.setPoligonoParcela(poligonoParcela);
                actP = true;
            }
            if (!parcela.getParaje().getNombre().equals(parcelaDto.getParcela().getParaje())) {
                Paraje paraje = parajeRepository.findById(parcela.getParaje().getId())
                    .orElseThrow(() -> new RuntimeException("Paraje no encontrado"));
                paraje.setNombre(parcelaDto.getParcela().getParaje());
                parajeRepository.save(paraje);

                parcela.setParaje(paraje);
                actP = true;
            }
            if (!parcela.getClase().equals(parcelaDto.getParcela().getClase())) {
                parcela.setClase(parcelaDto.getParcela().getClase());
                actP = true;
            }
            if (!parcela.getUsoPrincipal().equals(parcelaDto.getParcela().getUsoPrincipal())) {
                parcela.setUsoPrincipal(parcelaDto.getParcela().getUsoPrincipal());
                actP = true;
            }
            if (parcela.getSuperficie() != parcelaDto.getParcela().getSuperficie()) {
                parcela.setSuperficie(parcelaDto.getParcela().getSuperficie());
                actP = true;
            }
            if (actP) {
                parcela.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                parcelaRepository.save(parcela);
            }

            List<Subparcela> subparcelas = subparcelaRepository.findByParcelaReferenciaCatastral(parcela.getReferenciaCatastral());

            for (Subparcela subparcela : subparcelas) {
                SubparcelaRequestDto subparcelaDto = parcelaDto.getSubparcelas().stream()
                    .filter(sp -> sp.getSubparcela().equals(subparcela.getSubparcela()))
                    .findFirst()
                    .orElse(null);

                if (subparcelaDto != null) {
                    if (!subparcela.getIntensidad().equals(subparcelaDto.getIntensidadProductiva())) {
                        subparcela.setIntensidad(subparcelaDto.getIntensidadProductiva());
                        actS = true;
                    }
                    if (subparcela.getSuperficie().compareTo(subparcelaDto.getSuperficie()) != 0) {
                        subparcela.setSuperficie(subparcelaDto.getSuperficie());
                        actS = true;
                    }
                    if (!subparcela.getCultivo().getCodigo().equals(subparcelaDto.getCodigoCultivo())) {
                        Cultivo cultivo = cultivoRepository.findById(subparcela.getId())
                            .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
                        cultivo.setCodigo(subparcelaDto.getCodigoCultivo());
                        cultivoRepository.save(cultivo);

                        subparcela.setCultivo(cultivo);
                        actS = true;
                    }
                    if (!subparcela.getCultivo().getDescripcion().equals(subparcelaDto.getDescripcionCultivo())) {
                        Cultivo cultivo = cultivoRepository.findById(subparcela.getId())
                            .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
                        cultivo.setDescripcion(subparcelaDto.getDescripcionCultivo());
                        cultivoRepository.save(cultivo);
                        
                        subparcela.setCultivo(cultivo);
                        actS = true;
                    }
                    if (actP) {
                        subparcela.setParcela(parcela);
                        actS = true;
                    }
                    if (actS) {
                        parcela.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                        parcelaRepository.save(parcela);

                        subparcela.setParcela(parcela);
                        subparcelaRepository.save(subparcela);
                    }
                } else {
                    SubparcelaRequestDto subparcelaNueva = parcelaDto.getSubparcelas().stream()
                            .filter(sp -> !sp.getSubparcela().equals(subparcela.getSubparcela()))
                            .findFirst()
                            .orElse(null);
                    parcelaDto.getSubparcelas().remove(subparcelaNueva);

                    subparcelaRepository.delete(subparcela);

                    Cultivo cultivo = new Cultivo();
                    if (cultivoRepository.existsByCodigo(subparcelaNueva.getCodigoCultivo())) {
                        cultivo = cultivoRepository.findByCodigo(subparcelaNueva.getCodigoCultivo());
                    } else {
                        cultivo = Cultivo.builder()
                            .codigo(subparcelaNueva.getCodigoCultivo())
                            .descripcion(subparcelaNueva.getDescripcionCultivo())
                            .build();
                        cultivoRepository.save(cultivo);
                    }

                    parcela.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                    parcelaRepository.save(parcela);

                    Subparcela nuevaSubparcela = Subparcela.builder()
                                .parcela(parcela)
                                .subparcela(subparcelaNueva.getSubparcela())
                                .intensidad(subparcelaNueva.getIntensidadProductiva())
                                .cultivo(cultivo)
                                .superficie(subparcelaNueva.getSuperficie())
                                .build();
                      
                    subparcelaRepository.save(nuevaSubparcela);
                    actS = true;
                }
            }
        }
        return ResponseEntity.ok(new MessageResponse("La parcela se ha añadido correctamente."));
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
                subparcela.getCultivo().getCodigo().concat(" ").concat(subparcela.getCultivo().getDescripcion())
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(subparcelas);
    }

    public ResponseEntity<?> findUsuariosInParcela(String referenciaCatastral) {
        List<UsuarioParcelaResponseDto> usuariosParcelaDto = 
            usuarioParcelaRepository.findUsuarioParcelaByReferenciaCatastral(referenciaCatastral);

        return ResponseEntity.ok(usuariosParcelaDto);
    }
}

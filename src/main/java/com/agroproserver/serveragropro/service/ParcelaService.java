package com.agroproserver.serveragropro.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.agroproserver.serveragropro.dto.request.ParcelaConstruccionDto;
import com.agroproserver.serveragropro.dto.request.ParcelaConstruccionRequestDto;
import com.agroproserver.serveragropro.dto.request.ParcelaDto;
import com.agroproserver.serveragropro.dto.request.ParcelaRequestDto;
import com.agroproserver.serveragropro.dto.request.SubparcelaRequestDto;
import com.agroproserver.serveragropro.dto.request.UsuarioParcelaDto;
import com.agroproserver.serveragropro.dto.request.UsuarioParcelaRequestDto;
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

    private static final String REFERENCIA_CATASTRAL_FORMATO = "^[0-9A-Z]{20}$";

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
            String ref = parcelaDto.getParcela().getReferenciaCatastral().replaceAll("\\s+", "").toUpperCase();

            if (!isValidReferenciaCatastral(ref)) {
                System.out.println("\n \n HOLAL \n \n");
                return ResponseEntity.badRequest().body(new MessageResponse("La referencia catastral no es valida"));
            }

            if (parcelaRepository.existsById(ref)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error, la parcela ya esta registrada"));
            }

            if (parcelaConstruccionRepository.existsById(ref)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error, la parcela construccion ya esta registrada"));
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
                ref,
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

            for (UsuarioParcelaRequestDto usuarioParcelaDto : parcelaDto.getUsuariosParcela()) {
                if (usuarioParcelaDto.getParticipacion() <= 0 || usuarioParcelaDto.getParticipacion() > 100) {
                    return ResponseEntity.badRequest().body(new MessageResponse("La participacion tiene que estar entre 0 y 100"));
                }
                Usuario usuario = usuarioRepository.findById(usuarioParcelaDto.getUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                UsuarioParcela usuarioParcela = new UsuarioParcela (
                    usuario,
                    parcela,
                    usuarioParcelaDto.getParticipacion(),
                    new Timestamp(System.currentTimeMillis())
                );
                usuarioParcelaRepository.save(usuarioParcela);
            }

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
            if (parcela.getSuperficie().compareTo(parcelaDto.getParcela().getSuperficie()) != 0) {
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
                    subparcelaRepository.delete(subparcela);
                    parcela.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                    parcelaRepository.save(parcela);
                    actS = true;
                }
            }
            for (SubparcelaRequestDto subparcelaDto : parcelaDto.getSubparcelas()) {
                Subparcela subparcela = subparcelas.stream()
                    .filter(sp -> sp.getSubparcela().equals(subparcelaDto.getSubparcela()))
                    .findFirst()
                    .orElse(null);
                if (subparcela == null) {
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

                    parcela.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                    parcelaRepository.save(parcela);

                    Subparcela nuevaSubparcela = Subparcela.builder()
                                .parcela(parcela)
                                .subparcela(subparcelaDto.getSubparcela())
                                .intensidad(subparcelaDto.getIntensidadProductiva())
                                .cultivo(cultivo)
                                .superficie(subparcelaDto.getSuperficie())
                                .build();
                      
                    subparcelaRepository.save(nuevaSubparcela);
                    actS = true;
                }
            }
            if (actP || actS) {
                return ResponseEntity.ok(new MessageResponse("La parcela se ha actualizado correctamente."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        } 
    }

    @Transactional
    public ResponseEntity<?> editarParcela (ParcelaRequestDto parcelaDto, BindingResult bindingResult) {
        
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (parcelaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha creado ninguna parcela"));
        } else {
            Boolean actP = false;

            Parcela parcela = parcelaRepository.findById(parcelaDto.getReferenciaCatastral())
                .orElseThrow(() -> new RuntimeException("Parcela no encontrada"));

            if (parcelaDto.getValorSuelo() != null) {
                if (!parcelaDto.getValorSuelo().equals(parcela.getValorSuelo())) {
                    parcela.setValorSuelo(parcelaDto.getValorSuelo());
                    actP = true;
                }
            } else {
                if (parcela.getValorSuelo() != null) {
                    parcela.setValorSuelo(null);
                    actP = true;
                }               
            }

            if (parcelaDto.getValorConstruccion() != null) {
                if (!parcelaDto.getValorConstruccion().equals(parcela.getValorConstruccion())) {
                    parcela.setValorConstruccion(parcelaDto.getValorConstruccion());
                    actP = true;
                }
            } else {
                if (parcela.getValorConstruccion() != null) {
                    parcela.setValorConstruccion(null);
                    actP = true;
                }                
            }
            
            if (parcelaDto.getValorCatastral() != null) {
                if (!parcelaDto.getValorCatastral().equals(parcela.getValorCatastral())) {
                    parcela.setValorCatastral(parcelaDto.getValorCatastral());
                    actP = true;
                }
            } else {
                if (parcela.getValorCatastral() != null) {
                    parcela.setValorCatastral(null);
                    actP = true;
                }
            }
            
            if (parcelaDto.getAnoValor() != null) {
                if (!parcelaDto.getAnoValor().equals(parcela.getAñoValor())) {
                    parcela.setAñoValor(parcelaDto.getAnoValor());
                    actP = true;
                }
            } else {
                if (parcela.getAñoValor() != null) {
                    parcela.setAñoValor(null);
                    actP = true;
                }
            }

            if (actP) {
                parcela.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                parcelaRepository.save(parcela);
                return ResponseEntity.ok(new MessageResponse("La parcela se ha actualizado correctamente."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        }
    }

    @Transactional
    public ResponseEntity<?> darAltaParcela(String referenciaCatastral) {

        if (parcelaRepository.existsById(referenciaCatastral)) {
            Parcela parcela = parcelaRepository.findById(referenciaCatastral)
                .orElseThrow(() -> new RuntimeException("Parcela no encontrada"));
            
            parcela.setFechaBaja(null);
            parcelaRepository.save(parcela);
            return ResponseEntity.ok(new MessageResponse("La parcela se ha dado de alta correctamente."));
        }
        if (parcelaConstruccionRepository.existsById(referenciaCatastral)) {
            ParcelaConstruccion parcelaConstruccion = parcelaConstruccionRepository.findById(referenciaCatastral)
                .orElseThrow(() -> new RuntimeException("Parcela construccion no encontrada"));
            
            parcelaConstruccion.setFechaBaja(null);
            return ResponseEntity.ok(new MessageResponse("La parcela construccion se ha dado de alta correctamente."));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Transactional
    public ResponseEntity<?> darBajaParcela(String referenciaCatastral) {

        if (parcelaRepository.existsById(referenciaCatastral)) {
            Parcela parcela = parcelaRepository.findById(referenciaCatastral)
                .orElseThrow(() -> new RuntimeException("Parcela no encontrada"));
            
            List<UsuarioParcela> usuariosParcela = usuarioParcelaRepository.findUsuariosParcelaByReferenciaCatastral(referenciaCatastral);

            for (UsuarioParcela usuarioParcela : usuariosParcela) {
                usuarioParcela.setFechaBaja(new Timestamp(System.currentTimeMillis()));
                usuarioParcelaRepository.save(usuarioParcela);
            }
            
            parcela.setFechaBaja(new Timestamp(System.currentTimeMillis()));
            parcelaRepository.save(parcela);
            return ResponseEntity.ok(new MessageResponse("La parcela se ha dado de baja correctamente."));
        }
        if (parcelaConstruccionRepository.existsById(referenciaCatastral)) {
            ParcelaConstruccion parcelaConstruccion = parcelaConstruccionRepository.findById(referenciaCatastral)
                .orElseThrow(() -> new RuntimeException("Parcela construccion no encontrada"));
            
            List<UsuarioParcela> usuariosParcela = usuarioParcelaRepository.findUsuariosParcelaConstruccionByReferenciaCatastral(referenciaCatastral);

            for (UsuarioParcela usuarioParcela : usuariosParcela) {
                usuarioParcela.setFechaBaja(new Timestamp(System.currentTimeMillis()));
                usuarioParcelaRepository.save(usuarioParcela);
            }

            parcelaConstruccion.setFechaBaja(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.ok(new MessageResponse("La parcela construccion se ha dado de baja correctamente."));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Transactional
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
            String ref = parcelaDto.getParcelaConstruccion().getReferenciaCatastral().replaceAll("\\s+", "").toUpperCase();

            if (!isValidReferenciaCatastral(ref)) {
                return ResponseEntity.badRequest().body(new MessageResponse("La referencia catastral no es valida"));
            }

            if (parcelaRepository.existsById(ref)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error, la parcela ya esta registrada"));
            }

            if (parcelaConstruccionRepository.existsById(ref)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error, la parcela construccion ya esta registrada"));
            }

            Finca finca = fincaRepository.findById(parcelaDto.getParcelaConstruccion().getIdFinca())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));      
            
            ParcelaConstruccion parcelaConstruccion = new ParcelaConstruccion (
                parcelaDto.getParcelaConstruccion().getReferenciaCatastral(),
                parcelaDto.getParcelaConstruccion().getUsoPrincipal(),
                parcelaDto.getParcelaConstruccion().getSuperficie(),
                parcelaDto.getParcelaConstruccion().getEscalera(),
                parcelaDto.getParcelaConstruccion().getPlanta(),
                parcelaDto.getParcelaConstruccion().getPuerta(),
                parcelaDto.getParcelaConstruccion().getTipoReforma(),
                parcelaDto.getParcelaConstruccion().getFechaReforma(),
                finca,
                new Timestamp(System.currentTimeMillis())
            );

            parcelaConstruccionRepository.save(parcelaConstruccion);

            for (UsuarioParcelaRequestDto usuarioParcelaDto : parcelaDto.getUsuariosParcela()) {
                if (usuarioParcelaDto.getParticipacion() <= 0 || usuarioParcelaDto.getParticipacion() > 100) {
                    return ResponseEntity.badRequest().body(new MessageResponse("La participacion tiene que estar entre 0 y 100"));
                }
                Usuario usuario = usuarioRepository.findById(usuarioParcelaDto.getUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                UsuarioParcela usuarioParcela = new UsuarioParcela (
                    usuario,
                    parcelaConstruccion,
                    usuarioParcelaDto.getParticipacion(),
                    new Timestamp(System.currentTimeMillis())
                );
                usuarioParcelaRepository.save(usuarioParcela);
            }
            return ResponseEntity.ok(new MessageResponse("La parcela construccion se ha añadido correctamente."));
        }
    }

    @Transactional
    public ResponseEntity<?> editarParcelaConstruccion(ParcelaConstruccionRequestDto parcelaDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (parcelaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha creado ninguna parcela construccion"));
        }  else {
            Boolean actP = false;
            ParcelaConstruccion parcelaConstruccion = parcelaConstruccionRepository.findById(parcelaDto.getReferenciaCatastral())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada")); 

            if (parcelaDto.getEscalera() != null) {
                if (!parcelaDto.getEscalera().equals(parcelaConstruccion.getEscalera())) {
                    parcelaConstruccion.setEscalera(parcelaDto.getEscalera());
                    actP = true;
                }
            }
            if (parcelaDto.getPlanta() != null) {
                if (!parcelaDto.getPlanta().equals(parcelaConstruccion.getPlanta())) {
                    parcelaConstruccion.setPlanta(parcelaDto.getPlanta());
                    actP = true;
                }
            }
            if (parcelaDto.getPuerta() != null) {
                if (!parcelaDto.getPuerta().equals(parcelaConstruccion.getPuerta())) {
                    parcelaConstruccion.setPuerta(parcelaDto.getPuerta());
                    actP = true;
                }
            }
            if (parcelaDto.getTipoReforma() != null) {
                if (!parcelaDto.getTipoReforma().equals(parcelaConstruccion.getTipoReforma())) {
                    parcelaConstruccion.setTipoReforma(parcelaDto.getTipoReforma());
                    actP = true;
                }
            }
            if (parcelaDto.getFechaReforma() != null) {
                if (!parcelaDto.getFechaReforma().equals(parcelaConstruccion.getFechaReforma())) {
                    parcelaConstruccion.setFechaReforma(parcelaDto.getFechaReforma());
                    actP = true;
                }
            }

            if (actP) {
                parcelaConstruccion.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                parcelaConstruccionRepository.save(parcelaConstruccion);
                return ResponseEntity.ok(new MessageResponse("La parcela se ha actualizado correctamente."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        }
    }

    public ResponseEntity<?> crearUsuarioParcela (List<UsuarioParcelaDto> usuariosParcelaDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }
        if (usuariosParcelaDto.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha creado ningun usuario parcela"));
        } else {
            Parcela parcela = new Parcela();
            ParcelaConstruccion parcelaConstruccion = new ParcelaConstruccion();
            if (parcelaRepository.existsById(usuariosParcelaDto.get(0).getParcela())) {
                parcela = parcelaRepository.findById(usuariosParcelaDto.get(0).getParcela())
                    .orElseThrow(() -> new RuntimeException("Parcela no encontrada"));
            } else {
                parcelaConstruccion = parcelaConstruccionRepository.findById(usuariosParcelaDto.get(0).getParcelaConstruccion())
                    .orElseThrow(() -> new RuntimeException("Parcela no encontrada"));
            }

            for (UsuarioParcelaDto usuarioParcelaDto : usuariosParcelaDto) {
                if (usuarioParcelaDto.getParticipacion() <= 0 || usuarioParcelaDto.getParticipacion() > 100) {
                    return ResponseEntity.badRequest().body(new MessageResponse("La participacion tiene que estar entre 0 y 100"));
                }
                
                if (parcela != null && usuarioParcelaRepository.existsByUsuarioIdAndParcelaReferenciaCatastral(usuarioParcelaDto.getUsuario(), usuarioParcelaDto.getParcela())) {
                    UsuarioParcela usuarioParcela = usuarioParcelaRepository.findByUsuarioIdAndParcelaReferenciaCatastral(usuarioParcelaDto.getUsuario(), usuarioParcelaDto.getParcela());
                    if (usuarioParcela.getFechaBaja() == null) {
                        return ResponseEntity.badRequest().body(new MessageResponse("El usuario ya está registrado en la parcela"));
                    }
                    usuarioParcela.setParticipacion(usuarioParcelaDto.getParticipacion());
                    usuarioParcela.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                    usuarioParcela.setFechaBaja(null);
                    usuarioParcelaRepository.save(usuarioParcela);
                }
                if (parcelaConstruccion != null && usuarioParcelaRepository.existsByUsuarioIdAndParcelaConstruccionReferenciaCatastral(usuarioParcelaDto.getUsuario(), usuarioParcelaDto.getParcela())) {
                    UsuarioParcela usuarioParcela = usuarioParcelaRepository.findByUsuarioIdAndParcelaConstruccionReferenciaCatastral(usuarioParcelaDto.getUsuario(), usuarioParcelaDto.getParcela());
                    
                    if (usuarioParcela.getFechaBaja() == null) {
                        return ResponseEntity.badRequest().body(new MessageResponse("El usuario ya está registrado en la parcela"));
                    }

                    usuarioParcela.setParticipacion(usuarioParcelaDto.getParticipacion());
                    usuarioParcela.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                    usuarioParcela.setFechaBaja(null);
                    usuarioParcelaRepository.save(usuarioParcela);
                } else {
                    Usuario usuario = usuarioRepository.findById(usuarioParcelaDto.getUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                    UsuarioParcela usuarioParcela = new UsuarioParcela();
                    if (parcela != null) {
                        usuarioParcela = new UsuarioParcela (
                            usuario,
                            parcela,
                            usuarioParcelaDto.getParticipacion(),
                            new Timestamp(System.currentTimeMillis())
                        );
                    } else {
                        usuarioParcela = new UsuarioParcela (
                            usuario,
                            parcelaConstruccion,
                            usuarioParcelaDto.getParticipacion(),
                            new Timestamp(System.currentTimeMillis())
                        );
                        usuarioParcelaRepository.save(usuarioParcela);
                    }
                }        
            }
        }

        return ResponseEntity.ok(new MessageResponse("Los usuarios se han añadido correctamente"));
    }

    @Transactional
    public ResponseEntity<?> editarUsuarioParcela (UsuarioParcelaResponseDto usuarioParcelaDto) {

        if (usuarioParcelaDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ningún usuario a la finca"));
        } else {
            UsuarioParcela usuarioParcela = usuarioParcelaRepository.findById(usuarioParcelaDto.getId())
                .orElseThrow(() -> new RuntimeException("Usuario finca no encontrado"));
            
            if (usuarioParcelaDto.getParticipacion() != usuarioParcela.getParticipacion() && usuarioParcelaDto.getParticipacion() > 0  && usuarioParcelaDto.getParticipacion() <= 100) {
                usuarioParcela.setParticipacion(usuarioParcelaDto.getParticipacion());

                usuarioParcela.setFechaModificacion(new Timestamp(System.currentTimeMillis()));
                usuarioParcelaRepository.save(usuarioParcela);
                return ResponseEntity.ok(new MessageResponse("El usuario " + usuarioParcela.getUsuario().getUsername() + " de la parcela se ha modificado correctamente"));
            }

            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }   
    }

    @Transactional
    public ResponseEntity<?> eliminarUsuarioParcela (UsuarioParcelaResponseDto usuarioParcelaDto) {

        UsuarioParcela usuarioParcela = usuarioParcelaRepository.findById(usuarioParcelaDto.getId())
            .orElseThrow(() -> new RuntimeException("Usuario finca no encontrado"));
        
        usuarioParcela.setFechaBaja(new Timestamp(System.currentTimeMillis()));
        usuarioParcelaRepository.save(usuarioParcela);
        
        return ResponseEntity.ok(new MessageResponse("El usuario " + usuarioParcela.getUsuario().getUsername() + " de la parcela " + usuarioParcela.getParcela().getReferenciaCatastral() + " se ha eliminado correctamente"));
    }

    @Transactional
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
        }
        ParcelaResponseDto parcelaDto = new ParcelaResponseDto();
        return ResponseEntity.ok(parcelaDto);
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    public ResponseEntity<?> findUsuariosInParcela(String referenciaCatastral) {
        List<UsuarioParcelaResponseDto> usuariosParcelaDto = 
            usuarioParcelaRepository.findUsuarioParcelaByReferenciaCatastral(referenciaCatastral);

        if (usuariosParcelaDto.isEmpty()) {
            usuariosParcelaDto = usuarioParcelaRepository.findUsuarioParcelaConstruccionByReferenciaCatastral(referenciaCatastral);
        }

        return ResponseEntity.ok(usuariosParcelaDto);
    }

    @Transactional
    public ResponseEntity<?> findUsuariosBajaInParcela(String referenciaCatastral) {
        List<UsuarioParcelaResponseDto> usuariosParcelaDto = 
            usuarioParcelaRepository.findUsuarioParcelaBajaByReferenciaCatastral(referenciaCatastral);

        if (usuariosParcelaDto.isEmpty()) {
            usuariosParcelaDto = usuarioParcelaRepository.findUsuarioParcelaConstruccionBajaByReferenciaCatastral(referenciaCatastral);
        }

        return ResponseEntity.ok(usuariosParcelaDto);
    }

    @Transactional
    public ResponseEntity<?> findUsuarioParcelaById(UUID idUsuarioParcela) {
        UsuarioParcela usuarioParcela = usuarioParcelaRepository.findById(idUsuarioParcela)
            .orElseThrow(() -> new RuntimeException("Usuario parcela no encontrado"));
            
        String referenciaCatastral;
        if (usuarioParcela.getParcela() != null) {
            referenciaCatastral = usuarioParcela.getParcela().getReferenciaCatastral();
        } else {
            referenciaCatastral = usuarioParcela.getParcelaConstruccion().getReferenciaCatastral();
        }
        UsuarioParcelaResponseDto usuarioParcelaResponseDto = new UsuarioParcelaResponseDto(
            usuarioParcela.getId(),
            usuarioParcela.getUsuario().getNombre(),
            usuarioParcela.getUsuario().getApellido1(),
            usuarioParcela.getUsuario().getApellido2(),
            usuarioParcela.getParticipacion(),
            usuarioParcela.getUsuario().getId(),
            referenciaCatastral,
            usuarioParcela.getFechaAlta(),
            usuarioParcela.getFechaModificacion(),
            usuarioParcela.getFechaBaja()
        );

        return ResponseEntity.ok(usuarioParcelaResponseDto);
    }

    @Transactional
    public ResponseEntity<?> getParticipacionesDisponibles (String referenciaCatastral) {

        Long participacion = usuarioParcelaRepository.getParticipacionDisponibleParcela(referenciaCatastral);

        if (participacion == 100) {
            participacion = usuarioParcelaRepository.getParticipacionDisponibleParcelaConstruccion(referenciaCatastral);
        }
        return ResponseEntity.ok(participacion);
    }

    private boolean isValidReferenciaCatastral(String referenciaCatastral) {
         
        if (referenciaCatastral == null || referenciaCatastral.isEmpty()) {
            return false;
        }
        return referenciaCatastral.matches(REFERENCIA_CATASTRAL_FORMATO);
    }
}

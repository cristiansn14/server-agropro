package com.agroproserver.serveragropro.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.agroproserver.serveragropro.dto.request.LineaLiquidacionRequest;
import com.agroproserver.serveragropro.dto.request.LiquidacionRequest;
import com.agroproserver.serveragropro.dto.response.LineaLiquidacionResponse;
import com.agroproserver.serveragropro.dto.response.LiquidacionResponse;
import com.agroproserver.serveragropro.model.Archivo;
import com.agroproserver.serveragropro.model.Finca;
import com.agroproserver.serveragropro.model.LineaLiquidacion;
import com.agroproserver.serveragropro.model.Liquidacion;
import com.agroproserver.serveragropro.model.Movimiento;
import com.agroproserver.serveragropro.model.Usuario;
import com.agroproserver.serveragropro.model.UsuarioFinca;
import com.agroproserver.serveragropro.model.UsuarioParcela;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.ArchivoRepository;
import com.agroproserver.serveragropro.repository.FincaRepository;
import com.agroproserver.serveragropro.repository.LineaLiquidacionRepository;
import com.agroproserver.serveragropro.repository.LiquidacionRepository;
import com.agroproserver.serveragropro.repository.MovimientoRepository;
import com.agroproserver.serveragropro.repository.ParcelaRepository;
import com.agroproserver.serveragropro.repository.ParcelaConstruccionRepository;
import com.agroproserver.serveragropro.repository.UsuarioFincaRepository;
import com.agroproserver.serveragropro.repository.UsuarioParcelaRepository;


@Service
public class LiquidacionService {

    @Autowired
    MovimientoRepository movimientoRepository;

    @Autowired
    FincaRepository fincaRepository;

    @Autowired
    ParcelaRepository parcelaRepository;

    @Autowired
    ParcelaConstruccionRepository parcelaConstruccionRepository;

    @Autowired
    UsuarioFincaRepository usuarioFincaRepository;

    @Autowired
    UsuarioParcelaRepository usuarioParcelaRepository;

    @Autowired
    LiquidacionRepository liquidacionRepository;

    @Autowired
    LineaLiquidacionRepository lineaLiquidacionRepository;

    @Autowired
    ArchivoRepository archivoRepository;

    @Transactional
    public ResponseEntity<?> findById(UUID idLiquidacion) {

        Liquidacion liquidacion = liquidacionRepository.findById(idLiquidacion)
            .orElseThrow(() -> new RuntimeException("Liquidacion no encontrada"));

        LiquidacionResponse liquidacionResponse = new LiquidacionResponse(
            liquidacion.getId(),
            liquidacion.getConcepto(),
            liquidacion.getTipo(),
            liquidacion.getImporteTotal(),
            liquidacion.getFechaDesde(),
            liquidacion.getFechaHasta(),
            liquidacion.getFinca().getId(),
            liquidacion.getFecha(),
            null,
            null,
            null
        );

        if (liquidacion.getArchivo() != null) {
            liquidacionResponse.setIdArchivo(liquidacion.getArchivo().getId());
            liquidacionResponse.setNombreArchivo(liquidacion.getArchivo().getName());
            liquidacionResponse.setTipoArchivo(liquidacion.getArchivo().getType());
        }

        return ResponseEntity.ok(liquidacionResponse); 
    }

    @Transactional
    public ResponseEntity<?> findByFincaId(UUID idFinca) {

        List<Liquidacion> liquidaciones = liquidacionRepository.findByFincaId(idFinca);
        if (liquidaciones != null) {
            List<LiquidacionResponse> liquidacionesDto = new ArrayList<>();
            liquidaciones.forEach(liquidacion -> {
                LiquidacionResponse liquidacionDto = new LiquidacionResponse(
                    liquidacion.getId(),
                    liquidacion.getConcepto(),
                    liquidacion.getTipo(),
                    liquidacion.getImporteTotal(),
                    liquidacion.getFechaDesde(),
                    liquidacion.getFechaHasta(),
                    liquidacion.getFinca().getId(),
                    liquidacion.getFecha(),
                    null,
                    null,
                    null
                );

                if (liquidacion.getArchivo() != null) {
                    liquidacionDto.setIdArchivo(liquidacion.getArchivo().getId());
                    liquidacionDto.setNombreArchivo(liquidacion.getArchivo().getName());
                    liquidacionDto.setTipoArchivo(liquidacion.getArchivo().getType());
                }
                
                liquidacionesDto.add(liquidacionDto);
            });

            return ResponseEntity.ok(liquidacionesDto);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("La finca no tiene movimientos registrados"));
        }
    }

    @Transactional
    public ResponseEntity<?> generarLiquidacion(LiquidacionRequest liquidacionRequest, MultipartFile documento, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        } else {
            if (liquidacionRequest.getFechaDesde().after(liquidacionRequest.getFechaHasta())) {
                return ResponseEntity.badRequest().body(new MessageResponse("La fecha desde debe ser anterior a la fecha hasta"));
            }

            BigDecimal importeTotal = movimientoRepository.sumImportesBetweenDates(liquidacionRequest.getFechaDesde(), liquidacionRequest.getFechaHasta(), liquidacionRequest.getIdFinca());

            if (importeTotal.compareTo(BigDecimal.ZERO) <= 0){
                return ResponseEntity.badRequest().body(new MessageResponse("No existen movimientos en ese rango de fechas o el importe es 0"));
            }

            Finca finca = fincaRepository.findById(liquidacionRequest.getIdFinca())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

            Liquidacion liquidacion = Liquidacion.builder()
                                .concepto(liquidacionRequest.getConcepto())
                                .importeTotal(importeTotal)
                                .tipo(liquidacionRequest.getTipo())
                                .fechaDesde(liquidacionRequest.getFechaDesde())
                                .fechaHasta(liquidacionRequest.getFechaHasta())
                                .fecha(new Timestamp(System.currentTimeMillis()))
                                .finca(finca)
                                .build();

            if (documento != null && !documento.isEmpty()) {
                try {
                    Archivo archivo = Archivo.builder()
                        .name(documento.getOriginalFilename())
                        .type(documento.getContentType())
                        .data(documento.getBytes())
                        .finca(finca)
                        .build();
                    archivoRepository.save(archivo);
                    liquidacion.setArchivo(archivo);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error al procesar el documento", e);
                }
            }

            liquidacionRepository.save(liquidacion);

            switch (liquidacionRequest.getTipo()) {
                case "Equitativa":

                    List<UsuarioFinca> usuariosFinca = usuarioFincaRepository.findUsuariosFincaByFincaId(finca.getId());

                    if (!usuariosFinca.isEmpty()){
                        for (UsuarioFinca usuarioFinca : usuariosFinca) {
                            LineaLiquidacion lineaLiquidacion = LineaLiquidacion.builder()
                                .importe(importeTotal.divide(BigDecimal.valueOf(usuariosFinca.size()), 2, RoundingMode.HALF_UP))
                                .liquidacion(liquidacion)
                                .usuario(usuarioFinca.getUsuario())
                                .build();
                            lineaLiquidacion.setRecibida(false);
                            lineaLiquidacionRepository.save(lineaLiquidacion);
                        }
                        return ResponseEntity.ok(new MessageResponse("Se ha generado la liquidación correctamente"));
                    } else {
                        return ResponseEntity.badRequest().body(new MessageResponse("No hay usuarios registrados en la finca"));
                    }              
                case "Superficie de parcela":

                    List<UsuarioParcela> usuariosParcela = usuarioParcelaRepository.findUsuariosParcelaByFincaId(finca.getId());

                    if (!usuariosParcela.isEmpty()) {
                        BigDecimal superficieTotalParcelas = parcelaRepository.sumSuperficieByFincaId(finca.getId());
                        BigDecimal superficieTotalParcelasConstruccion = parcelaConstruccionRepository.sumSuperficieByFincaId(finca.getId());
                        BigDecimal superficieTotal = superficieTotalParcelas.add(superficieTotalParcelasConstruccion);

                        Map<Usuario, BigDecimal> importesPorUsuario = new HashMap<>();

                        for (UsuarioParcela usuarioParcela : usuariosParcela) {                           
                            BigDecimal superficie = usuarioParcela.getParcela() != null
                                    ? usuarioParcela.getParcela().getSuperficie()
                                    : usuarioParcela.getParcelaConstruccion().getSuperficie();
                        
                            BigDecimal multiplicador = superficie.divide(superficieTotal, 2, RoundingMode.HALF_UP);
                            BigDecimal importeParcela = importeTotal.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal importeIndividual = importeParcela.multiply(BigDecimal.valueOf(usuarioParcela.getParticipacion()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                            
                            importesPorUsuario.merge(usuarioParcela.getUsuario(), importeIndividual, BigDecimal::add);
                        }

                        for (Map.Entry<Usuario, BigDecimal> entry : importesPorUsuario.entrySet()) {
                            LineaLiquidacion lineaLiquidacion = LineaLiquidacion.builder()
                                .importe(entry.getValue())
                                .liquidacion(liquidacion)
                                .usuario(entry.getKey())
                                .build();
                            lineaLiquidacion.setRecibida(false);
    
                            lineaLiquidacionRepository.save(lineaLiquidacion);
                        }
    
                        return ResponseEntity.ok(new MessageResponse("Se ha generado la liquidación correctamente"));
                    } else {
                        return ResponseEntity.badRequest().body(new MessageResponse("No hay usuarios registrados en la parcela"));
                    }                           
                case "Onzas":
                    List<UsuarioFinca> usuariosFincaOnzas = usuarioFincaRepository.findUsuariosFincaByFincaId(finca.getId());

                    boolean hasZeroOnzas = usuariosFincaOnzas.stream().anyMatch(usuarioFinca -> usuarioFinca.getOnzas() == 0);

                    if (usuariosFincaOnzas.isEmpty()) {
                        return ResponseEntity.badRequest().body(new MessageResponse("No hay usuarios registrados en la finca"));
                    }
                    if (hasZeroOnzas) {
                        return ResponseEntity.badRequest().body(new MessageResponse("Configure las onzas en todos los usuarios de la finca"));
                    } else {
                        for (UsuarioFinca usuarioFinca : usuariosFincaOnzas) {
                            BigDecimal multiplicador = BigDecimal.valueOf(usuarioFinca.getOnzas()).divide(BigDecimal.valueOf(finca.getOnzas()), 2, RoundingMode.HALF_UP);
                            BigDecimal importeIndividual = importeTotal.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);

                            LineaLiquidacion lineaLiquidacion = LineaLiquidacion.builder()
                                    .importe(importeIndividual)
                                    .liquidacion(liquidacion)
                                    .usuario(usuarioFinca.getUsuario())
                                    .build();
                            lineaLiquidacion.setRecibida(false);
                            lineaLiquidacionRepository.save(lineaLiquidacion);
                        }                   
                    }
                    return ResponseEntity.ok(new MessageResponse("Se ha generado la liquidación correctamente"));
                default:
                    return ResponseEntity.badRequest().body(new MessageResponse("No existe el tipo de liquidación"));
            } 
        }  
    }

    @Transactional
    public ResponseEntity<?> eliminarLiquidacion(UUID idLiquidacion) {

        Liquidacion liquidacion = liquidacionRepository.findById(idLiquidacion)
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

        List<LineaLiquidacion> lineasLiquidacion = lineaLiquidacionRepository.findByLiquidacionId(idLiquidacion);

        lineaLiquidacionRepository.deleteAll(lineasLiquidacion);
        liquidacionRepository.delete(liquidacion);

        return ResponseEntity.ok(new MessageResponse("La liquidación se ha eliminado correctamente"));
    }

    @Transactional
    public ResponseEntity<?> findLineasLiquidacionByLiquidacionId(UUID idLiquidacion) {
        
        List<LineaLiquidacionResponse> lineasLiquidacionResponse = lineaLiquidacionRepository.findByLiquidacionId(idLiquidacion).stream()
            .map(lineaLiquidacion -> new LineaLiquidacionResponse(
                lineaLiquidacion.getId(),
                lineaLiquidacion.getImporte(),
                lineaLiquidacion.getUsuario().getNombre(),
                lineaLiquidacion.getUsuario().getApellido1(),
                lineaLiquidacion.getUsuario().getApellido2(),
                idLiquidacion,
                lineaLiquidacion.getUsuario().getId(),
                lineaLiquidacion.getRecibida()
            ))
        .collect(Collectors.toList());
        return ResponseEntity.ok(lineasLiquidacionResponse);
    }

    @Transactional
    public ResponseEntity<?> liquidacionRecibida(LineaLiquidacionRequest lineaLiquidacionDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        }

        LineaLiquidacion lineaLiquidacion = lineaLiquidacionRepository.findById(lineaLiquidacionDto.getId())
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

        Liquidacion liquidacion = liquidacionRepository.findById(lineaLiquidacionDto.getIdLiquidacion())
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

        Movimiento movimiento = Movimiento.builder()
                    .concepto(liquidacion.getConcepto() + " " + lineaLiquidacion.getUsuario().getNombre() + " " + lineaLiquidacion.getUsuario().getApellido1() + " " + lineaLiquidacion.getUsuario().getApellido2())
                    .importe(lineaLiquidacionDto.getImporte().negate())
                    .fecha(new Timestamp(System.currentTimeMillis()))
                    .finca(liquidacion.getFinca())
                    .build();

        movimientoRepository.save(movimiento);

        lineaLiquidacion.setRecibida(true);
        lineaLiquidacionRepository.save(lineaLiquidacion);

        return ResponseEntity.ok(new MessageResponse("Se ha generado el movimiento correctamente"));
    }
}

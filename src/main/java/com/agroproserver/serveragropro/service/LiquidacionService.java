package com.agroproserver.serveragropro.service;

import java.sql.Timestamp;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.agroproserver.serveragropro.dto.request.LiquidacionRequest;
import com.agroproserver.serveragropro.model.Finca;
import com.agroproserver.serveragropro.model.LineaLiquidacion;
import com.agroproserver.serveragropro.model.Liquidacion;
import com.agroproserver.serveragropro.model.UsuarioFinca;
import com.agroproserver.serveragropro.model.UsuarioParcela;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
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

    @Transactional
    public ResponseEntity<?> generarLiquidacion(LiquidacionRequest liquidacionRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        } else {
            BigDecimal importeTotal = movimientoRepository.sumImportesBetweenDates(liquidacionRequest.getFechaDesde(), liquidacionRequest.getFechaHasta());

            if (importeTotal.compareTo(BigDecimal.ZERO) <= 0){
                return ResponseEntity.badRequest().body(new MessageResponse("El saldo es insuficiente"));
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

            switch (liquidacionRequest.getTipo()) {
                case "Equitativa":
                    List<UsuarioFinca> usuariosFinca = usuarioFincaRepository.findUsuariosFincaByFincaId(finca.getId());
                    if (!usuariosFinca.isEmpty()){
                        liquidacionRepository.save(liquidacion);

                        for (UsuarioFinca usuarioFinca : usuariosFinca) {
                            LineaLiquidacion lineaLiquidacion = LineaLiquidacion.builder()
                                .concepto(liquidacion.getConcepto())
                                .tipo(liquidacionRequest.getTipo())
                                .importe(importeTotal.divide(BigDecimal.valueOf(usuariosFinca.size()), 2, RoundingMode.HALF_UP))
                                .liquidacion(liquidacion)
                                .usuario(usuarioFinca.getUsuario())
                                .fecha(new Timestamp(System.currentTimeMillis()))
                                .build();
                            lineaLiquidacionRepository.save(lineaLiquidacion);
                        }
                    } else {
                        return ResponseEntity.badRequest().body(new MessageResponse("No hay usuarios registrados en la finca"));
                    }              
                break;
                case "Superficie de parcela":
                    List<UsuarioParcela> usuariosParcela = usuarioParcelaRepository.findUsuariosParcelaByFincaId(finca.getId());

                    if (!usuariosParcela.isEmpty()) {
                        liquidacionRepository.save(liquidacion);

                        BigDecimal superficieTotalParcelas = parcelaRepository.sumSuperficieByFincaId(finca.getId());
                        BigDecimal superficieTotalParcelasConstruccion = parcelaConstruccionRepository.sumSuperficieByFincaId(finca.getId());
                        BigDecimal superficieTotal = superficieTotalParcelas.add(superficieTotalParcelasConstruccion);

                        for (UsuarioParcela usuarioParcela : usuariosParcela) {
                            BigDecimal superficie = usuarioParcela.getParcela() != null
                                    ? usuarioParcela.getParcela().getSuperficie()
                                    : usuarioParcela.getParcelaConstruccion().getSuperficie();
                        
                            BigDecimal multiplicador = superficie.divide(superficieTotal, 2, RoundingMode.HALF_UP);
                            BigDecimal importeParcela = importeTotal.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal importeIndividual = importeParcela.multiply(BigDecimal.valueOf(usuarioParcela.getParticipacion()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                        
                            LineaLiquidacion lineaLiquidacion = LineaLiquidacion.builder()
                                .concepto(liquidacion.getConcepto())
                                .tipo(liquidacionRequest.getTipo())
                                .importe(importeIndividual)
                                .liquidacion(liquidacion)
                                .usuario(usuarioParcela.getUsuario())
                                .fecha(new Timestamp(System.currentTimeMillis()))
                                .build();
                        
                            lineaLiquidacionRepository.save(lineaLiquidacion);
                        }                        
                    } else {
                        return ResponseEntity.badRequest().body(new MessageResponse("No hay usuarios registrados en la parcela"));
                    }                           
                break;
                case "Onzas":
                    List<UsuarioFinca> usuariosFincaOnzas = usuarioFincaRepository.findUsuariosFincaByFincaId(finca.getId());
                    boolean hasZeroOnzas = usuariosFincaOnzas.stream().anyMatch(usuarioFinca -> usuarioFinca.getOnzas() == 0);
                    if (usuariosFincaOnzas.isEmpty()) {
                        return ResponseEntity.badRequest().body(new MessageResponse("No hay usuarios registrados en la finca"));
                    }
                    if (hasZeroOnzas) {
                        return ResponseEntity.badRequest().body(new MessageResponse("Configure las onzas en todos los usuarios de la finca"));
                    } else {
                        liquidacionRepository.save(liquidacion);
                        
                        for (UsuarioFinca usuarioFinca : usuariosFincaOnzas) {
                            BigDecimal multiplicador = BigDecimal.valueOf(usuarioFinca.getOnzas()).divide(BigDecimal.valueOf(finca.getOnzas()));
                            BigDecimal importeIndividual = importeTotal.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);

                            LineaLiquidacion lineaLiquidacion = LineaLiquidacion.builder()
                                    .concepto(liquidacion.getConcepto())
                                    .tipo(liquidacionRequest.getTipo())
                                    .importe(importeIndividual)
                                    .liquidacion(liquidacion)
                                    .usuario(usuarioFinca.getUsuario())
                                    .fecha(new Timestamp(System.currentTimeMillis()))
                                    .build();
                            lineaLiquidacionRepository.save(lineaLiquidacion);
                        }                   
                    }
                break;
            }

            return ResponseEntity.ok(new MessageResponse("Se ha generado la liquidación correctamente"));
        }  
    }
}

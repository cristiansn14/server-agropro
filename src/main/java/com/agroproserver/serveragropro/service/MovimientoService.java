package com.agroproserver.serveragropro.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.agroproserver.serveragropro.dto.request.MovimientoRequestDto;
import com.agroproserver.serveragropro.dto.response.MovimientoResponseDto;
import com.agroproserver.serveragropro.model.Archivo;
import com.agroproserver.serveragropro.model.Movimiento;
import com.agroproserver.serveragropro.model.Finca;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.ArchivoRepository;
import com.agroproserver.serveragropro.repository.MovimientoRepository;
import com.agroproserver.serveragropro.repository.FincaRepository;

@Service
public class MovimientoService {

    @Autowired
    FincaRepository fincaRepository;

    @Autowired
    MovimientoRepository movimientoRepository;

    @Autowired
    ArchivoRepository archivoRepository;

    @Transactional
    public ResponseEntity<?> crearMovimiento(MovimientoRequestDto movimientoDto, MultipartFile documento, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Campos erróneos"));
        } else if (movimientoDto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error, no se ha añadido ninguna cuenta"));
        } else {
            Finca finca = fincaRepository.findById(movimientoDto.getIdFinca())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaActual = null;
            try {
                fechaActual = sdf.parse(sdf.format(new Date()));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body(new MessageResponse("Error al formatear la fecha"));
            }

            BigDecimal importeAjustado = movimientoDto.getImporte();
            if ("gasto".equalsIgnoreCase(movimientoDto.getTipo()) && importeAjustado.compareTo(BigDecimal.ZERO) >= 0) {
                importeAjustado = importeAjustado.negate();
            }

            Movimiento movimiento = new Movimiento(
                movimientoDto.getConcepto(),
                importeAjustado,
                fechaActual,
                finca,
                null 
            );

            if (documento != null && !documento.isEmpty()) {
                try {
                    Archivo archivo = Archivo.builder()
                        .name(documento.getOriginalFilename())
                        .type(documento.getContentType())
                        .data(documento.getBytes())
                        .finca(finca)
                        .build();
                    archivoRepository.save(archivo);
                    movimiento.setArchivo(archivo);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error al procesar el documento", e);
                }
            }

            movimientoRepository.save(movimiento);
            return ResponseEntity.ok(new MessageResponse("El movimiento ha sido añadido correctamente"));
        }
    }

    @Transactional
    public ResponseEntity<?> eliminarMovimiento(UUID idMovimiento) {

        Movimiento movimiento = movimientoRepository.findById(idMovimiento)
            .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        movimientoRepository.delete(movimiento);
        return ResponseEntity.ok(new MessageResponse("El movimiento ha sido añadido correctamente"));
    }

    @Transactional
    public ResponseEntity<?> findByFincaId(UUID idFinca) {

        List<Movimiento> movimientos = movimientoRepository.findByFincaId(idFinca);
        if (movimientos != null) {
            List<MovimientoResponseDto> movimientosDto = new ArrayList<>();
            movimientos.forEach(movimiento -> {
                MovimientoResponseDto movimientoDto = new MovimientoResponseDto(
                    movimiento.getId(),
                    movimiento.getConcepto(),
                    movimiento.getImporte(),
                    movimiento.getFecha(),
                    null,
                    null,
                    null
                );

                if (movimiento.getArchivo() != null) {
                    movimientoDto.setIdArchivo(movimiento.getArchivo().getId());
                    movimientoDto.setNombreArchivo(movimiento.getArchivo().getName());
                    movimientoDto.setTipoArchivo(movimiento.getArchivo().getType());
                }
                
                movimientosDto.add(movimientoDto);
            });

            return ResponseEntity.ok(movimientosDto);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("La finca no tiene movimientos registrados"));
        }
    }

    @Transactional
    public ResponseEntity<?> findArchivoById(UUID idArchivo) {

        Archivo archivo = archivoRepository.findById(idArchivo)
            .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
        if (archivo != null) {
            ByteArrayResource resource = new ByteArrayResource(archivo.getData());
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + archivo.getName() + "\"")
                .body(resource);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Archivo no encontrado"));
        }
    }
}

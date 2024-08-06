package com.agroproserver.serveragropro.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.agroproserver.serveragropro.dto.request.MovimientoRequestDto;
import com.agroproserver.serveragropro.model.Archivo;
import com.agroproserver.serveragropro.model.Movimiento;
import com.agroproserver.serveragropro.model.Finca;
import com.agroproserver.serveragropro.payload.response.MessageResponse;
import com.agroproserver.serveragropro.repository.ArchivoRepository;
import com.agroproserver.serveragropro.repository.MovimientoRepository;
import com.agroproserver.serveragropro.repository.FincaRepository;
import com.agroproserver.serveragropro.utils.ImageUtils;

@Service
public class MovimientoService {

    @Autowired
    FincaRepository fincaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    ArchivoRepository archivoRepository;

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

            long importeAjustado = movimientoDto.getImporte();
            if ("gasto".equalsIgnoreCase(movimientoDto.getTipo())) {
                importeAjustado = -importeAjustado;
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
                    byte[] data = documento.getBytes();
                    Archivo archivo = Archivo.builder()
                        .name(documento.getOriginalFilename())
                        .type(documento.getContentType())
                        .data(ImageUtils.compressImage(data))
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
            return ResponseEntity.ok(new MessageResponse("La cuenta ha sido añadida correctamente"));
        }
    }
}

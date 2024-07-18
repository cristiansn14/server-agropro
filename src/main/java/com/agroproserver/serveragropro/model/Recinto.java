package com.agroproserver.serveragropro.model;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recinto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recinto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "REFERENCIA_CATASTRAL")
    private Parcela parcela;

    @NotBlank
    @Column(name = "RECINTO")
    private String recinto;

    @NotNull
    @Column(name = "SUPERFICIE")
    private long superficie;

    @Column(name = "PENDIENTE")
    private String pendiente;

    @Column(name = "ALTITUD")
    private long altitud; 

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_CULTIVO")
    private Cultivo cultivo;

    @Column(name = "PORCENTAJE_SUBVENCION")
    private String porcentajeSubvencion;

    @Column(name = "SUPERFICIE_SUBVENCION")
    private long superficieSubvencion;

    @Column(name = "COEFICIENTE_REGADIO")
    private long coeficienteRegadio;

    @Column(name = "INCIDENCIAS")
    private String incidencias;

    @Column(name = "REGION")
    private String region;

    @NotNull
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_MODIFICACION")
    private Timestamp fechaModificacion;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

    public Recinto (Parcela parcela, String recinto, long superficie, String pendiente, long altitud, 
                    Cultivo cultivo, String porcentajeSubvencion, long superficieSubvencion, 
                    long coeficienteRegadio,  String incidencias, String region, Timestamp fechaAlta) {
        this.parcela = parcela;
        this.recinto = recinto;
        this.superficie = superficie;
        this.pendiente = pendiente;
        this.altitud = altitud;
        this.cultivo = cultivo;
        this.porcentajeSubvencion = porcentajeSubvencion;
        this.superficieSubvencion = superficieSubvencion;
        this.coeficienteRegadio = coeficienteRegadio;
        this.incidencias = incidencias;
        this.region = region;
        this.fechaAlta = fechaAlta;
    }
}

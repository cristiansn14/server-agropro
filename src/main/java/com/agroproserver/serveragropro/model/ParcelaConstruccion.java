package com.agroproserver.serveragropro.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parcela_construccion")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParcelaConstruccion {

    @Id
    @Column(name = "REFERENCIA_CATASTRAL")
    private String referenciaCatastral;

    @Size(max = 20)
    @Column(name = "CLASE")
    private String clase;

    @Size(max = 20)
    @Column(name = "USO_PRINCIPAL")
    private String usoPrincipal;

    @Column(name = "SUPERFICIE", precision = 10, scale = 2)
    private BigDecimal superficie;

    @Column(name = "ESCALERA")
    private long escalera;

    @Column(name = "PLANTA")
    private long planta;

    @Column(name = "PUERTA")
    private long puerta;

    @Column(name = "TIPO_REFORMA")
    private String tipoReforma;

    @Column(name = "FECHA_REFORMA")
    private Date fechaReforma;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_FINCA")
    private Finca finca;

    @NotNull
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_MODIFICACION")
    private Timestamp fechaModificacion;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

    public ParcelaConstruccion (String referenciaCatastral, String usoPrincipal, BigDecimal superficie, 
                    long escalera, long planta, long puerta, String tipoReforma, Date fechaReforma, Finca finca, Timestamp fechaAlta) {
        this.referenciaCatastral = referenciaCatastral;
        this.usoPrincipal = usoPrincipal;
        this.superficie = superficie;
        this.escalera = escalera;
        this.planta = planta;
        this.puerta = puerta;
        this.tipoReforma = tipoReforma;
        this.fechaReforma = fechaReforma;
        this.finca = finca;
        this.fechaAlta = fechaAlta;
    }

}

package com.agroproserver.serveragropro.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
@Table(name = "parcela")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Parcela {

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

    @Column(name = "VALOR_SUELO")
    private long valorSuelo;

    @Column(name = "VALOR_CONSTRUCCION")
    private long valorConstruccion;

    @Column(name = "VALOR_CATASTRAL")
    private long valorCatastral;

    @Column(name = "AÑO_VALOR")
    private String añoValor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_POLIGONOPARCELA")
    private PoligonoParcela poligonoParcela;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_FINCA")
    private Finca finca;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PARAJE")
    private Paraje paraje;

    @NotNull
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_MODIFICACION")
    private Timestamp fechaModificacion;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;  

    public Parcela (String referenciaCatastral, String clase, String usoPrincipal, BigDecimal superficie, 
                    long valorSuelo, long valorConstruccion, long valorCatastral, String añoValor, 
                    PoligonoParcela poligonoParcela, Finca finca, Paraje paraje, Timestamp fechaAlta) {
        this.referenciaCatastral = referenciaCatastral;
        this.clase = clase;
        this.usoPrincipal = usoPrincipal;
        this.superficie = superficie;
        this.valorSuelo = valorSuelo;
        this.valorConstruccion = valorConstruccion;
        this.valorCatastral = valorCatastral;
        this.añoValor = añoValor;
        this.poligonoParcela = poligonoParcela;
        this.finca = finca;
        this.paraje = paraje;
        this.fechaAlta = fechaAlta;
    }
}

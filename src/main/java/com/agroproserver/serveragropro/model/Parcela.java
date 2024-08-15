package com.agroproserver.serveragropro.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
    private BigDecimal valorSuelo;

    @Column(name = "VALOR_CONSTRUCCION")
    private BigDecimal valorConstruccion;

    @Column(name = "VALOR_CATASTRAL")
    private BigDecimal valorCatastral;

    @Column(name = "AÑO_VALOR")
    private String añoValor;

    @OneToOne(fetch = FetchType.EAGER)
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
                    BigDecimal valorSuelo, BigDecimal valorConstruccion, BigDecimal valorCatastral, String añoValor, 
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

    public Parcela (String referenciaCatastral, String clase, String usoPrincipal, BigDecimal superficie, 
                    PoligonoParcela poligonoParcela, Finca finca, Paraje paraje, Timestamp fechaAlta) {
        this.referenciaCatastral = referenciaCatastral;
        this.clase = clase;
        this.usoPrincipal = usoPrincipal;
        this.superficie = superficie;
        this.poligonoParcela = poligonoParcela;
        this.finca = finca;
        this.paraje = paraje;
        this.fechaAlta = fechaAlta;
    }
}

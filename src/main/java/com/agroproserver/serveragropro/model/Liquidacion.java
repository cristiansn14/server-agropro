package com.agroproserver.serveragropro.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "liquidacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Liquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Size(max = 100)
    @Column(name = "CONCEPTO")
    @NotNull
    private String concepto;

    @Column(name = "TIPO")
    @NotNull
    private String tipo;

    @NotNull
    @Column(name = "IMPORTE_TOTAL", precision = 10, scale = 2)
    private BigDecimal importeTotal;

    @NotNull
    @Column(name = "FECHA_DESDE")
    private Date fechaDesde;

    @NotNull
    @Column(name = "FECHA_HASTA")
    private Date fechaHasta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_FINCA")
    private Finca finca;

    @NotNull
    @Column(name = "FECHA")
    private Timestamp fecha;

}

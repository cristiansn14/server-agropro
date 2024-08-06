package com.agroproserver.serveragropro.model;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "movimiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Size(max = 100)
    @Column(name = "CONCEPTO")
    @NotNull
    private String concepto;

    @NotNull
    @Column(name = "IMPORTE")
    private long importe;

    @NotNull
    @Column(name = "FECHA")
    private Date fecha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_FINCA")
    private Finca finca;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ARCHIVO")
    private Archivo archivo;    

    public Movimiento (String concepto, long importe, Date fecha, Finca finca, Archivo archivo) {
        this.concepto = concepto;
        this.importe = importe;
        this.fecha = fecha;
        this.finca = finca;
        this.archivo = archivo;
    }
}

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "finca")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Finca {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "ONZAS")
    private long onzas;

    @Size(max = 20)
    @Column(name = "NOMBRE")
    @NotNull
    private String nombre;

    @NotNull
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_MODIFICACION")
    private Timestamp fechaModificacion;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_COMUNIDAD")
    private Comunidad comunidad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PROVINCIA")
    private Provincia provincia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_MUNICIPIO")
    private Municipio municipio;

    public Finca(String nombre, long onzas, Comunidad comunidad, Provincia provincia, Municipio municipio, Timestamp fechaAlta) {
        this.nombre = nombre;
        this.onzas = onzas;
        this.comunidad = comunidad;
        this.provincia = provincia;
        this.municipio = municipio;
        this.fechaAlta = fechaAlta;
    }
}

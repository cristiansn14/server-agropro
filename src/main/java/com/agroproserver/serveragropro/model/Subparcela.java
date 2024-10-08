package com.agroproserver.serveragropro.model;

import java.math.BigDecimal;
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
@Table(name = "subparcela")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subparcela {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "REFERENCIA_CATASTRAL")
    private Parcela parcela;

    @NotBlank
    @Column(name = "SUBPARCELA")
    private String subparcela;

    @Column(name = "INTENSIDAD")
    private String intensidad;

    @Column(name = "SUPERFICIE")
    private BigDecimal superficie;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_CULTIVO")
    private Cultivo cultivo;

    public Subparcela (Parcela parcela, String subparcela, String intensidad, BigDecimal superficie, Cultivo cultivo) {
        this.parcela = parcela;
        this.subparcela = subparcela;
        this.intensidad = intensidad;
        this.superficie = superficie;
        this.cultivo = cultivo;
    }
}

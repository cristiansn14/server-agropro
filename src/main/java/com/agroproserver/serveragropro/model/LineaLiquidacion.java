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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "linea_liquidacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LineaLiquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @NotNull
    @Column(name = "IMPORTE", precision = 10, scale = 2)
    private BigDecimal importe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_LIQUIDACION")
    private Liquidacion liquidacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @NotNull
    @Column(name = "RECIBIDA")
    private boolean recibida;

    public boolean getRecibida() {
        return this.recibida;
    }
    
    public void setRecibida(boolean recibida) {
        this.recibida = recibida;
    } 
}

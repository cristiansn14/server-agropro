package com.agroproserver.serveragropro.model;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "usuario_parcela")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UsuarioParcela {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "PARTICIPACION")
    private long participacion;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "REFERENCIA_CATASTRAL_PARCELA")
    private Parcela parcela;

    @ManyToOne
    @JoinColumn(name = "REFERENCIA_CATASTRAL_PARCELA_CONSTRUCCION")
    private ParcelaConstruccion parcelaConstruccion;

    @NotNull
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_MODIFICACION")
    private Timestamp fechaModificacion;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

    public UsuarioParcela (Usuario usuario, Parcela parcela, long participacion, Timestamp fechaAlta) {
        this.usuario = usuario;
        this.parcela = parcela;
        this.participacion = participacion;
        this.fechaAlta = fechaAlta;
    }

    public UsuarioParcela (Usuario usuario, ParcelaConstruccion parcelaConstruccion, long participacion, Timestamp fechaAlta) {
        this.usuario = usuario;
        this.parcelaConstruccion = parcelaConstruccion;
        this.participacion = participacion;
        this.fechaAlta = fechaAlta;
    }
}

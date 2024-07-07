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
@Table(name = "usuario_finca")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UsuarioFinca {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "ONZAS")
    private long onzas;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ID_FINCA")
    private Finca finca;

    @ManyToOne
    @JoinColumn(name = "ID_ROL")
    private Rol rol;

    @NotNull
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

    public UsuarioFinca (Usuario usuario, Finca finca, Rol rol, long onzas, Timestamp fechaAlta) {
        this.usuario = usuario;
        this.finca = finca;
        this.rol = rol;
        this.onzas = onzas;
        this.fechaAlta = fechaAlta;
    }
}

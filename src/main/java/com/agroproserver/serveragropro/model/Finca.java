package com.agroproserver.serveragropro.model;
import java.security.Timestamp;
import java.util.HashSet;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "ONZAS")
    private long onzas;

    @Size(max = 20)
    @Column(name = "NOMBRE")
    @NotNull
    private String nombre;

    @NotBlank
    @CreationTimestamp
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_COMUNIDAD")
    private Comunidad comunidad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PROVINCIA")
    private Comunidad provincia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_MUNICIPIO")
    private Comunidad municipio;

    @OneToMany(mappedBy = "finca", cascade = CascadeType.ALL, orphanRemoval = true)
    private HashSet<UsuarioFinca> usuarioFincas;
}

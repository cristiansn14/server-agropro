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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "representante")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Representante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Size(max = 20)
    @Column(name = "NOMBRE")
    private String nombre;

    @Size(max = 20)
    @Column(name = "APELLIDO1")
    private String apellido1;

    @Size(max = 20)
    @Column(name = "APELLIDO2")
    private String apellido2;

    @Size(max = 9)
    @Column(name = "DNI")
    private String dni;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "EMAIL")
    private String email;

    @Size(max = 9)
    @Column(name = "TELEFONO")
    private String telefono;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @NotNull
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

}

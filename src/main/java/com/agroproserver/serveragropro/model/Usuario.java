package com.agroproserver.serveragropro.model;

import java.security.Timestamp;
import java.util.HashSet;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "usuario", uniqueConstraints = { @UniqueConstraint(columnNames = "USUARIO"), @UniqueConstraint(columnNames = "CORREO") })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @NotNull
    private String email;

    @Size(max = 9)
    @Column(name = "TELEFONO")
    private String telefono;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "CODIGO_POSTAL")
    private String codigoPostal;

    @Column(name = "CUENTA")
    private String cuenta;

    @NotBlank
    @Size(max = 20)
    @Column(name = "USUARIO", unique = true)
    @NotNull
    private String username;

    @NotBlank
    @Size(max = 120)
    @Column(name = "PASSWORD")
    @NotNull
    private String password;   

    @ColumnDefault("false")
    private boolean superUsuario;

    @NotBlank
    @CreationTimestamp
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private HashSet<UsuarioFinca> usuarioFincas;

    public Usuario (String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
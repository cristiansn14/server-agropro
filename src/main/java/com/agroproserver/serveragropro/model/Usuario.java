package com.agroproserver.serveragropro.model;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "usuario", uniqueConstraints = { @UniqueConstraint(columnNames = "USUARIO"), @UniqueConstraint(columnNames = "EMAIL") })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Usuario {

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
    @JoinColumn(name = "ID_COMUNIDAD")
    private Comunidad comunidad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PROVINCIA")
    private Provincia provincia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_MUNICIPIO")
    private Municipio municipio;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "CODIGO_POSTAL")
    private String codigoPostal;

    @Column(name = "CUENTA")
    private String cuenta;

    @NotBlank
    @Size(max = 20)
    @Column(name = "USUARIO")
    private String username;

    @NotBlank
    @Size(max = 120)
    @Column(name = "PASSWORD")
    @NotNull
    private String password;   

    @NotNull
    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioFinca> usuarioFincas;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(  name = "usuario_roles", joinColumns = @JoinColumn(name = "ID_USUARIO"), inverseJoinColumns = @JoinColumn(name = "ID_ROL"))
    private Set<Rol> roles;

    public Usuario (String username, String password, String email, String nombre, String apellido1, String apellido2) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
    }
}
package com.agroproserver.serveragropro.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "municipio")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Size(max = 70)
    @Column(name = "NOMBRE")
    @NotNull
    private String nombre;  

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_COMUNIDAD")
    private Comunidad comunidad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PROVINCIA")
    private Provincia provincia;  

    @NotNull
    @Column(name = "ID_MUNICIPIO")
    private long idMunicipio;

    @JsonIgnore
    @OneToMany(mappedBy = "municipio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Finca> fincas;
}

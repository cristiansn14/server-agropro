package com.agroproserver.serveragropro.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cultivo")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Size(max = 2)
    @Column(name = "CODIGO")
    @NotNull
    private String codigo;  

    @Size(max = 80)
    @Column(name = "DESCRIPCION")
    @NotNull
    private String descripcion;  

}

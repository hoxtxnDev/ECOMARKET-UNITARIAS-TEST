package com.ecomarket.gestiontiendaservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sucursal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Column
    private String nombre;

    @NotNull
    @NotBlank
    @Column
    private String direccion;

    @Column
    private String telefono;

    @Column
    private Long gerenteCargoId;

    @NotNull
    @Column
    private Boolean activa;

    @Column
    private LocalDateTime fechaInauguracion;
}
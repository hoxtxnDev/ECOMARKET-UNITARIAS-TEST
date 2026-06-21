package com.horacio.ecomarket.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "direcciones_usuario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Direccion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long usuarioId;

    @NotNull
    private String calle;
    private String numero;
    private String departamento;
    
    @NotNull
    private String ciudad;
    
    @NotNull
    private String region;
    
    private String codigoPostal;
    
    @NotNull
    private String destinatario;
    
    @Builder.Default
    private Boolean esPredeterminada = false;
}

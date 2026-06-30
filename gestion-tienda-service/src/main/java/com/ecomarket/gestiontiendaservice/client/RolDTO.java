package com.ecomarket.gestiontiendaservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {

    private Long id;
    private String nombre;
    private String descripcion;
}

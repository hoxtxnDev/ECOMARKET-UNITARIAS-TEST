package com.horacio.ecomarket.usuarios.repository;

import com.horacio.ecomarket.usuarios.model.SesionJWT;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionJWTRepository extends JpaRepository<SesionJWT, Long> {

    boolean existsByToken(String token);

    void deleteByUsuarioId(Long usuarioId);
}

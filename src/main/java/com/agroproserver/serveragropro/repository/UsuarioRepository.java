package com.agroproserver.serveragropro.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroproserver.serveragropro.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID>{
    
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
    
    boolean existByEmail(String email);

}

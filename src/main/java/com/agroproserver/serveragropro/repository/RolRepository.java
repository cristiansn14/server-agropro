package com.agroproserver.serveragropro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroproserver.serveragropro.enums.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long>{

    Optional<Rol> findByRol (Rol rol);
    
}

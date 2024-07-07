package com.agroproserver.serveragropro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroproserver.serveragropro.model.ERol;
import com.agroproserver.serveragropro.model.Rol;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface RolRepository extends JpaRepository<Rol, Long>{

    Optional<Rol> findByRol (ERol rol);
    
}

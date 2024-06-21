package com.agroproserver.serveragropro.service;

import java.util.Optional;

import org.jvnet.hk2.annotations.Service;

import com.agroproserver.serveragropro.enums.Rol;
import com.agroproserver.serveragropro.repository.RolRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RolService {
    RolRepository rolRepository;

    public Optional<Rol> getByRol(Rol rol){
        return rolRepository.findByRol(rol);
    }
}

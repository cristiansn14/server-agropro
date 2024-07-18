package com.agroproserver.serveragropro.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Subparcela;

@Repository
@Transactional
public interface SubparcelaRepository extends JpaRepository<Subparcela, UUID>{

}

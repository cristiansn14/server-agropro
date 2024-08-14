package com.agroproserver.serveragropro.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.agroproserver.serveragropro.model.Finca;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
@Transactional
public interface FincaRepository extends JpaRepository<Finca, UUID> {
    
    boolean existsByNombre(String nombre); 

    Optional<Finca> findByNombre(String nombre);

    @Query("SELECT f FROM Finca f WHERE EXISTS (" +
           "SELECT uf FROM UsuarioFinca uf WHERE uf.finca = f AND uf.usuario.id = :usuarioId AND uf.fechaBaja IS NULL) ")
    List<Finca> findByUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT f.onzas - COALESCE(SUM(uf.onzas), 0) FROM Finca f " +
           "LEFT JOIN UsuarioFinca uf ON f.id = uf.finca.id AND uf.fechaBaja IS NULL " +
           "WHERE f.id = :fincaId " +
           "GROUP BY f.onzas")
    Long getOnzasDisponibles(@Param("fincaId") UUID fincaId);

}

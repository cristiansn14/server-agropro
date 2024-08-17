package com.agroproserver.serveragropro.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agroproserver.serveragropro.model.Rol;
import com.agroproserver.serveragropro.model.Usuario;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, UUID>{
    
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);

    HashSet<Usuario> findAllByRoles(Rol rol);

    @Query("SELECT u FROM Usuario u WHERE u.fechaBaja IS NULL AND NOT EXISTS (" +
       "SELECT uf FROM UsuarioFinca uf WHERE uf.usuario.id = u.id AND uf.finca.id = :fincaId AND uf.fechaBaja IS NULL)")
    List<Usuario> findUsuariosNotInFinca(@Param("fincaId") UUID fincaId);

    @Query("SELECT u FROM Usuario u JOIN u.usuarioFincas uf WHERE uf.finca.id = :fincaId " +
       "AND uf.fechaBaja IS NULL " +  
       "AND NOT EXISTS (" +
       "SELECT uf2 FROM UsuarioFinca uf2 WHERE uf2.usuario.id = u.id " +
       "AND uf2.rol.rol = 'SUPERUSUARIO' " +
       "AND uf2.fechaBaja IS NULL)")
    List<Usuario> findUsuariosInFinca(@Param("fincaId") UUID fincaId);

    @Query("SELECT uf.usuario FROM UsuarioFinca uf " +
       "WHERE uf.finca.id = :idFinca " +
       "AND uf.fechaBaja IS NULL " +  
       "AND uf.usuario.id NOT IN (" +
       "    SELECT up.usuario.id FROM UsuarioParcela up " +
       "    WHERE up.parcela.referenciaCatastral = :referenciaCatastral " +
       "    AND up.fechaBaja IS NULL" +
       ") " +
       "AND uf.usuario.id NOT IN (" +
       "    SELECT up.usuario.id FROM UsuarioParcela up " +
       "    WHERE up.parcelaConstruccion.referenciaCatastral = :referenciaCatastral " +
       "    AND up.fechaBaja IS NULL" +
       ") " +
       "AND NOT EXISTS (" +
       "    SELECT uf2 FROM UsuarioFinca uf2 " +
       "    WHERE uf2.usuario.id = uf.usuario.id " +
       "    AND uf2.rol.rol = 'SUPERUSUARIO'" +
       ")")
    List<Usuario> findUsuariosByFincaAndNotInParcela(@Param("idFinca") UUID idFinca, @Param("referenciaCatastral") String referenciaCatastral);

    @Query("SELECT u FROM Usuario u WHERE u.fechaBaja IS NULL")
    List<Usuario> findAllUsuariosAlta();

    @Query("SELECT u FROM Usuario u WHERE u.fechaBaja IS NOT NULL")
    List<Usuario> findAllUsuariosBaja();
}

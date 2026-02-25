package com.virtualhub.repository;

import com.virtualhub.model.Amigo;
import com.virtualhub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AmigoRepository extends JpaRepository<Amigo, Long> {
    
    @Query("SELECT a.amigo FROM Amigo a WHERE a.usuario = :usuario AND a.estado = 'ACEPTADO'")
    List<Usuario> findAmigosAceptados(@Param("usuario") Usuario usuario);
    
    @Query("SELECT a FROM Amigo a WHERE a.usuario = :usuario AND a.estado = 'PENDIENTE'")
    List<Amigo> findSolicitudesEnviadas(@Param("usuario") Usuario usuario);
    
    @Query("SELECT a FROM Amigo a WHERE a.amigo = :usuario AND a.estado = 'PENDIENTE'")
    List<Amigo> findSolicitudesRecibidas(@Param("usuario") Usuario usuario);
    
    @Query("SELECT COUNT(a) FROM Amigo a WHERE a.amigo = :usuario AND a.estado = 'PENDIENTE'")
    long countSolicitudesPendientes(@Param("usuario") Usuario usuario);
    
    Optional<Amigo> findByUsuarioAndAmigo(Usuario usuario, Usuario amigo);
}
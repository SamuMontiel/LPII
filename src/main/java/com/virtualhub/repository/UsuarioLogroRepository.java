package com.virtualhub.repository;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.Logro;
import com.virtualhub.model.UsuarioLogro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioLogroRepository extends JpaRepository<UsuarioLogro, Long> {
    
    Optional<UsuarioLogro> findByUsuarioAndLogro(Usuario usuario, Logro logro);
    
    List<UsuarioLogro> findByUsuarioOrderByProgresoDesc(Usuario usuario);
    
    @Query("SELECT ul FROM UsuarioLogro ul WHERE ul.usuario = :usuario AND ul.completado = true")
    List<UsuarioLogro> findCompletadosByUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT COUNT(ul) FROM UsuarioLogro ul WHERE ul.usuario = :usuario AND ul.completado = true")
    long countCompletadosByUsuario(@Param("usuario") Usuario usuario);
}
package com.virtualhub.repository;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.Juego;
import com.virtualhub.model.UsuarioJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioJuegoRepository extends JpaRepository<UsuarioJuego, Long> {

    Optional<UsuarioJuego> findByUsuarioAndJuego(Usuario usuario, Juego juego);
    long countByUsuario(Usuario usuario);
    boolean existsByUsuarioAndJuego(Usuario usuario, Juego juego);

    List<UsuarioJuego> findByUsuario(Usuario usuario);  
    List<UsuarioJuego> findByUsuarioAndJugandoTrue(Usuario usuario);

    @Query("SELECT SUM(uj.horasJugadas) FROM UsuarioJuego uj WHERE uj.usuario = :usuario")
    Double sumHorasByUsuario(@Param("usuario") Usuario usuario);
    @Query("SELECT COUNT(uj) FROM UsuarioJuego uj WHERE uj.usuario = :usuario AND uj.horasJugadas > 20")
    long countCompletadosByUsuario(@Param("usuario") Usuario usuario);
    @Query("SELECT uj FROM UsuarioJuego uj WHERE uj.usuario.id = :usuarioId AND uj.juego.id = :juegoId")
    Optional<UsuarioJuego> findByUsuarioIdAndJuegoId(@Param("usuarioId") Long usuarioId, @Param("juegoId") Long juegoId);
    
    @Query("SELECT COUNT(uj) FROM UsuarioJuego uj WHERE uj.usuario.id = :usuarioId")
    long countByUsuarioId(@Param("usuarioId") Long usuarioId);
}
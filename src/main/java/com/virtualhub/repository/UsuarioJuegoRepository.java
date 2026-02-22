package com.virtualhub.repository;

import com.virtualhub.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UsuarioJuegoRepository
        extends JpaRepository<UsuarioJuego, Long> {

    Optional<UsuarioJuego> findByUsuarioAndJuego(Usuario usuario, Juego juego);

    List<UsuarioJuego> findByUsuario(Usuario usuario);
    
    
}
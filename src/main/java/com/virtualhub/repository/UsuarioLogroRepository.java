package com.virtualhub.repository;

import com.virtualhub.model.UsuarioLogro;
import com.virtualhub.model.Usuario;
import com.virtualhub.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioLogroRepository
        extends JpaRepository<UsuarioLogro, Long> {

    int countByUsuarioAndLogroJuego(Usuario usuario, Juego juego);
}
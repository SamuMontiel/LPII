package com.virtualhub.repository;

import com.virtualhub.model.Logro;
import com.virtualhub.model.Usuario;
import com.virtualhub.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogroRepository extends JpaRepository<Logro, Long> {

    List<Logro> findByJuego(Juego juego);

    int countByJuego(Juego juego);

    boolean existsByUsuarioAndJuegoAndNombre(
            Usuario usuario,
            Juego juego,
            String nombre
    );
}
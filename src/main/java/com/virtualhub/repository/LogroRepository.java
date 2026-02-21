package com.virtualhub.repository;

import com.virtualhub.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogroRepository extends JpaRepository<Logro, Long> {

    Optional<Logro> findByNombreAndJuego(String nombre, Juego juego);
}
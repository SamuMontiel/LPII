package com.virtualhub.repository;

import com.virtualhub.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByJuego(Juego juego);
}
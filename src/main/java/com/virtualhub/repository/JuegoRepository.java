package com.virtualhub.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.virtualhub.model.Juego;

public interface JuegoRepository extends JpaRepository<Juego, Long> {
	List<Juego> findByTituloContainingIgnoreCase(String titulo);
}

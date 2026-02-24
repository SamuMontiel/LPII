package com.virtualhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.virtualhub.model.Compra;
import com.virtualhub.model.Usuario;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

	@Query("SELECT COUNT(c) FROM Compra c WHERE c.usuario = :usuario")
	long countByUsuario(@Param("usuario") Usuario usuario);

    List<Compra> findByUsuario(Usuario usuario);
}

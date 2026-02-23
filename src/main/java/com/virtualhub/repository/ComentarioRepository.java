package com.virtualhub.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virtualhub.model.Comentario;
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
	List<Comentario> findByJuegoId(Long juegoId);
}
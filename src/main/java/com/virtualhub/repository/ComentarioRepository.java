package com.virtualhub.repository;

import java.util.List;
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
	List<Comentario> findByJuegoId(Long juegoId);
}
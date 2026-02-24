package com.virtualhub.repository;

import com.virtualhub.model.Captura;
import com.virtualhub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CapturaRepository extends JpaRepository<Captura, Long> {
    List<Captura> findByUsuarioOrderByFechaSubidaDesc(Usuario usuario);
    
    @Query("SELECT c FROM Captura c ORDER BY c.fechaSubida DESC")
    List<Captura> findRecientes();
}
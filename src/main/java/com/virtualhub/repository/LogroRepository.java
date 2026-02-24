package com.virtualhub.repository;

import com.virtualhub.model.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LogroRepository extends JpaRepository<Logro, Long> {
    // ✅ Cambia de Logro a Optional<Logro>
    Optional<Logro> findByNombre(String nombre);
}
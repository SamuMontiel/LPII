package com.virtualhub.repository;

import com.virtualhub.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioLogroRepository extends JpaRepository<UsuarioLogro, Long> {

    Optional<UsuarioLogro> findByUsuarioAndLogro(Usuario usuario, Logro logro);
}
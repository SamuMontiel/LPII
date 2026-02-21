package com.virtualhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.virtualhub.model.Compra;
import com.virtualhub.model.Usuario;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByUsuario(Usuario usuario);
}

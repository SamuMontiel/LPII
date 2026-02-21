package com.virtualhub.repository;

import com.virtualhub.model.Wishlist;
import com.virtualhub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WishlistRepository
        extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUsuario(Usuario usuario);

    boolean existsByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);
}
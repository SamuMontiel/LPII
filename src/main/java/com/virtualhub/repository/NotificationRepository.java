package com.virtualhub.repository;

import com.virtualhub.model.Notificacion;
import com.virtualhub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    List<Notificacion> findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(Usuario usuario);
    List<Notificacion> findByUsuarioAndLeidaFalse(Usuario usuario);
}
package com.virtualhub.service;

import com.virtualhub.model.Notificacion;
import com.virtualhub.model.Usuario;
import com.virtualhub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    public void crearNotificacion(Usuario usuario, String mensaje, String tipo) {
        Notificacion notificacion = Notificacion.builder()
            .usuario(usuario)
            .mensaje(mensaje)
            .tipo(tipo)
            .leida(false)
            .fechaCreacion(LocalDateTime.now())
            .build();
        
        notificationRepository.save(notificacion);
    }
    
    public List<Notificacion> getNotificacionesNoLeidas(Usuario usuario) {
        return notificationRepository.findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(usuario);
    }
    
    @Transactional
    public void marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificationRepository.findById(notificacionId)
            .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notificacion.setLeida(true);
        notificationRepository.save(notificacion);
    }
    
    @Transactional
    public void marcarTodasComoLeidas(Usuario usuario) {
        List<Notificacion> noLeidas = notificationRepository.findByUsuarioAndLeidaFalse(usuario);
        noLeidas.forEach(n -> n.setLeida(true));
        notificationRepository.saveAll(noLeidas);
    }
}
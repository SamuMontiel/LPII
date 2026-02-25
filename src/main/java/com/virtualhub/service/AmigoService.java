package com.virtualhub.service;

import com.virtualhub.model.Amigo;
import com.virtualhub.model.Usuario;
import com.virtualhub.repository.AmigoRepository;
import com.virtualhub.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmigoService {
    
    private final AmigoRepository amigoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificationService notificationService; // ✅ AGREGAR
    
    @Transactional
    public void enviarSolicitud(Long usuarioId, String emailAmigo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Usuario amigo = usuarioRepository.findByEmail(emailAmigo)
            .orElseThrow(() -> new RuntimeException("No existe un usuario con ese email"));
        
        if (usuario.getId().equals(amigo.getId())) {
            throw new RuntimeException("No puedes agregarte a ti mismo");
        }
        
        // Verificar si ya son amigos
        if (amigoRepository.findAmigosAceptados(usuario).contains(amigo)) {
            throw new RuntimeException("Ya son amigos");
        }
        
        // Verificar si ya hay solicitud pendiente
        amigoRepository.findByUsuarioAndAmigo(usuario, amigo)
            .ifPresent(a -> { throw new RuntimeException("Ya enviaste solicitud a este usuario"); });
        
        Amigo solicitud = Amigo.builder()
            .usuario(usuario)
            .amigo(amigo)
            .estado("PENDIENTE")
            .fechaSolicitud(LocalDateTime.now())
            .build();
        
        amigoRepository.save(solicitud);
        
        // ✅ CREAR NOTIFICACIÓN PARA EL DESTINATARIO
        notificationService.crearNotificacion(
            amigo,
            usuario.getNombre() + " te ha enviado una solicitud de amistad",
            "AMIGO"
        );
    }
    
    @Transactional
    public void aceptarSolicitud(Long solicitudId) {
        Amigo solicitud = amigoRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        
        if ("ACEPTADO".equals(solicitud.getEstado())) {
            throw new RuntimeException("Esta solicitud ya fue aceptada");
        }
        
        solicitud.setEstado("ACEPTADO");
        solicitud.setFechaAceptacion(LocalDateTime.now());
        amigoRepository.save(solicitud);
        
        
        Amigo relacionInversa = amigoRepository
            .findByUsuarioAndAmigo(solicitud.getAmigo(), solicitud.getUsuario())
            .orElse(null);
        
        if (relacionInversa == null) {
            relacionInversa = Amigo.builder()
                .usuario(solicitud.getAmigo())
                .amigo(solicitud.getUsuario())
                .estado("ACEPTADO")
                .fechaSolicitud(solicitud.getFechaSolicitud())
                .fechaAceptacion(LocalDateTime.now())
                .build();
            amigoRepository.save(relacionInversa);
        }
    }
    
    @Transactional
    public void rechazarSolicitud(Long solicitudId) {
        Amigo solicitud = amigoRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        // ✅ NOTIFICAR AL SOLICITANTE QUE SU SOLICITUD FUE RECHAZADA
        notificationService.crearNotificacion(
            solicitud.getUsuario(),
            solicitud.getAmigo().getNombre() + " rechazó tu solicitud de amistad",
            "AMIGO"
        );
        
        amigoRepository.deleteById(solicitudId);
    }
    
    public List<Amigo> obtenerSolicitudesRecibidas(Usuario usuario) {
        return amigoRepository.findSolicitudesRecibidas(usuario);
    }
    
    public List<Usuario> obtenerAmigos(Usuario usuario) {
        return amigoRepository.findAmigosAceptados(usuario);
    }
    
    public long contarSolicitudesPendientes(Usuario usuario) {
        return amigoRepository.countSolicitudesPendientes(usuario);
    }
}
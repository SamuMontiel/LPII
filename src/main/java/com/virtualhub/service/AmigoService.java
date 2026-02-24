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
    
    @Transactional
    public void enviarSolicitud(Long usuarioId, String emailAmigo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Usuario amigo = usuarioRepository.findByEmail(emailAmigo)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (usuario.getId().equals(amigo.getId())) {
            throw new RuntimeException("No puedes agregarte a ti mismo");
        }
        
        // Verificar si ya existe relación
        amigoRepository.findByUsuarioAndAmigo(usuario, amigo)
            .ifPresent(a -> { throw new RuntimeException("Ya existe una solicitud"); });
        
        Amigo solicitud = Amigo.builder()
            .usuario(usuario)
            .amigo(amigo)
            .estado("PENDIENTE")
            .fechaSolicitud(LocalDateTime.now())
            .build();
        
        amigoRepository.save(solicitud);
    }
    
    @Transactional
    public void aceptarSolicitud(Long solicitudId) {
        Amigo solicitud = amigoRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setEstado("ACEPTADO");
        solicitud.setFechaAceptacion(LocalDateTime.now());
        amigoRepository.save(solicitud);
    }
    
    @Transactional
    public void rechazarSolicitud(Long solicitudId) {
        amigoRepository.deleteById(solicitudId);
    }
    
    public List<Usuario> obtenerAmigos(Usuario usuario) {
        return amigoRepository.findAmigosAceptados(usuario);
    }
    
    public long contarSolicitudesPendientes(Usuario usuario) {
        return amigoRepository.countSolicitudesPendientes(usuario);
    }
}
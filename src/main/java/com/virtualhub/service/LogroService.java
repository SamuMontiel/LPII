package com.virtualhub.service;

import com.virtualhub.model.*;
import com.virtualhub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogroService {
    
    private final LogroRepository logroRepository;
    private final UsuarioLogroRepository usuarioLogroRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioJuegoRepository usuarioJuegoRepository;
    private final CompraRepository compraRepository;
    private final AmigoRepository amigoRepository;
    private final NotificationService notificationService;
    
    @Transactional
    public void verificarYActualizarLogros(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<UsuarioJuego> biblioteca = usuarioJuegoRepository.findByUsuario(usuario);
        
        // Calcular estadísticas actuales
        double totalHoras = biblioteca.stream()
            .mapToDouble(uj -> uj.getHorasJugadas() != null ? uj.getHorasJugadas() : 0)
            .sum();
            
        long juegosDiferentes = biblioteca.size();
        
        long juegosCompletados = biblioteca.stream()
            .filter(uj -> uj.getHorasJugadas() != null && uj.getHorasJugadas() > 10)
            .count();
            
        long compras = compraRepository.countByUsuario(usuario);
        
        long cantidadAmigos = amigoRepository.findAmigosAceptados(usuario).size();
        
        long logrosCompletados = usuarioLogroRepository.countCompletadosByUsuario(usuario);
        
        // Verificar cada logro
        verificarLogro(usuario, "Primeros Pasos", (int) juegosCompletados);
        verificarLogro(usuario, "Coleccionista", (int) logrosCompletados);
        verificarLogro(usuario, "Veterano", (int) totalHoras);
        verificarLogro(usuario, "Socialito", (int) cantidadAmigos);
        verificarLogro(usuario, "Explorador", (int) juegosDiferentes);
        verificarLogro(usuario, "Comprador Frecuente", (int) compras);
        verificarLogro(usuario, "Leyenda Local", usuario.getNivel());
        
        // Logro especial: Adicto (10 horas en un día)
        verificarLogroHorasDia(usuario);
    }
    
    private void verificarLogro(Usuario usuario, String nombreLogro, int progresoActual) {
        Optional<Logro> logroOpt = logroRepository.findByNombre(nombreLogro);
        if (logroOpt.isEmpty()) return;
        
        Logro logro = logroOpt.get();
        actualizarProgreso(usuario, logro, progresoActual);
    }
    
    private void verificarLogroHorasDia(Usuario usuario) {
        Optional<Logro> logroOpt = logroRepository.findByNombre("Adicto");
        if (logroOpt.isEmpty()) return;
        
        Logro logro = logroOpt.get();
        
        // Calcular horas jugadas hoy
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        double horasHoy = usuarioJuegoRepository.findByUsuario(usuario).stream()
            .filter(uj -> uj.getUltimaSesion() != null && 
                         uj.getUltimaSesion().isAfter(inicioDia))
            .mapToDouble(uj -> {
                // Aquí necesitarías registrar sesiones diarias
                // Por ahora, simplificamos
                return uj.getHorasJugadas() != null ? uj.getHorasJugadas() : 0;
            })
            .sum();
            
        actualizarProgreso(usuario, logro, (int) horasHoy);
    }
    
    @Transactional
    public void actualizarProgreso(Usuario usuario, Logro logro, Integer progresoActual) {
        UsuarioLogro usuarioLogro = usuarioLogroRepository
            .findByUsuarioAndLogro(usuario, logro)
            .orElse(UsuarioLogro.builder()
                .usuario(usuario)
                .logro(logro)
                .progreso(0)
                .completado(false)
                .build());
        
        if (usuarioLogro.getCompletado()) return;
        
        usuarioLogro.setProgreso(progresoActual);
        
        // ✅ CUANDO SE COMPLETA EL LOGRO
        if (progresoActual >= logro.getRequisitoValor()) {
            usuarioLogro.setCompletado(true);
            usuarioLogro.setFechaCompletado(LocalDateTime.now());
            
            // Dar XP
            usuario.setXp(usuario.getXp() + logro.getXpRecompensa());
            
            // Actualizar nivel
            int nuevoNivel = (usuario.getXp() / 1000) + 1;
            if (nuevoNivel > usuario.getNivel()) {
                usuario.setNivel(nuevoNivel);
            }
            
            usuarioRepository.save(usuario);
            
            // ✅ CREAR NOTIFICACIÓN
            notificationService.crearNotificacion(
                usuario,
                "¡Logro desbloqueado: " + logro.getNombre() + "! +" + logro.getXpRecompensa() + " XP",
                "LOGRO"
            );
        }
        
        usuarioLogroRepository.save(usuarioLogro);
    }
    
    public List<UsuarioLogro> getLogrosRecientes(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return usuarioLogroRepository.findByUsuarioOrderByProgresoDesc(usuario)
            .stream()
            .limit(3)
            .toList();
    }
    
    public List<UsuarioLogro> getTodosLosLogros(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return usuarioLogroRepository.findByUsuarioOrderByProgresoDesc(usuario);
    }
}
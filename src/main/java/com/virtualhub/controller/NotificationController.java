package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.service.NotificationService;
import com.virtualhub.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UsuarioService usuarioService;
    
    @GetMapping("/recientes")
    public ResponseEntity<List<Map<String, Object>>> getNotificacionesRecientes(Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        
        List<Map<String, Object>> notificaciones = notificationService
            .getNotificacionesNoLeidas(usuario)
            .stream()
            .map(n -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", n.getId());
                map.put("mensaje", n.getMensaje());
                map.put("tipo", n.getTipo());
                map.put("leida", n.getLeida());
                map.put("fecha", n.getFechaCreacion()
                    .format(DateTimeFormatter.ofPattern("dd MMM HH:mm")));
                return map;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(notificaciones);
    }
    
    @GetMapping("/no-leidas")
    public ResponseEntity<Map<String, Long>> getCantidadNoLeidas(Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        long cantidad = notificationService.getNotificacionesNoLeidas(usuario).size();
        
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }
    
    @PostMapping("/marcar/{id}")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        notificationService.marcarComoLeida(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/marcar-todas")
    public ResponseEntity<?> marcarTodasComoLeidas(Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        notificationService.marcarTodasComoLeidas(usuario);
        return ResponseEntity.ok().build();
    }
}
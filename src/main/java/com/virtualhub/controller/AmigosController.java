package com.virtualhub.controller;

import com.virtualhub.model.Amigo;
import com.virtualhub.model.Usuario;
import com.virtualhub.service.AmigoService;
import com.virtualhub.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/amigos")
@RequiredArgsConstructor
public class AmigosController {

    private final AmigoService amigoService;
    private final UsuarioService usuarioService;

    @PostMapping("/solicitud")
    public ResponseEntity<?> enviarSolicitud(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String emailAmigo = request.get("email");
            Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
            
            amigoService.enviarSolicitud(usuario.getId(), emailAmigo);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Solicitud enviada correctamente a " + emailAmigo
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    // ✅ NUEVO: Obtener solicitudes RECIBIDAS (las que te han enviado a ti)
    @GetMapping("/solicitudes/recibidas")
    public ResponseEntity<?> solicitudesRecibidas(Authentication authentication) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
            List<Amigo> solicitudes = amigoService.obtenerSolicitudesRecibidas(usuario);
            
            List<Map<String, Object>> response = solicitudes.stream().map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("nombre", s.getUsuario().getNombre());
                map.put("email", s.getUsuario().getEmail());
                map.put("avatar", s.getUsuario().getAvatarUrl());
                map.put("fecha", s.getFechaSolicitud().toString());
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ✅ NUEVO: Aceptar solicitud
    @PostMapping("/solicitudes/{id}/aceptar")
    public ResponseEntity<?> aceptarSolicitud(@PathVariable Long id) {
        try {
            amigoService.aceptarSolicitud(id);
            return ResponseEntity.ok(Map.of("mensaje", "Solicitud aceptada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ✅ NUEVO: Rechazar solicitud
    @PostMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long id) {
        try {
            amigoService.rechazarSolicitud(id);
            return ResponseEntity.ok(Map.of("mensaje", "Solicitud rechazada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/pendientes")
    public ResponseEntity<?> solicitudesPendientes(Authentication authentication) {
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        long cantidad = amigoService.contarSolicitudesPendientes(usuario);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }
}
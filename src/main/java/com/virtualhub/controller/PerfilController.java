package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.UsuarioJuego;
import com.virtualhub.model.UsuarioLogro;
import com.virtualhub.dto.PerfilDTO;
import com.virtualhub.model.Amigo;
import com.virtualhub.service.AmigoService;
import com.virtualhub.service.LogroService;
import com.virtualhub.service.NotificationService;
import com.virtualhub.service.UsuarioJuegoService;
import com.virtualhub.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final UsuarioJuegoService usuarioJuegoService;
    private final LogroService logroService;
    private final AmigoService amigoService;
    private final NotificationService notificationService;

    @GetMapping
    public String perfil(Model model, Authentication authentication) {

        if (authentication == null) return "redirect:/login";
        
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        
        // Obtener amigos
        List<Usuario> amigos = amigoService.obtenerAmigos(usuario);
        
        // Obtener solicitudes de amistad recibidas
        List<Amigo> solicitudesRecibidas = amigoService.obtenerSolicitudesRecibidas(usuario);
        
        List<UsuarioJuego> biblioteca = usuarioJuegoService.obtenerBiblioteca(usuario);
        
        UsuarioJuego juegoMasJugado = biblioteca.stream()
                .filter(uj -> uj.getHorasJugadas() != null)
                .max(Comparator.comparingDouble(UsuarioJuego::getHorasJugadas))
                .orElse(null);
        
        // Calcular horas totales
        double totalHoras = biblioteca.stream()
                .mapToDouble(uj -> uj.getHorasJugadas() != null ? uj.getHorasJugadas() : 0)
                .sum();
        
        // Calcular valor biblioteca
        double valorBiblioteca = biblioteca.stream()
                .mapToDouble(uj -> uj.getJuego() != null && uj.getJuego().getPrecio() != null 
                        ? uj.getJuego().getPrecio() : 0)
                .sum();
        
        // Calcular nivel
        int nivel = totalHoras > 0 ? (int) (totalHoras / 10) + 1 : 1;
        int xp = (int) ((totalHoras % 10) * 100);
        
        // Verificar logros
        try {
            logroService.verificarYActualizarLogros(usuario.getId());
        } catch (Exception e) {
            System.out.println("Error al verificar logros: " + e.getMessage());
        }
        
        // Obtener logros recientes
        List<?> logrosRecientes = new ArrayList<>();
        long logrosCompletados = 0;
        
        try {
            logrosRecientes = logroService.getLogrosRecientes(usuario.getId());
            logrosCompletados = logrosRecientes.stream()
                    .filter(l -> l instanceof UsuarioLogro && ((UsuarioLogro) l).getCompletado() != null 
                            && ((UsuarioLogro) l).getCompletado())
                    .count();
        } catch (Exception e) {
            logrosRecientes = new ArrayList<>();
            logrosCompletados = 0;
        }
        
        // Obtener notificaciones no leídas
        long notificacionesNoLeidas = 0;
        try {
            notificacionesNoLeidas = notificationService.getNotificacionesNoLeidas(usuario).size();
        } catch (Exception e) {
            System.out.println("Error al obtener notificaciones: " + e.getMessage());
        }
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("biblioteca", biblioteca);
        model.addAttribute("juegoMasJugado", juegoMasJugado);
        model.addAttribute("totalHoras", totalHoras);
        model.addAttribute("valorBiblioteca", valorBiblioteca);
        model.addAttribute("nivel", nivel);
        model.addAttribute("xp", xp);
        model.addAttribute("logrosRecientes", logrosRecientes);
        model.addAttribute("logrosCompletados", logrosCompletados);
        model.addAttribute("amigos", amigos);
        model.addAttribute("solicitudesRecibidas", solicitudesRecibidas);
        model.addAttribute("notificacionesNoLeidas", notificacionesNoLeidas);
        model.addAttribute("activePage", "perfil");

        return "perfil";
    }
    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarPerfil(@RequestBody PerfilDTO perfilDTO, Authentication authentication) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
            Usuario actualizado = usuarioService.actualizarPerfil(usuario.getId(), perfilDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Perfil actualizado correctamente");
            response.put("usuario", actualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
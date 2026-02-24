package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.UsuarioJuego;
import com.virtualhub.model.UsuarioLogro;
import com.virtualhub.service.AmigoService;
import com.virtualhub.service.LogroService;
import com.virtualhub.service.NotificationService; // ✅ IMPORTAR
import com.virtualhub.service.UsuarioJuegoService;
import com.virtualhub.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final UsuarioJuegoService usuarioJuegoService;
    private final LogroService logroService;
    private final AmigoService amigoService;
    private final NotificationService notificationService; // ✅ AGREGADO

    @GetMapping
    public String perfil(Model model, Authentication authentication) {

        if (authentication == null) return "redirect:/login";
        
        // Obtener usuario
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        
        // Obtener amigos
        List<Usuario> amigos = amigoService.obtenerAmigos(usuario);
        
        // Obtener biblioteca
        List<UsuarioJuego> biblioteca = usuarioJuegoService.obtenerBiblioteca(usuario);
        
        // DEBUG
        System.out.println("=== VALORES REALES DE BIBLIOTECA ===");
        for (UsuarioJuego uj : biblioteca) {
            System.out.println("Juego: " + uj.getJuego().getTitulo() + 
                               " - Horas: " + uj.getHorasJugadas() +
                               " - Precio: " + uj.getJuego().getPrecio());
        }
        
        // Juego más jugado
        UsuarioJuego juegoMasJugado = biblioteca.stream()
                .filter(uj -> uj.getHorasJugadas() != null)
                .max(Comparator.comparingDouble(UsuarioJuego::getHorasJugadas))
                .orElse(null);
        
        // Calcular horas totales
        double totalHoras = 0;
        for (UsuarioJuego uj : biblioteca) {
            if (uj.getHorasJugadas() != null) {
                totalHoras += uj.getHorasJugadas();
            }
        }
        System.out.println("Total horas calculadas: " + totalHoras);
        
        // Calcular valor biblioteca
        double valorBiblioteca = 0;
        for (UsuarioJuego uj : biblioteca) {
            if (uj.getJuego() != null && uj.getJuego().getPrecio() != null) {
                valorBiblioteca += uj.getJuego().getPrecio();
            }
        }
        System.out.println("Valor biblioteca calculado: " + valorBiblioteca);
        
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
        
        // ✅ NUEVO: Obtener notificaciones no leídas
        long notificacionesNoLeidas = 0;
        try {
            notificacionesNoLeidas = notificationService.getNotificacionesNoLeidas(usuario).size();
        } catch (Exception e) {
            System.out.println("Error al obtener notificaciones: " + e.getMessage());
        }
        
        // Agregar todo al modelo
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
        model.addAttribute("notificacionesNoLeidas", notificacionesNoLeidas); // ✅ AGREGADO
        model.addAttribute("activePage", "perfil");

        return "perfil";
    }
}
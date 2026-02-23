package com.virtualhub.controller;

import com.virtualhub.model.Juego;
import com.virtualhub.model.Usuario;
import com.virtualhub.model.UsuarioJuego;
import com.virtualhub.service.JuegoService;
import com.virtualhub.service.UsuarioJuegoService;
import com.virtualhub.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/biblioteca")
public class BibliotecaController {

    private final UsuarioService usuarioService;
    private final JuegoService juegoService;
    private final UsuarioJuegoService usuarioJuegoService;

    @GetMapping
    public String biblioteca(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        List<UsuarioJuego> biblioteca = usuarioJuegoService.obtenerBiblioteca(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("biblioteca", biblioteca);
        model.addAttribute("activePage", "biblioteca");   // ← ya lo tenías, perfecto

        return "biblioteca";
    }

    @PostMapping("/jugar/{id}")
    public ResponseEntity<String> jugar(@PathVariable Long id, Authentication authentication) {
        try {
            if (authentication == null) return ResponseEntity.status(401).body("No autenticado");

            Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
            Juego juego = juegoService.buscarPorId(id);

            if (juego == null) return ResponseEntity.badRequest().body("Juego no encontrado");

            usuarioJuegoService.iniciarJuego(usuario, juego);
            return ResponseEntity.ok("Juego iniciado");
        } catch (Exception e) {
            log.error("Error al iniciar juego {}", id, e);
            return ResponseEntity.internalServerError().body("Error interno");
        }
    }

    @PostMapping("/cerrar-juego/{id}")
    public ResponseEntity<String> cerrarJuego(@PathVariable Long id, Authentication authentication) {
        try {
            if (authentication == null) return ResponseEntity.status(401).body("No autenticado");

            Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
            Juego juego = juegoService.buscarPorId(id);

            if (juego == null) return ResponseEntity.badRequest().body("Juego no encontrado");

            usuarioJuegoService.cerrarJuego(usuario, juego);
            return ResponseEntity.ok("Juego detenido");
        } catch (Exception e) {
            log.error("Error al cerrar juego {}", id, e);
            return ResponseEntity.internalServerError().body("Error interno");
        }
    }
}
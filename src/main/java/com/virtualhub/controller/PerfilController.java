package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.UsuarioJuego;
import com.virtualhub.service.UsuarioJuegoService;
import com.virtualhub.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final UsuarioJuegoService usuarioJuegoService;

    @GetMapping
    public String perfil(Model model, Authentication authentication) {

        if (authentication == null) return "redirect:/login";

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        List<UsuarioJuego> biblioteca = usuarioJuegoService.obtenerBiblioteca(usuario);

        // Juego más jugado
        UsuarioJuego juegoMasJugado = biblioteca.stream()
                .filter(uj -> uj.getHorasJugadas() != null)
                .max(Comparator.comparingDouble(UsuarioJuego::getHorasJugadas))
                .orElse(null);

        // Total horas jugadas
        double totalHoras = biblioteca.stream()
                .mapToDouble(uj -> uj.getHorasJugadas() != null ? uj.getHorasJugadas() : 0)
                .sum();

        int nivel = (int) (totalHoras / 10); // 10 horas = 1 nivel
        int xp = (int) (totalHoras % 10) * 10;

        model.addAttribute("usuario", usuario);
        model.addAttribute("biblioteca", biblioteca);
        model.addAttribute("juegoMasJugado", juegoMasJugado);
        model.addAttribute("nivel", nivel);
        model.addAttribute("xp", xp);
        model.addAttribute("activePage", "perfil");

        return "perfil";
    }
}
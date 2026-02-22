package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.UsuarioJuego;
import com.virtualhub.model.Juego;
import com.virtualhub.service.UsuarioService;
import com.virtualhub.service.JuegoService;
import com.virtualhub.service.UsuarioJuegoService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UsuarioService usuarioService;
    private final JuegoService juegoService;
    private final UsuarioJuegoService usuarioJuegoService;

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());

        List<Juego> juegos = juegoService.listarTodos();
        List<UsuarioJuego> biblioteca = usuarioJuegoService.obtenerBiblioteca(usuario);

        Set<Long> juegosComprados = biblioteca.stream()
                .map(uj -> uj.getJuego().getId())
                .collect(Collectors.toSet());

        model.addAttribute("usuario", usuario);
        model.addAttribute("juegos", juegos);
        model.addAttribute("juegosComprados", juegosComprados);

        return "home";
    }
}
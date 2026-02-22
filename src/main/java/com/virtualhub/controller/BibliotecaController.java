package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.UsuarioJuego;
import com.virtualhub.model.Juego;
import com.virtualhub.service.UsuarioService;
import com.virtualhub.service.JuegoService;
import com.virtualhub.service.UsuarioJuegoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BibliotecaController {

    private final UsuarioService usuarioService;
    private final JuegoService juegoService;
    private final UsuarioJuegoService usuarioJuegoService;

    @GetMapping("/biblioteca")
    public String biblioteca(Model model, Authentication authentication) {

        Usuario usuario =
                usuarioService.buscarPorEmail(authentication.getName());

        List<UsuarioJuego> biblioteca =
                usuarioJuegoService.obtenerBiblioteca(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("biblioteca", biblioteca);

        return "biblioteca";
    }

    @PostMapping("/jugar/{id}")
    @ResponseBody
    public void jugar(@PathVariable Long id,
                      Authentication authentication) {

        Usuario usuario =
                usuarioService.buscarPorEmail(authentication.getName());

        Juego juego = juegoService.buscarPorId(id);

        usuarioJuegoService.iniciarJuego(usuario, juego);
    }

    @PostMapping("/cerrar-juego/{id}")
    @ResponseBody
    public void cerrarJuego(@PathVariable Long id,
                            Authentication authentication) {

        Usuario usuario =
                usuarioService.buscarPorEmail(authentication.getName());

        Juego juego = juegoService.buscarPorId(id);

        usuarioJuegoService.cerrarJuego(usuario, juego);
    }
}
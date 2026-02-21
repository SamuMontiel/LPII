package com.virtualhub.controller;

import com.virtualhub.model.Juego;
import com.virtualhub.model.Usuario;
import com.virtualhub.repository.JuegoRepository;
import com.virtualhub.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class JuegoController {

    private final JuegoRepository juegoRepository;
    private final UsuarioRepository usuarioRepository;

    public JuegoController(JuegoRepository juegoRepository,
                           UsuarioRepository usuarioRepository) {
        this.juegoRepository = juegoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/comprar/{id}")
    public String comprarJuego(@PathVariable Long id,
                               Authentication authentication) {

        Juego juego = juegoRepository.findById(id).orElse(null);
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElse(null);

        if (juego != null && usuario != null) {
            if (!usuario.getBiblioteca().contains(juego)) {
                usuario.getBiblioteca().add(juego);
                usuarioRepository.save(usuario);
            }
        }

        return "redirect:/juego/" + id;
    }
}

package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.Juego;
import com.virtualhub.service.UsuarioService;
import com.virtualhub.service.JuegoService;
import com.virtualhub.service.UsuarioJuegoService;

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

        model.addAttribute("usuario", usuario);
        model.addAttribute("juegos", juegoService.listarTodos());

        model.addAttribute("biblioteca",
                usuarioJuegoService.obtenerBiblioteca(usuario));

        return "home";
    }

    @Transactional
    @PostMapping("/comprar/{id}")
    public String comprarJuego(@PathVariable Long id,
                               Authentication authentication) {

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        Juego juego = juegoService.buscarPorId(id);

     
        if (usuario.getSaldo() < juego.getPrecio()) {
            return "redirect:/home";
        }

        usuario.setSaldo(usuario.getSaldo() - juego.getPrecio());
        usuarioService.guardar(usuario);

   
        usuarioJuegoService.agregarJuego(usuario, juego);

        return "redirect:/home";
    }
}
package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.Juego;
import com.virtualhub.service.UsuarioService;
import com.virtualhub.service.JuegoService;
//sdsdsddsds
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
//SSDSDSDSDSD
    private final UsuarioService usuarioService;
    private final JuegoService juegoService;

    public HomeController(UsuarioService usuarioService, JuegoService juegoService) {
        this.usuarioService = usuarioService;
        this.juegoService = juegoService;
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        model.addAttribute("usuario", usuario);
        model.addAttribute("juegos", juegoService.listarTodos());

        return "home";
    }
    @Transactional
    @PostMapping("/comprar/{id}")
    public String comprarJuego(@PathVariable Long id, Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);
        Juego juego = juegoService.buscarPorId(id);

        if (usuario.getBiblioteca().contains(juego)) {
            return "redirect:/home";
        }


        if (usuario.getSaldo() < juego.getPrecio()) {
            return "redirect:/home";
        }
        usuario.setSaldo(usuario.getSaldo() - juego.getPrecio());
        usuario.getBiblioteca().add(juego);
        usuarioService.guardar(usuario);
        return "redirect:/home";
    }
}
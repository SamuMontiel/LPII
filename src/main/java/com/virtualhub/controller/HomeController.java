package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.service.UsuarioService;
import com.virtualhub.service.JuegoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UsuarioService usuarioService;
    private final JuegoService juegoService;

    public HomeController(UsuarioService usuarioService, JuegoService juegoService) {
        this.usuarioService = usuarioService;
        this.juegoService = juegoService;
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        model.addAttribute("usuario", usuario);
        model.addAttribute("juegos", juegoService.listarJuegos());

        return "home";
    }
}

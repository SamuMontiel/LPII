package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BibliotecaController {

    private final UsuarioService usuarioService;

    public BibliotecaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/biblioteca")
    public String verBiblioteca(Authentication authentication, Model model) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        model.addAttribute("usuario", usuario);
        model.addAttribute("juegos", usuario.getBiblioteca());

        return "biblioteca";
    }

}

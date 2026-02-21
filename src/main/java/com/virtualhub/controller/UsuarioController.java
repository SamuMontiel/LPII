package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }


    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario,
                                    Model model) {


        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "El email ya está registrado");
            return "registro";
        }

 
        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );

  
        usuario.setSaldo(1000.0);

        usuarioRepository.save(usuario);

        return "redirect:/login?registrado";
    }
}

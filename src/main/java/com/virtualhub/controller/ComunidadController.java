package com.virtualhub.controller;

import com.virtualhub.entity.Comentario;
import com.virtualhub.model.Juego;
import com.virtualhub.model.Usuario;
import com.virtualhub.repository.ComentarioRepository;
import com.virtualhub.repository.JuegoRepository;
import com.virtualhub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/comunidad")
public class ComunidadController {

    @Autowired
    private JuegoRepository juegoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @GetMapping
    public String comunidad(Model model) {
        List<Juego> juegos = juegoRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Cargar comentarios asociados a cada juego
        juegos.forEach(juego -> {
            List<Comentario> comentarios = comentarioRepository.findByJuegoId(juego.getId());
            juego.setComentarios(comentarios);
        });

        model.addAttribute("juegos", juegos);
        model.addAttribute("usuarios", usuarios);
        return "comunidad";
    }

    @PostMapping("/comentar")
    public String comentar(@RequestParam Long juegoId,
                           @RequestParam String contenido,
                           Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName());
        Comentario comentario = new Comentario();
        comentario.setContenido(contenido);
        comentario.setFecha(LocalDateTime.now());
        comentario.setUsuario(usuario);
        comentario.setJuego(juegoRepository.findById(juegoId).orElse(null));

        comentarioRepository.save(comentario);
        return "redirect:/comunidad";
    }
}

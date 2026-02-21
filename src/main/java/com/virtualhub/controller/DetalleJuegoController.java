package com.virtualhub.controller;

import com.virtualhub.model.Juego;
import com.virtualhub.model.Resena;
import com.virtualhub.model.Usuario;
import com.virtualhub.repository.JuegoRepository;
import com.virtualhub.repository.ResenaRepository;
import com.virtualhub.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/juego")
public class DetalleJuegoController {

    private final JuegoRepository juegoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ResenaRepository resenaRepository;

    public DetalleJuegoController(JuegoRepository juegoRepository,
                                  UsuarioRepository usuarioRepository,
                                  ResenaRepository resenaRepository) {
        this.juegoRepository = juegoRepository;
        this.usuarioRepository = usuarioRepository;
        this.resenaRepository = resenaRepository;
    }

    @GetMapping("/{id}")
    public String detalleJuego(@PathVariable Long id,
                               Model model,
                               Authentication authentication) {

        Juego juego = juegoRepository.findById(id).orElse(null);

        if (juego == null) {
            return "redirect:/home";
        }

        model.addAttribute("juego", juego);
        model.addAttribute("resenas", resenaRepository.findByJuego(juego));

        boolean comprado = false;

        if (authentication != null) {
            Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElse(null);

            if (usuario != null) {
                comprado = usuario.getBiblioteca().contains(juego);
            }
        }

        model.addAttribute("comprado", comprado);

        return "detalle-juego";
    }

    @PostMapping("/{id}/resenar")
    public String resenar(@PathVariable Long id,
                          @RequestParam int puntuacion,
                          @RequestParam boolean recomendado,
                          @RequestParam String comentario,
                          Authentication authentication) {

        Juego juego = juegoRepository.findById(id).orElse(null);
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElse(null);

        if (juego != null && usuario != null && usuario.getBiblioteca().contains(juego)) {

            Resena resena = new Resena();
            resena.setJuego(juego);
            resena.setUsuario(usuario);
            resena.setPuntuacion(puntuacion);
            resena.setComentario(comentario);
            resena.setRecomendado(recomendado);
            resena.setFecha(LocalDateTime.now());

            resenaRepository.save(resena);
        }

        return "redirect:/juego/" + id;
    }
}

package com.virtualhub.controller;

import com.virtualhub.model.Comentario;
import com.virtualhub.model.Juego;
import com.virtualhub.model.Usuario;
import com.virtualhub.repository.ComentarioRepository;
import com.virtualhub.repository.JuegoRepository;
import com.virtualhub.repository.UsuarioRepository;
import com.virtualhub.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/comunidad")
public class ComunidadController {
    @Autowired
    private JuegoRepository juegoRepository;
    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String mostrarComunidad(Model model, Principal principal) {
        List<Juego> juegos = juegoRepository.findAll();
        model.addAttribute("juegos", juegos);

        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);

        model.addAttribute("totalUsuarios", usuarios.size());

        long totalComentarios = comentarioRepository.count();
        model.addAttribute("totalComentarios", totalComentarios);
        
        List<Juego> juegosPopular = juegos.stream()
                .sorted((j1, j2) -> Integer.compare(
                        j2.getComentarios() != null ? j2.getComentarios().size() : 0,
                        j1.getComentarios() != null ? j1.getComentarios().size() : 0))
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("juegosPopular", juegosPopular);

        if (principal != null) {
            Usuario usuarioLogueado = usuarioRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            model.addAttribute("usuarioLogueado", usuarioLogueado);
            model.addAttribute("usuario", usuarioLogueado); // 👈 PARA COMPATIBILIDAD CON EL NAVBAR
            
            long notificacionesNoLeidas = notificationService.getNotificacionesNoLeidas(usuarioLogueado).size();
            model.addAttribute("notificacionesNoLeidas", notificacionesNoLeidas);
        }

        model.addAttribute("activePage", "comunidad");
        return "comunidad";
    }

    @PostMapping("/comentar")
    public String agregarComentario(@RequestParam Long juegoId,
                                    @RequestParam String contenido,
                                    Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Juego juego = juegoRepository.findById(juegoId).orElseThrow();

        Comentario comentario = new Comentario();
        comentario.setContenido(contenido);
        comentario.setFecha(LocalDateTime.now());
        comentario.setUsuario(usuario);
        comentario.setJuego(juego);

        comentarioRepository.save(comentario);
        
        return "redirect:/comunidad";
    }

    @PostMapping("/editar")
    public String editarComentario(@RequestParam Long comentarioId,
                                   @RequestParam String contenido,
                                   Principal principal) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElseThrow();
        if (!comentario.getUsuario().getEmail().equals(principal.getName())) {
            throw new RuntimeException("No tienes permiso para editar este comentario");
        }
        comentario.setContenido(contenido);
        comentarioRepository.save(comentario);
        return "redirect:/comunidad";
    }

    @PostMapping("/responder")
    public String responderComentario(@RequestParam Long comentarioId,
                                      @RequestParam String contenido,
                                      Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Comentario comentarioPadre = comentarioRepository.findById(comentarioId).orElseThrow();

        Comentario respuesta = new Comentario();
        respuesta.setContenido(contenido);
        respuesta.setFecha(LocalDateTime.now());
        respuesta.setUsuario(usuario);
        respuesta.setJuego(comentarioPadre.getJuego());
        respuesta.setComentarioPadre(comentarioPadre);

        comentarioRepository.save(respuesta);
        return "redirect:/comunidad";
    }

    @PostMapping("/borrar")
    public String borrarComentario(@RequestParam Long comentarioId, Principal principal) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElseThrow();
        if (!comentario.getUsuario().getEmail().equals(principal.getName())) {
            throw new RuntimeException("No tienes permiso para borrar este comentario");
        }
        comentarioRepository.delete(comentario);
        return "redirect:/comunidad";
    }
}
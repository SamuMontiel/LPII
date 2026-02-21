package com.virtualhub.service;

import com.virtualhub.model.Juego;
import com.virtualhub.model.Usuario;
import com.virtualhub.model.UsuarioJuego;
import com.virtualhub.repository.UsuarioJuegoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioJuegoService {

    private final UsuarioJuegoRepository usuarioJuegoRepository;

    public List<UsuarioJuego> obtenerBiblioteca(Usuario usuario) {
        return usuarioJuegoRepository.findByUsuario(usuario);
    }

    public void agregarJuego(Usuario usuario, Juego juego) {

        if (usuarioJuegoRepository
                .findByUsuarioAndJuego(usuario, juego)
                .isPresent()) {
            return;
        }

        UsuarioJuego uj = UsuarioJuego.builder()
                .usuario(usuario)
                .juego(juego)
                .horasJugadas(0.0)
                .totalSesiones(0)
                .jugando(false)
                .horaInicio(null)
                .ultimaSesion(null)
                .build();

        usuarioJuegoRepository.save(uj);
    }

    @Transactional
    public void iniciarJuego(Usuario usuario, Juego juego) {

        List<UsuarioJuego> biblioteca =
                usuarioJuegoRepository.findByUsuario(usuario);

        // 🔥 SOLO UN JUEGO ACTIVO A LA VEZ
        for (UsuarioJuego item : biblioteca) {
            if (Boolean.TRUE.equals(item.getJugando())) {
                item.setJugando(false);
                item.setHoraInicio(null);
            }
        }

        UsuarioJuego uj = usuarioJuegoRepository
                .findByUsuarioAndJuego(usuario, juego)
                .orElseThrow(() ->
                        new RuntimeException("Juego no encontrado"));

        // Seguridad contra null
        if (uj.getTotalSesiones() == null) {
            uj.setTotalSesiones(0);
        }

        if (uj.getHorasJugadas() == null) {
            uj.setHorasJugadas(0.0);
        }

        uj.setJugando(true);
        uj.setHoraInicio(LocalDateTime.now());
        uj.setUltimaSesion(LocalDateTime.now());
        uj.setTotalSesiones(uj.getTotalSesiones() + 1);
 
        usuarioJuegoRepository.saveAll(biblioteca);
    }

    @Transactional
    public void cerrarJuego(Usuario usuario, Juego juego) {

        UsuarioJuego uj = usuarioJuegoRepository
                .findByUsuarioAndJuego(usuario, juego)
                .orElseThrow(() ->
                        new RuntimeException("Juego no encontrado"));

        if (Boolean.TRUE.equals(uj.getJugando())
                && uj.getHoraInicio() != null) {

            Duration duracion = Duration.between(
                    uj.getHoraInicio(),
                    LocalDateTime.now());

            double horas = duracion.toMinutes() / 60.0;

            if (uj.getHorasJugadas() == null) {
                uj.setHorasJugadas(0.0);
            }

            uj.setHorasJugadas(uj.getHorasJugadas() + horas);
            uj.setUltimaSesion(LocalDateTime.now());
        }

        uj.setJugando(false);
        uj.setHoraInicio(null);

        usuarioJuegoRepository.save(uj);
    }
}
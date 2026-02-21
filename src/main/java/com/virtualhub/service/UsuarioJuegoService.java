package com.virtualhub.service;

import com.virtualhub.model.*;
import com.virtualhub.repository.UsuarioJuegoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UsuarioJuegoService {

    private final UsuarioJuegoRepository repository;
    private final LogroService logroService; 

    public void agregarJuego(Usuario usuario, Juego juego) {

        if (repository.findByUsuarioAndJuego(usuario, juego).isPresent()) {
            return; 
        }

        UsuarioJuego uj = UsuarioJuego.builder()
                .usuario(usuario)
                .juego(juego)
                .horasJugadas(0.0)
                .build();

        repository.save(uj);
    }


    public void registrarSesion(Usuario usuario,
                                Juego juego,
                                double minutosJugados) {

        UsuarioJuego uj = repository
                .findByUsuarioAndJuego(usuario, juego)
                .orElseThrow();

        double horas = minutosJugados / 60.0;

        uj.setHorasJugadas(uj.getHorasJugadas() + horas);
        uj.setUltimaSesion(LocalDateTime.now());

        repository.save(uj);

        double totalHoras = uj.getHorasJugadas();

        if (totalHoras >= 1) {
            logroService.desbloquear(usuario, juego, "Primer Paso");
        }

        if (totalHoras >= 5) {
            logroService.desbloquear(usuario, juego, "Veterano");
        }

        if (totalHoras >= 20) {
            logroService.desbloquear(usuario, juego, "Adicto");
        }
    }

    public List<UsuarioJuego> obtenerBiblioteca(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }
}
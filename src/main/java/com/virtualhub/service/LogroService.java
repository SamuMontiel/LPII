package com.virtualhub.service;

import com.virtualhub.model.*;
import com.virtualhub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogroService {

    private final LogroRepository logroRepository;
    private final UsuarioLogroRepository usuarioLogroRepository;

    public void desbloquear(Usuario usuario, Juego juego, String nombreLogro) {

        Logro logro = logroRepository
                .findByNombreAndJuego(nombreLogro, juego)
                .orElse(null);

        if (logro == null) return;

        boolean yaDesbloqueado =
                usuarioLogroRepository
                        .findByUsuarioAndLogro(usuario, logro)
                        .isPresent();

        if (yaDesbloqueado) return;

        UsuarioLogro ul = UsuarioLogro.builder()
                .usuario(usuario)
                .logro(logro)
                .fechaDesbloqueo(LocalDateTime.now())
                .build();

        usuarioLogroRepository.save(ul);
    }
}
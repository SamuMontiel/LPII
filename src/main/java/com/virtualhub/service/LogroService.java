package com.virtualhub.service;

import com.virtualhub.model.*;
import com.virtualhub.repository.LogroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogroService {

    private final LogroRepository logroRepository;

    public void desbloquear(Usuario usuario, Juego juego, String nombreLogro) {

        boolean yaExiste = logroRepository
                .existsByUsuarioAndJuegoAndNombre(usuario, juego, nombreLogro);

        if (!yaExiste) {
            Logro logro = new Logro();
            logro.setUsuario(usuario);
            logro.setJuego(juego);
            logro.setNombre(nombreLogro);
            logroRepository.save(logro);
        }
    }
}
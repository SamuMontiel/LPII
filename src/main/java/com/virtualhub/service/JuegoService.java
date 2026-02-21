package com.virtualhub.service;

import org.springframework.stereotype.Service;
import com.virtualhub.model.Juego;
import com.virtualhub.repository.JuegoRepository;

import java.util.List;

@Service
public class JuegoService {

    private final JuegoRepository juegoRepository;

    public JuegoService(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public List<Juego> listarJuegos() {
        return juegoRepository.findAll();
    }

    public Juego guardarJuego(Juego juego) {
        return juegoRepository.save(juego);
    }
    public Juego buscarPorId(Long id) {
        return juegoRepository.findById(id).orElseThrow();
    }

}

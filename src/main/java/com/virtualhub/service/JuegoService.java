package com.virtualhub.service;

import com.virtualhub.model.Juego;
import com.virtualhub.repository.JuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JuegoService {

    private final JuegoRepository juegoRepository;

    public JuegoService(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public List<Juego> listarTodos() {
        return juegoRepository.findAll();
    }

    public Juego buscarPorId(Long id) {
        return juegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
    }
}
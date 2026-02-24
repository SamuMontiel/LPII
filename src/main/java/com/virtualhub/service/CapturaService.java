package com.virtualhub.service;

import com.virtualhub.model.Captura;
import com.virtualhub.model.Juego;
import com.virtualhub.model.Usuario;
import com.virtualhub.repository.CapturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CapturaService {
    
    private final CapturaRepository capturaRepository;
    
    @Transactional
    public Captura subirCaptura(Usuario usuario, Juego juego, String imagenUrl, String titulo) {
        Captura captura = Captura.builder()
            .usuario(usuario)
            .juego(juego)
            .imagenUrl(imagenUrl)
            .titulo(titulo)
            .fechaSubida(LocalDateTime.now())
            .likes(0)
            .build();
        
        return capturaRepository.save(captura);
    }
    
    public List<Captura> obtenerCapturasRecientes(Usuario usuario) {
        return capturaRepository.findByUsuarioOrderByFechaSubidaDesc(usuario);
    }
    
    @Transactional
    public void darLike(Long capturaId) {
        Captura captura = capturaRepository.findById(capturaId)
            .orElseThrow(() -> new RuntimeException("Captura no encontrada"));
        captura.setLikes(captura.getLikes() + 1);
        capturaRepository.save(captura);
    }
}
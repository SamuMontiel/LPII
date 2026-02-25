package com.virtualhub.service;

import com.virtualhub.dto.PerfilDTO;
import com.virtualhub.model.Usuario;
import com.virtualhub.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    // ✅ AGREGAR ESTE MÉTODO
    @Transactional
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    
    @Transactional
    public Usuario actualizarPerfil(Long usuarioId, PerfilDTO perfilDTO) {
        Usuario usuario = buscarPorId(usuarioId);
        
        if (perfilDTO.getNombre() != null && !perfilDTO.getNombre().isEmpty()) {
            usuario.setNombre(perfilDTO.getNombre());
        }
        
        if (perfilDTO.getBio() != null) {
            usuario.setBio(perfilDTO.getBio());
        }
        
        if (perfilDTO.getAvatarUrl() != null && !perfilDTO.getAvatarUrl().isEmpty()) {
            usuario.setAvatarUrl(perfilDTO.getAvatarUrl());
        }
        
        if (perfilDTO.getBannerColor() != null && !perfilDTO.getBannerColor().isEmpty()) {
            usuario.setBannerColor(perfilDTO.getBannerColor());
        }
        
        return usuarioRepository.save(usuario);
    }
}
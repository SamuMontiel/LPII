package com.virtualhub.service;

import com.virtualhub.model.*;
import com.virtualhub.repository.CarritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioJuegoService usuarioJuegoService;
    private final UsuarioService usuarioService;

    public void agregar(Usuario usuario, Juego juego) {

        // Si ya lo tiene comprado, no permitir
        if (usuarioJuegoService.existe(usuario, juego)) {
            return;
        }

        // Evitar duplicado en carrito
        carritoRepository.findByUsuarioAndJuego(usuario, juego)
                .orElseGet(() -> {
                    Carrito item = new Carrito();
                    item.setUsuario(usuario);
                    item.setJuego(juego);
                    return carritoRepository.save(item);
                });
    }

    public List<Carrito> obtenerPorUsuario(Usuario usuario) {
        return carritoRepository.findByUsuario(usuario);
    }

    @Transactional
    public void confirmarCompra(Usuario usuario) {

        List<Carrito> items = carritoRepository.findByUsuario(usuario);

        if (items.isEmpty()) return;

        double total = items.stream()
                .mapToDouble(i -> i.getJuego().getPrecio())
                .sum();

        if (usuario.getSaldo() < total) {
            throw new RuntimeException("Saldo insuficiente");
        }

        usuario.setSaldo(usuario.getSaldo() - total);
        usuarioService.guardar(usuario);

        for (Carrito item : items) {
            usuarioJuegoService.agregarJuego(usuario, item.getJuego());
        }

        carritoRepository.deleteByUsuario(usuario);
    }

    public void eliminar(Long id) {
        carritoRepository.deleteById(id);
    }
}
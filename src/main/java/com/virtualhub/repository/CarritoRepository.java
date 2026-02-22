package com.virtualhub.repository;

import com.virtualhub.model.Carrito;
import com.virtualhub.model.Usuario;

import jakarta.transaction.Transactional;

import com.virtualhub.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    List<Carrito> findByUsuario(Usuario usuario);

    Optional<Carrito> findByUsuarioAndJuego(Usuario usuario, Juego juego);

    void deleteByUsuario(Usuario usuario);
}
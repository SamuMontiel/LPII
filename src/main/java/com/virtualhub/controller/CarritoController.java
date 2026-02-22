package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.Juego;
import com.virtualhub.model.Carrito;
import com.virtualhub.service.CarritoService;
import com.virtualhub.service.UsuarioService;
import com.virtualhub.service.JuegoService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;
    private final JuegoService juegoService;

    // ✅ Ver carrito
    @GetMapping
    public String verCarrito(Model model, Authentication authentication) {

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());

        List<Carrito> items = carritoService.obtenerPorUsuario(usuario);

        double total = items.stream()
                .mapToDouble(i -> i.getJuego().getPrecio())
                .sum();

        model.addAttribute("usuario", usuario);
        model.addAttribute("items", items);
        model.addAttribute("total", total);

        return "carrito";
    }

    // ✅ Agregar al carrito
    @PostMapping("/agregar/{id}")
    public String agregar(@PathVariable Long id,
                          Authentication authentication) {

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        Juego juego = juegoService.buscarPorId(id);

        carritoService.agregar(usuario, juego);

        return "redirect:/carrito";
    }

    // ✅ Confirmar compra
    @PostMapping("/confirmar")
    public String confirmar(Authentication authentication) {

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());

        carritoService.confirmarCompra(usuario);

        return "redirect:/biblioteca";
    }

    // ✅ Eliminar item individual
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        carritoService.eliminar(id);

        return "redirect:/carrito";
    }
}
package com.virtualhub.controller;

import com.virtualhub.model.Usuario;
import com.virtualhub.model.Juego;
import com.virtualhub.model.Carrito;
import com.virtualhub.service.CarritoService;
import com.virtualhub.service.UsuarioService;
import com.virtualhub.service.JuegoService;
import com.virtualhub.service.ReportService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;
    private final JuegoService juegoService;
    private final ReportService reportService;

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

    // ✅ Confirmar compra (SOLO confirma y redirige)
    @PostMapping("/confirmar")
    public ResponseEntity<byte[]> confirmar(Authentication authentication) throws Exception {

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());

        // 1️⃣ Obtener items ANTES de borrarlos
        List<Carrito> items = carritoService.obtenerItemsCompra(usuario);

        // 2️⃣ Confirmar compra (esto borra carrito)
        carritoService.confirmarCompra(usuario);

        // 3️⃣ Generar factura con los items guardados en memoria
        byte[] pdf = reportService.generarFactura(items, usuario);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Factura_VirtualHub.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdf);
    }

    // ✅ Descargar factura (separado y profesional)
    @GetMapping("/factura")
    public ResponseEntity<byte[]> descargarFactura(Authentication authentication) throws Exception {

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());

        // Aquí deberías obtener la última compra guardada en historial
        // Por ahora usamos el carrito antes de vaciarlo si lo guardas en sesión
        List<Carrito> items = carritoService.obtenerItemsCompra(usuario);

        byte[] pdf = reportService.generarFactura(items, usuario);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Factura_VirtualHub.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdf);
    }

    // ✅ Eliminar item individual
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        carritoService.eliminar(id);

        return "redirect:/carrito";
    }
}
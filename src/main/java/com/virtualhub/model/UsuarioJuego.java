package com.virtualhub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_juego")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioJuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "juego_id")
    private Juego juego;

    private Double horasJugadas = 0.0;

    private LocalDateTime ultimaSesion;
}
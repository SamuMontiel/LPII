package com.virtualhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioJuego {

	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Juego juego;

    @Column(nullable = false)
    private Double horasJugadas = 0.0;

    @Column(nullable = false)
    private Integer totalSesiones = 0;

    @Column(nullable = false)
    private Boolean jugando = false;
    
    private LocalDateTime horaInicio;

    private LocalDateTime ultimaSesion;
}
package com.virtualhub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "capturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Captura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "juego_id", nullable = false)
    private Juego juego;
    
    private String titulo;
    
    @Column(nullable = false)
    private String imagenUrl;
    
    private LocalDateTime fechaSubida;
    
    private Integer likes = 0;
}
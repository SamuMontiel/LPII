package com.virtualhub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "amigos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amigo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "amigo_id", nullable = false)
    private Usuario amigo;
    
    private String estado; // PENDIENTE, ACEPTADO, RECHAZADO
    
    private LocalDateTime fechaSolicitud;
    
    private LocalDateTime fechaAceptacion;
}
package com.virtualhub.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "logros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Logro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "juego_id")
    private Juego juego;
}
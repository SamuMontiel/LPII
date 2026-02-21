package com.virtualhub.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "juegos")
@Data

@NoArgsConstructor
@AllArgsConstructor
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private Double precio;
    private String imagenUrl;

    @OneToMany(mappedBy = "juego")
    private List<Resena> resenas = new ArrayList<>();
}
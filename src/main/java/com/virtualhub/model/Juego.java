package com.virtualhub.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "juegos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    private Double precio;

    private String trailerUrl;
    private String bannerUrl;
    private String coverUrl;
    private String logoUrl;
    private String screenshotBaseName;
}
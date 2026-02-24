package com.virtualhub.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String password;

    private Double saldo = 500.0;
    
    @Column(length = 500) 
    private String bio;
    
    private String avatarUrl;
    
    private String bannerColor = "#1f2833"; 

    @ManyToMany
    @JoinTable(
            name = "biblioteca",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "juego_id")
    )
    private Set<Juego> biblioteca = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private List<Resena> resenas = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioLogro> logros = new ArrayList<>();

    private Integer nivel = 1;
    private Integer xp = 0;
    private Integer horasTotales = 0;
}
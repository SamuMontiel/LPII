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

    @ManyToMany
    @JoinTable(
            name = "biblioteca",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "juego_id")
    )
    private Set<Juego> biblioteca = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private List<Resena> resenas = new ArrayList<>();
}

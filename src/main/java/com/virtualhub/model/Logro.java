package com.virtualhub.model;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Data
@Entity
@Table(name = "logros")
public class Logro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    private String descripcion;
    private String icono;
    private Integer xpRecompensa;
    private String requisitoTipo;
    private Integer requisitoValor;
    
    @OneToMany(mappedBy = "logro")
    private List<UsuarioLogro> usuarios;
}
package com.virtualhub.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Amistad {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Usuario usuario1;

    @ManyToOne
    private Usuario usuario2;

    private Boolean aceptada;
}
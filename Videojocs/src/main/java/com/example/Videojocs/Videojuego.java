package com.example.Videojocs;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "videojuegos_indie")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Videojuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String estudio;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    private int pegi;
}

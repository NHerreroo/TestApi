package com.example.Videojocs;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideojuegoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String estudio;
    private LocalDate fechaSalida;
    private int pegi;
}

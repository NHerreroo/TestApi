package com.example.Videojocs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/videojuegos")
public class VideojuegoController {

    private final VideojuegoService videojuegoService;

    public VideojuegoController(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    // GET: Lista todos los videojuegos
    @GetMapping
    public List<Videojuego> obtenerTodos() {
        return videojuegoService.obtenerTodos();
    }

    // GET: Obtiene un videojuego por ID
    @GetMapping("/{id}")
    public ResponseEntity<Videojuego> obtenerPorId(@PathVariable Long id) {
        Optional<Videojuego> videojuego = videojuegoService.obtenerPorId(id);
        return videojuego.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET: Devuelve solo el estudio de un videojuego
    @GetMapping("/{id}/estudio")
    public ResponseEntity<String> obtenerEstudio(@PathVariable Long id) {
        Optional<Videojuego> videojuego = videojuegoService.obtenerPorId(id);
        return videojuego.map(v -> ResponseEntity.ok(v.getEstudio()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: Crea un videojuego
    @PostMapping
    public Videojuego agregar(@RequestBody Videojuego videojuego) {
        return videojuegoService.guardar(videojuego);
    }

    // DELETE: Elimina un videojuego por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (videojuegoService.obtenerPorId(id).isPresent()) {
            videojuegoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT: Reemplaza un videojuego por ID
    @PutMapping("/{id}")
    public ResponseEntity<Videojuego> actualizar(@PathVariable Long id, @RequestBody Videojuego nuevoVideojuego) {
        return videojuegoService.obtenerPorId(id).map(videojuego -> {
            videojuego.setNombre(nuevoVideojuego.getNombre());
            videojuego.setDescripcion(nuevoVideojuego.getDescripcion());
            videojuego.setEstudio(nuevoVideojuego.getEstudio());
            videojuego.setFechaSalida(nuevoVideojuego.getFechaSalida());
            videojuego.setPegi(nuevoVideojuego.getPegi());
            return ResponseEntity.ok(videojuegoService.guardar(videojuego));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PATCH: Ejemplo de modificación masiva (subir el PEGI en +1 a todos los videojuegos)
    @PatchMapping
    public ResponseEntity<Void> incrementarPegi() {
        List<Videojuego> videojuegos = videojuegoService.obtenerTodos();
        for (Videojuego v : videojuegos) {
            if (v.getPegi() < 18) { // PEGI máximo es 18
                v.setPegi(v.getPegi() + 1);
                videojuegoService.guardar(v);
            }
        }
        return ResponseEntity.ok().build();
    }
}

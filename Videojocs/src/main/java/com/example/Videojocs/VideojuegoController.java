package com.example.Videojocs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
    @RequestMapping("/api/v1/videojuegos")
public class VideojuegoController {


    @Autowired
    private ObjectMapper objectMapper;

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
    public ResponseEntity<Map<String, String>> obtenerEstudio(@PathVariable Long id) {
        Optional<Videojuego> videojuego = videojuegoService.obtenerPorId(id);
        return videojuego
                .map(v -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("estudio", v.getEstudio());
                    return ResponseEntity.ok(response);
                })
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



    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Videojuego> patchVideojuego(@PathVariable Long id, @RequestBody JsonPatch patch) {
        return (ResponseEntity<Videojuego>) videojuegoService.obtenerPorId(id)
                .map(original -> {
                    try {
                        Videojuego patched = applyPatchToVideojuego(patch, original);
                        videojuegoService.guardar(patched);
                        return ResponseEntity.ok(patched);
                    } catch (JsonPatchException | JsonProcessingException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Videojuego applyPatchToVideojuego(JsonPatch patch, Videojuego targetVideojuego)
            throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(targetVideojuego, JsonNode.class));
        return objectMapper.treeToValue(patched, Videojuego.class);
    }

}

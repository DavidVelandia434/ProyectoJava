package libreria.api.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import libreria.api.Respuestas;
import libreria.api.dto.AutorRequest;
import libreria.model.Autor;
import libreria.service.AutorService;

@RestController
@RequestMapping("/api/autores")
public class AutorController {
    private final AutorService autores;

    public AutorController(AutorService autores) {
        this.autores = autores;
    }

    @GetMapping
    public List<Autor> listar() {
        return autores.listar();
    }

    @PostMapping
    public ResponseEntity<Autor> crear(@Valid @RequestBody AutorRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autores.registrar(r.nombre(), r.nacionalidad()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        return Respuestas.eliminacion(autores.eliminar(id));
    }
}

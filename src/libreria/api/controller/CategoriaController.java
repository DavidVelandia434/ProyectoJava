/*
 * Programa : Sistema de gestión de librería
 * Archivo  : CategoriaController.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import libreria.api.Respuestas;
import libreria.api.dto.CategoriaRequest;
import libreria.model.Categoria;
import libreria.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaService categorias;

    public CategoriaController(CategoriaService categorias) {
        this.categorias = categorias;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categorias.listar();
    }

    @PostMapping
    public ResponseEntity<Categoria> crear(@Valid @RequestBody CategoriaRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categorias.registrar(r.nombre()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        return Respuestas.eliminacion(categorias.eliminar(id));
    }
}

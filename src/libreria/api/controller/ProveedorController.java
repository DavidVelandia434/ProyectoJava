/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ProveedorController.java
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import libreria.api.Respuestas;
import libreria.api.dto.ProveedorRequest;
import libreria.model.Proveedor;
import libreria.service.ProveedorService;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {
    private final ProveedorService proveedores;

    public ProveedorController(ProveedorService proveedores) {
        this.proveedores = proveedores;
    }

    @GetMapping
    public List<Proveedor> listar() {
        return proveedores.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtener(@PathVariable int id) {
        return proveedores.listar().stream().filter(p -> p.getId() == id).findFirst()
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Proveedor> crear(@Valid @RequestBody ProveedorRequest r) {
        Proveedor p = proveedores.registrar(r.nombre(), r.telefono(), r.correo());
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable int id, @Valid @RequestBody ProveedorRequest r) {
        proveedores.actualizarDatos(id, r.nombre(), r.telefono(), r.correo());
        return obtener(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        return Respuestas.eliminacion(proveedores.eliminar(id));
    }
}

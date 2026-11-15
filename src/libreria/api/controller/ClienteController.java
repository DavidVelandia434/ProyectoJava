package libreria.api.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import libreria.api.Respuestas;
import libreria.api.dto.ClienteRequest;
import libreria.model.Cliente;
import libreria.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService clientes;

    public ClienteController(ClienteService clientes) {
        this.clientes = clientes;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clientes.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable int id) {
        return clientes.listar().stream().filter(c -> c.getId() == id).findFirst()
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@Valid @RequestBody ClienteRequest r) {
        Cliente c = clientes.registrar(r.nombre(), r.documento(), r.telefono(), r.correo());
        return ResponseEntity.status(HttpStatus.CREATED).body(c);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable int id, @Valid @RequestBody ClienteRequest r) {
        clientes.actualizarContacto(id, r.nombre(), r.telefono(), r.correo());
        return obtener(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        return Respuestas.eliminacion(clientes.eliminar(id));
    }
}

package libreria.api.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import libreria.api.dto.VentaRequest;
import libreria.model.Venta;
import libreria.service.VentaService;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    private final VentaService ventas;

    public VentaController(VentaService ventas) {
        this.ventas = ventas;
    }

    @GetMapping
    public List<Venta> listar(@RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        if (clienteId != null) return ventas.historialPorCliente(clienteId);
        if (desde != null && hasta != null) return ventas.historialPorFechas(desde, hasta);
        return ventas.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtener(@PathVariable int id) {
        return ventas.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Venta> registrar(@Valid @RequestBody VentaRequest r) {
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (VentaRequest.ItemVenta item : r.items()) {
            cantidades.merge(item.idLibro(), item.cantidad(), Integer::sum);
        }
        Venta venta = ventas.registrarVenta(r.idCliente(), cantidades);
        return ResponseEntity.status(HttpStatus.CREATED).body(venta);
    }
}

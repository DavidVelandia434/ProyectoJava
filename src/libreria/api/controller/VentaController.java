/*
 * Programa : Sistema de gestión de librería
 * Archivo  : VentaController.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import libreria.api.dto.VentaRequest;
import libreria.model.Venta;
import libreria.service.VentaService;

/** Las ventas confirmadas solo se crean y se consultan: no hay PUT ni DELETE (integridad histórica). */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    private final VentaService ventas;

    public VentaController(VentaService ventas) {
        this.ventas = ventas;
    }

    /**
     * GET /api/ventas                                   -> todas
     * GET /api/ventas?clienteId=1                       -> historial por cliente
     * GET /api/ventas?desde=2026-09-01&hasta=2026-09-30 -> historial por fechas
     */
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

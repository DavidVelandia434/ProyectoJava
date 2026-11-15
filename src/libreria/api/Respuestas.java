package libreria.api;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import libreria.service.ResultadoEliminacion;

public final class Respuestas {
    private Respuestas() { }

    public static ResponseEntity<?> eliminacion(ResultadoEliminacion r) {
        switch (r) {
            case ELIMINADO:
                return ResponseEntity.noContent().build();
            case NO_ENCONTRADO:
                return ResponseEntity.status(404).body(Map.of("error", r.getMensaje()));
            default:
                return ResponseEntity.status(409).body(Map.of("error", r.getMensaje()));
        }
    }
}

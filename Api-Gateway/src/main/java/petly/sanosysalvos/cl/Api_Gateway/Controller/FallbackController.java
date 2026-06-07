package petly.sanosysalvos.cl.Api_Gateway.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/usuarios")
    public ResponseEntity<Map<String, String>> usuariosFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "servicio", "usuarios-service",
                        "mensaje", "El servicio de usuarios no está disponible temporalmente"
                ));
    }

    @RequestMapping("/fallback/mascotas")
    public ResponseEntity<Map<String, String>> mascotasFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "servicio", "mascotas-service",
                        "mensaje", "El servicio de mascotas no está disponible temporalmente"
                ));
    }

    @RequestMapping("/fallback/notificaciones")
    public ResponseEntity<Map<String, String>> notificacionesFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "servicio", "notificaciones-service",
                        "mensaje", "El servicio de notificaciones no está disponible temporalmente"
                ));
    }

    @RequestMapping("/fallback/coincidencias")
    public ResponseEntity<Map<String, String>> coincidenciasFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "servicio", "coincidencias-service",
                        "mensaje", "El servicio de coincidencias no está disponible temporalmente"
                ));
    }
    @RequestMapping("/fallback/reportes")
    public ResponseEntity<Map<String, String>> reportesFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "servicio", "reportes-service",
                        "mensaje", "El servicio de reportes no está disponible temporalmente"
            ));
}
}
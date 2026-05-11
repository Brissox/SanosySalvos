package petly.sanosysalvos.cl.Api_Gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    
    @GetMapping("/fallback/usuarios")
    public ResponseEntity<String> usuariosFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Usuarios no disponible temporalmente");
    }

    @GetMapping("/fallback/reportes")
    public ResponseEntity<String> reportesFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Reportes no disponible temporalmente");
    }

    @GetMapping("/fallback/geolocalizacion")
    public ResponseEntity<String> geolocalizacionFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Geolocalización no disponible temporalmente");
    }

    @GetMapping("/fallback/mascotas")
    public ResponseEntity<String> mascotasFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Mascotas no disponible temporalmente");
    }

    @GetMapping("/fallback/coincidencias")
    public ResponseEntity<String> coincidenciasFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Coincidencias no disponible temporalmente");
    }

    @GetMapping("/fallback/notificaciones")
    public ResponseEntity<String> notificacionesFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Notificaciones no disponible temporalmente");
    }
    
}

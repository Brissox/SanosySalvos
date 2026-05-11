package petly.sanosysalvos.cl.reportes.Client;

import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import petly.sanosysalvos.cl.reportes.DTO.GeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.GeoRequest;
import petly.sanosysalvos.cl.reportes.DTO.GeoResponse;

@Service
public class GeoClientResiliente {
    private final GeoClient geoClient;

    public GeoClientResiliente(GeoClient geoClient) {
        this.geoClient = geoClient;
    }

    @CircuitBreaker(name = "geoService", fallbackMethod = "fallbackCrear")
    @Retry(name = "geoService")
    @Bulkhead(name = "geoService")
    public GeoResponse crear(GeoRequest request) {
        return geoClient.crear(request);
    }

    @CircuitBreaker(name = "geoService", fallbackMethod = "fallbackObtener")
    @Retry(name = "geoService")
    @Bulkhead(name = "geoService")
    public GeoDTO obtener(Long id) {
        return geoClient.obtener(id);
    }

    @CircuitBreaker(name = "geoService", fallbackMethod = "fallbackEliminar")
    @Retry(name = "geoService")
    @Bulkhead(name = "geoService")
    public void eliminarDTO(Long id) {
        geoClient.eliminarDTO(id);
    }

    private GeoResponse fallbackCrear(GeoRequest request, Throwable ex) {
        throw new RuntimeException("El servicio de geolocalización no está disponible. No se pudo crear la ubicación.");
    }

    private GeoDTO fallbackObtener(Long id, Throwable ex) {
    System.out.println("Fallback obtener geo activado: " + ex.getMessage());
    throw new RuntimeException("El servicio de geolocalización no está disponible. No se pudo obtener la ubicación.");
    }

    private void fallbackEliminar(Long id, Throwable ex) {
        throw new RuntimeException("El servicio de geolocalización no está disponible. No se pudo eliminar la ubicación.");
    }
}

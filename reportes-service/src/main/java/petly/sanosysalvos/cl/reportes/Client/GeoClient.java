package petly.sanosysalvos.cl.reportes.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    //name = "geo-service", 
    //url = "http://localhost:8082" 
    // URL del microservicio de geolocalización
)
public interface GeoClient {

    @PostMapping("/locations")
    GeoResponse crear(@RequestBody GeoRequest request);
}
package petly.sanosysalvos.cl.reportes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import petly.sanosysalvos.cl.reportes.Client.GeoClient;
import petly.sanosysalvos.cl.reportes.Messaging.ReporteEventoPublisher;
import petly.sanosysalvos.cl.reportes.Repository.ReporteRepository;
import petly.sanosysalvos.cl.reportes.Services.OracleStorageService;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
    "JWT_SECRET=test-secret",
    "RABBITMQ_HOST=localhost",
    "RABBITMQ_PORT=5672",
    "RABBITMQ_USERNAME=guest",
    "RABBITMQ_PASSWORD=guest"
})
class ReportesServiceApplicationTests {
    
    @MockBean
    private ReporteRepository reporteRepository;

    @MockBean
    private GeoClient geoClient;

    @MockBean
    private OracleStorageService oracleStorageService;

    @MockBean
    private ReporteEventoPublisher reporteEventoPublisher;

    @Test
    void contextLoads() {
    }

}
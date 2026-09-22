package petly.sanosysalvos.cl.notificaciones;

import org.checkerframework.checker.units.qual.mol;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import petly.sanosysalvos.cl.notificaciones.Config.JwtUtil;
import petly.sanosysalvos.cl.notificaciones.Repository.CoincidenciaRepository;
import petly.sanosysalvos.cl.notificaciones.Repository.NotificacionRepository;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.DataSourceInitializationAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
    "spring.sql.init.mode=never",
    "JWT_SECRET=test-secret",
	"RABBITMQ_HOST=localhost",
    "RABBITMQ_PORT=5672",
    "RABBITMQ_USERNAME=guest",
    "RABBITMQ_PASSWORD=guest"	 
})
class NotificacionesServiceApplicationTests {
	@MockitoBean
	private NotificacionRepository notificacionRepository;

	@MockitoBean
	private CoincidenciaRepository coincidenciaRepository;

	@MockitoBean
    private JwtUtil jwtUtil;

	@Test
	void contextLoads() {
	}

}

package petly.sanosysalvos.cl.geolocalizacion;

import org.checkerframework.checker.units.qual.mol;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import petly.sanosysalvos.cl.geolocalizacion.Services.GeoServices;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.DataSourceInitializationAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
    "spring.sql.init.mode=never",
    "JWT_SECRET=test-secret" })
class GeolocalizacionServiceApplicationTests {

	@MockitoBean
	private GeoServices geoService;

	@Test
	void contextLoads() {
	}

}

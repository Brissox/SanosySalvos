package petly.sanosysalvos.cl.usuarios;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import petly.sanosysalvos.cl.usuarios.Repository.usuarioRepository;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.DataSourceInitializationAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
    "spring.sql.init.mode=never",
    "JWT_SECRET=test-secret" })
class UsuariosServiceApplicationTests {

	@MockitoBean
	private usuarioRepository usuarioRepository;

	@Test
	void contextLoads() {
	}

}

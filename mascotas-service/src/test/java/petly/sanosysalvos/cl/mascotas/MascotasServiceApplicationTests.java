package petly.sanosysalvos.cl.mascotas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import petly.sanosysalvos.cl.mascotas.Repository.MascotaRepository;
import petly.sanosysalvos.cl.mascotas.Services.OracleStorageService;


@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.jdbc.autoconfigure.DataSourceInitializationAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
        "spring.sql.init.mode=never",
        "JWT_SECRET=test-secret" })
class MascotasServiceApplicationTests {
    
    @MockitoBean
    private OracleStorageService oracleStorageService;

    @MockitoBean
    private MascotaRepository mascotaRepository;
    
	@Test
	void contextLoads() {
	}

}

package petly.sanosysalvos.cl.Api_Gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
     webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.main.web-application-type=servlet",
                "server.port=0",

                "spring.cloud.discovery.enabled=false",
                "spring.cloud.gateway.discovery.locator.enabled=false",
                "eureka.client.enabled=false",

                "jwt.secret=01234567890123456789012345678901"
        }
)
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}

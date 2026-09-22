package petly.sanosysalvos.cl.mascotas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import petly.sanosysalvos.cl.mascotas.Config.JwtUtil;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "12345678901234567890123456789012";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

    @Test
    void extractRun_TokenValido() {
        String token = generarToken(12345678);

        Integer resultado = jwtUtil.extractRun(token);

        assertThat(resultado).isEqualTo(12345678);
    }

    @Test
    void extractRun_TokenInvalido() {
        assertThatThrownBy(() -> jwtUtil.extractRun("token-invalido"))
                .isInstanceOf(Exception.class);
    }

    private String generarToken(Integer run) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claim("run", run)
                .setSubject("usuario-test")
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();
    }
}
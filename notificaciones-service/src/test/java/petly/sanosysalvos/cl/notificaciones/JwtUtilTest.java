package petly.sanosysalvos.cl.notificaciones;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import petly.sanosysalvos.cl.notificaciones.Config.JwtUtil;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "12345678901234567890123456789012";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

    @Test
    void extractUserId_tokenValido() {
        String token = Jwts.builder()
                .claim("run", 12345678L)
                .setSubject("usuario@test.com")
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        Long run = jwtUtil.extractUserId(token);

        assertThat(run).isEqualTo(12345678L);
    }
}
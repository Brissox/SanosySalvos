package petly.sanosysalvos.cl.reportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import petly.sanosysalvos.cl.reportes.Config.JwtUtil;
import io.jsonwebtoken.JwtException;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "01234567890123456789012345678901";

     @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

     @Test
    void extractRun_tokenValido() {
        var key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .claim("run", 123)
                .signWith(key)
                .compact();

        Integer run = jwtUtil.extractRun(token);

        assertEquals(123, run);
    }

    @Test
    void extractRun_tokenInvalido() {
        assertThrows(JwtException.class, () -> jwtUtil.extractRun("bad.token.value"));
    }
}
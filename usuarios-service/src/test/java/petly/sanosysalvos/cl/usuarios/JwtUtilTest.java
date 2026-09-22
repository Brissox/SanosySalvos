package petly.sanosysalvos.cl.usuarios;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import petly.sanosysalvos.cl.usuarios.Config.JwtUtil;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    void generarToken_extraerCorreo() {
        String token = jwtUtil.generarToken("test@test.cl", 12345678);

        String correo = jwtUtil.extraerCorreo(token);

        assertThat(correo).isEqualTo("test@test.cl");
        assertThat(jwtUtil.validarToken(token)).isTrue();
    }

    @Test
    void validarToken_tokenInvalido() {
        assertThat(jwtUtil.validarToken("token-invalido")).isFalse();
    }
}
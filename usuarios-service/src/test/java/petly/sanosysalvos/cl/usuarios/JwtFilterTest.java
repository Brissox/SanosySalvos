package petly.sanosysalvos.cl.usuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import petly.sanosysalvos.cl.usuarios.Config.JwtFilter;
import petly.sanosysalvos.cl.usuarios.Config.JwtUtil;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_options() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/petly/usuarios");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(jwtUtil);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_loginPublico() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/petly/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(jwtUtil);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_sinAutorizacion() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/petly/usuarios");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(jwtUtil);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_tokenValido() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/petly/usuarios");
        request.addHeader("Authorization", "Bearer token-valido");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtUtil.validarToken("token-valido")).thenReturn(true);
        when(jwtUtil.extraerCorreo("token-valido")).thenReturn("test@test.cl");

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("test@test.cl");

        verify(jwtUtil).validarToken("token-valido");
        verify(jwtUtil).extraerCorreo("token-valido");
    }

    @Test
    void doFilterInternal_tokenInvalido() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/petly/usuarios");
        request.addHeader("Authorization", "Bearer token-malo");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtUtil.validarToken("token-malo")).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtUtil).validarToken("token-malo");
        verify(jwtUtil, never()).extraerCorreo(anyString());
    }
}
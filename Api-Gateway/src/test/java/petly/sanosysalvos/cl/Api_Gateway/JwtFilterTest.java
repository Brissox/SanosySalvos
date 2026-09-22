package petly.sanosysalvos.cl.Api_Gateway;


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

import petly.sanosysalvos.cl.Api_Gateway.Config.JwtFilter;
import petly.sanosysalvos.cl.Api_Gateway.Config.JwtUtil;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_options() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/petly/usuarios");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(jwtUtil);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_loginPublico() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/petly/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(jwtUtil);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_registroPublico() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/petly/usuarios/registrar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(jwtUtil);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void doFilter_getReportes() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/petly/reportes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(jwtUtil);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void doFilter_sinAuthorizacion() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/petly/usuarios");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void doFilter_tokenInvalido() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/petly/usuarios");
        request.addHeader("Authorization", "Bearer token-malo");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtUtil.validarToken("token-malo")).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtUtil).validarToken("token-malo");
        verify(jwtUtil, never()).extraerCorreo(anyString());
    }

    @Test
    void doFilter_tokenValido() throws Exception {
        JwtFilter filter = new JwtFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/petly/usuarios");
        request.addHeader("Authorization", "Bearer token-valido");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtUtil.validarToken("token-valido")).thenReturn(true);
        when(jwtUtil.extraerCorreo("token-valido")).thenReturn("usuario@test.cl");

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("usuario@test.cl");

        verify(jwtUtil).validarToken("token-valido");
        verify(jwtUtil).extraerCorreo("token-valido");
    }
}
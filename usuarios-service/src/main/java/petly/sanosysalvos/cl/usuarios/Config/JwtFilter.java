package petly.sanosysalvos.cl.usuarios.Config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter { // Se ejecuta una vez por cada solicitud entrante

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) { // Volvemos a usar final para que no se pueda cambiar la referencia del objeto
                                        // jwtUtil después de la inyección de dependencias
        this.jwtUtil = jwtUtil; // Se considera buena práctica
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, // Este es el metodo que se ejecuta para cada solicitud
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        String path = request.getRequestURI();

        //Ignora preflight CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        //Ignora rutas públicas
        if (path.equals("/petly/auth/login") || path.equals("/petly/usuarios/registrar")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtiene el encabezado de autorización de la solicitud
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) { // Verifica que el encabezado no sea nulo y que
                                                                      // comience con "Bearer "
            String token = authHeader.substring(7);

            if (jwtUtil.validarToken(token)) { // Valida el token utilizando el método validarToken del JwtUtil.
                String correo = jwtUtil.extraerCorreo(token);

                // Registrar usuario autenticado en el contexto de Spring Security
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(correo, null,
                        List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
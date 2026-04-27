package petly.sanosysalvos.cl.usuarios.Config;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(String correo) {
        return Jwts.builder()
                .setSubject(correo) //Email de la persona
                .setIssuedAt(new Date()) //Cuando se creó?
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) //Expira en 1 hora
                .signWith(getKey(), SignatureAlgorithm.HS256) //La clave secreta designada firma el token 
                .compact(); //Genera el token en formato String (JWT)
    }

    public String extraerCorreo(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) //Verifica la firma del token con la clave secreta antes de extraer los datos
                .build() 
                .parseClaimsJws(token) //Analiza el token, verifica su validez y extrae las reclamaciones (claims) si es válido
                .getBody() //Saca los datos del token
                .getSubject(); //Extrae el subject del token, que en este caso es el correo electrónico del usuario.
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true; //Token válido
        } catch (JwtException | IllegalArgumentException e) {
            return false; //Token inválido puede ser por expiración
        }
    }
}
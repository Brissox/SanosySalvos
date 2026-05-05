package petly.sanosysalvos.cl.usuarios.Repository;

import java.util.List;
import java.util.Optional;

import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import petly.sanosysalvos.cl.usuarios.Model.Usuario;

public interface usuarioRepository extends JpaRepository<Usuario, Integer>{

    // Método personalizado para buscar un usuario por correo
    Optional<Usuario> findByCorreo(String correo);

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombreRol = :nombreRol")
    List<Usuario> findByRolNombreRol(@Param("nombreRol") String nombreRol);


}

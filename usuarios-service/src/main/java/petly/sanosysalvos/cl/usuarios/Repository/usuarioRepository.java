package petly.sanosysalvos.cl.usuarios.Repository;

import java.util.Optional;

import  org.springframework.data.jpa.repository.JpaRepository;

import petly.sanosysalvos.cl.usuarios.Model.Usuario;

public interface usuarioRepository extends JpaRepository<Usuario, Long>{

    // Método personalizado para buscar un usuario por correo
    Optional<Usuario> findByCorreo(String correo);


}
package petly.sanosysalvos.cl.usuarios.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import  petly.sanosysalvos.cl.usuarios.Model.Usuario;

public interface usuarioRepository extends JpaRepository<Usuario, Long>{


}
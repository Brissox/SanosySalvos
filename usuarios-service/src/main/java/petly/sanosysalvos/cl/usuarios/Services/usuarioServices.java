package petly.sanosysalvos.cl.usuarios.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.usuarios.Model.Usuario;
import petly.sanosysalvos.cl.usuarios.Repository.usuarioRepository;

@Service
@Transactional

public class usuarioServices {
     @Autowired

    private usuarioRepository usuariosrepository;

    public List<Usuario> BuscarTodoUsuario(){
        return usuariosrepository.findAll();
    }

    public Usuario BuscarUnUsuario(Long ID_USUARIO){
        return usuariosrepository.findById(ID_USUARIO).get();

    }

    public Usuario GuardarUsuario(Usuario usuario){
        return usuariosrepository.save(usuario);

    }

    public void EliminarUsuario(Long ID_USUARIO){
        usuariosrepository.deleteById(ID_USUARIO);
    }
}

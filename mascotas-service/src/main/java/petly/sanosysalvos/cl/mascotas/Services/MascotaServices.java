package petly.sanosysalvos.cl.mascotas.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.mascotas.Model;
import petly.sanosysalvos.cl.mascotas.Repository;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Repository.MascotaRepository;

@Service
@Transactional

public class MascotaServices {

    @Autowired

    private MascotaRepository mascotaRepository;

    public List<Mascota> BuscarTodoMascotas(){
        return mascotaRepository.findAll();
    }

    public Mascota BuscarUnaMascota(Long id_mascota){
        return mascotaRepository.findById(id_mascota).get();

    }

    public Mascota GuardarMascota(Mascota mascota){
        return mascotaRepository.save(mascota);

    }

    public void EliminarMascota(Long id_mascota){
        mascotaRepository.deleteById(id_mascota);
    }

}

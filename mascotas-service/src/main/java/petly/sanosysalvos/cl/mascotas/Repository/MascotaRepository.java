package petly.sanosysalvos.cl.mascotas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petly.sanosysalvos.cl.mascotas.Model.Mascota;


public interface MascotaRepository extends JpaRepository<Mascota, String>{

    List<Mascota> findByRunUsuario(Integer runUsuario);


}
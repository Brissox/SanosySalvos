package petly.sanosysalvos.cl.mascotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import  petly.sanosysalvos.cl.usuarios.Model.Mascota;

public interface MascotaRepository extends JpaRepository<Mascota, Long>{


}
package petly.sanosysalvos.cl.mascotas.Services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.mascotas.DTO.MascotaRequest;
import petly.sanosysalvos.cl.mascotas.Factory.MascotaFactory;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Repository.MascotaRepository;

@Service
@Transactional

public class MascotaServices {

    @Autowired
    private OracleStorageService oracleStorageService;
    private MascotaRepository mascotaRepository;

    public List<Mascota> BuscarTodoMascotas() {
        return mascotaRepository.findAll();
    }

    public Mascota BuscarUnaMascota(Long id_mascota) {
        return mascotaRepository.findById(id_mascota).get();

    }

    public Mascota GuardarMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);

    }

    public void EliminarMascota(Long id_mascota) {
        mascotaRepository.deleteById(id_mascota);
    }

    public Mascota crearMascota(MascotaRequest request, MultipartFile imagen) {

        String urlImagen;

        // Crear mascota base con factory
        Mascota mascota = MascotaFactory.crear(request);

        // Validar imagen
        if (imagen != null && !imagen.isEmpty()) {
            try {
                // Subir imagen a Oracle y obtener URL
                urlImagen = oracleStorageService.subirImagen(imagen);
            } catch (IOException e) {
                throw new RuntimeException("Error subiendo imagen a Oracle", e);
        }
        } else {
            urlImagen = null; // o una URL por defecto
        }
        
        // Guardar URL en la mascota
        mascota.setImagenUrl(urlImagen);

        // Guardar en BD la totalidad de la mascota
        return mascotaRepository.save(mascota);
    }

    public Mascota actualizarMascota(Long id, MascotaRequest request, MultipartFile imagen) {

        Mascota mascota = BuscarUnaMascota(id);

        mascota.setNombre(request.getNombre());
        mascota.setNumero_microchip(request.getNumeroMicrochip());
        mascota.setEspecie(request.getEspecie());
        mascota.setSexo(request.getSexo());
        mascota.setRaza(request.getRaza());
        mascota.setColor(request.getColor());
        mascota.setFecha_nacimiento(request.getFechaNacimiento());
        mascota.setEstado_reproductivo(request.getEstadoReproductivo());
        mascota.setUbicacion(request.getUbicacion());
        mascota.setTipo(request.getTipo());

        // Imagen opcional
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String urlImagen = oracleStorageService.subirImagen(imagen);
                mascota.setImagenUrl(urlImagen);
            } catch (IOException e) {
                throw new RuntimeException("Error subiendo imagen", e);
            }
        }

        return mascotaRepository.save(mascota);
    }

}

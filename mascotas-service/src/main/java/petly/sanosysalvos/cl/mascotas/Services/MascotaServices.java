package petly.sanosysalvos.cl.mascotas.Services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.mascotas.DTO.MascotaRequest;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Repository.MascotaRepository;

@Service
@Transactional

public class MascotaServices {

    @Autowired
    private OracleStorageService oracleStorageService;
    
    @Autowired
    private MascotaRepository mascotaRepository;

    public List<Mascota> BuscarTodoMascotas() {
        return mascotaRepository.findAll();
    }

    public Mascota BuscarUnaMascota(String chip){
        return mascotaRepository.findById(chip).get();
    }

    public Mascota GuardarMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    public void EliminarMascota(String chip){
        mascotaRepository.deleteById(chip);
    }

    public Mascota crearMascota(MascotaRequest request, MultipartFile imagen) {

        String urlImagen = null;

        // Crear mascota desde el request
        Mascota mascota = new Mascota();
        mascota.setChip(request.getChip());
        mascota.setNombre(request.getNombre());
        mascota.setSexo(request.getSexo());
        mascota.setEdad(request.getEdad());
        mascota.setRaza(request.getRaza());
        mascota.setColor(request.getColor());
        mascota.setDescripcion(request.getDescripcion());

        // Validar imagen
        if (imagen != null && !imagen.isEmpty()) {
            try {
                // Subir imagen a Oracle y obtener URL
                urlImagen = oracleStorageService.subirImagen(imagen);
            } catch (IOException e) {
                throw new RuntimeException("Error subiendo imagen a Oracle", e);
            }
        }
        
        // Guardar URL en la mascota
        mascota.setImagenUrl(urlImagen);

        // Guardar en BD la totalidad de la mascota
        return mascotaRepository.save(mascota);
    }

    public Mascota registrar(MascotaRequest request, MultipartFile foto, Long userId) {
        String urlImagen = null;

        // Crear mascota desde el request
        Mascota mascota = new Mascota();
        mascota.setChip(request.getChip());
        mascota.setNombre(request.getNombre());
        mascota.setSexo(request.getSexo());
        mascota.setEdad(request.getEdad());
        mascota.setRaza(request.getRaza());
        mascota.setColor(request.getColor());
        mascota.setDescripcion(request.getDescripcion());

        // Validar imagen
        if (foto != null && !foto.isEmpty()) {
            try {
                urlImagen = oracleStorageService.subirImagen(foto);
            } catch (IOException e) {
                throw new RuntimeException("Error subiendo imagen a Oracle", e);
            }
        }

        mascota.setImagenUrl(urlImagen);
        return mascotaRepository.save(mascota);
    }

    public Mascota actualizarMascota(Long id, MascotaRequest request, MultipartFile imagen) {

        Mascota mascota = mascotaRepository.findById(String.valueOf(id))
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        mascota.setNombre(request.getNombre());
        mascota.setSexo(request.getSexo());
        mascota.setRaza(request.getRaza());
        mascota.setColor(request.getColor());
        mascota.setEdad(request.getEdad());
        mascota.setDescripcion(request.getDescripcion());

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


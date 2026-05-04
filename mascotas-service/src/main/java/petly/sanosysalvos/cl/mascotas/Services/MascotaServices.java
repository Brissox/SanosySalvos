package petly.sanosysalvos.cl.mascotas.Services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import petly.sanosysalvos.cl.mascotas.DTO.MascotaRequest;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Model.TipoMascota;
import petly.sanosysalvos.cl.mascotas.Repository.MascotaRepository;

@Service
@Transactional

public class MascotaServices {

    @Autowired
    private OracleStorageService oracleStorageService;

    @Autowired
    private MascotaRepository mascotaRepository;

    public List<Mascota> buscarTodoMascotas() {
        return mascotaRepository.findAll();
    }

    public Mascota buscarUnaMascota(String chip) {
        return mascotaRepository.findById(chip).get();
    }

    public List<Mascota> buscarMascotasPorRun(Integer run) {
        return mascotaRepository.findByRunUsuario(run);
    }

    public Mascota guardarMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    public void eliminarMascota(String chip) {
        mascotaRepository.deleteById(chip);
    }

    public Mascota crearMascota(MascotaRequest request, MultipartFile imagen, Integer run) {

        Mascota mascota = new Mascota();

        mascota.setChip(request.getChip());
        mascota.setNombre(request.getNombre());
        mascota.setSexo(request.getSexo());
        mascota.setEdad(request.getEdad());
        mascota.setRaza(request.getRaza());
        mascota.setColor(request.getColor());
        mascota.setDescripcion(request.getDescripcion());
        mascota.setTipo(TipoMascota.valueOf(request.getTipo()));
        mascota.setOtroTipo(request.getOtroTipo());
        mascota.setRunUsuario(run);

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

    public Mascota actualizarMascota(String chip, MascotaRequest request, MultipartFile imagen) {

        Mascota mascota = mascotaRepository.findById(chip)
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
                // Guardar URL anterior
                String urlAnterior = mascota.getImagenUrl();

                // Subir nueva imagen
                String nuevaUrl = oracleStorageService.subirImagen(imagen);

                // Actualizar entidad
                mascota.setImagenUrl(nuevaUrl);

                // Eliminar imagen antigua (si existe)
                if (urlAnterior != null) {
                    oracleStorageService.eliminarImagen(urlAnterior);
                }

            } catch (IOException e) {
                throw new RuntimeException("Error subiendo imagen", e);
            }
        }

        return mascotaRepository.save(mascota);
    }

}

package petly.sanosysalvos.cl.mascotas.Factory;

import petly.sanosysalvos.cl.mascotas.DTO.MascotaRequest;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;

public class MascotaFactory {

     public static Mascota crear(MascotaRequest req) {


        if (req.getTipo() == null) {
        throw new RuntimeException("El tipo de mascota es obligatorio");
    }

        Mascota m = new Mascota();

        // datos base
        m.setNombre(req.getNombre());
        m.setNumero_microchip(req.getNumeroMicrochip());
        m.setEspecie(req.getEspecie());
        m.setSexo(req.getSexo());
        m.setRaza(req.getRaza());
        m.setColor(req.getColor());
        m.setFecha_nacimiento(req.getFechaNacimiento());
        m.setEstado_reproductivo(req.getEstadoReproductivo());

        m.setTipo(req.getTipo());

        // 🔥 lógica por tipo
        switch (req.getTipo()) {

            case CON_DUENO:
                // no necesita ubicación
                break;

            case PERDIDA:
                if (req.getUbicacion() == null) {
                    throw new RuntimeException("Debe indicar dónde se perdió");
                }
                m.setUbicacion(req.getUbicacion());
                break;

            case ENCONTRADA:
                if (req.getUbicacion() == null) {
                    throw new RuntimeException("Debe indicar dónde se encontró");
                }
                m.setUbicacion(req.getUbicacion());
                break;
        }

        return m;
    }
}

package petly.sanosysalvos.cl.mascotas;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import petly.sanosysalvos.cl.mascotas.DTO.CrearMascotaRequest;
import petly.sanosysalvos.cl.mascotas.Model.Mascota;
import petly.sanosysalvos.cl.mascotas.Model.TipoMascota;
import petly.sanosysalvos.cl.mascotas.Repository.MascotaRepository;
import petly.sanosysalvos.cl.mascotas.Services.MascotaServices;
import petly.sanosysalvos.cl.mascotas.Services.OracleStorageService;


@ExtendWith(MockitoExtension.class)
public class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private OracleStorageService oracleStorageService;

    @InjectMocks
    private MascotaServices mascotaService;

    @Test
    void buscarTodoMascotas() {
        Mascota mascota = new Mascota();
        mascota.setChip("123ABC");
        mascota.setNombre("Firulais");

        when(mascotaRepository.findAll()).thenReturn(List.of(mascota));

        List<Mascota> resultado = mascotaService.buscarTodoMascotas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getChip()).isEqualTo("123ABC");
        assertThat(resultado.get(0).getNombre()).isEqualTo("Firulais");
    }
    
    @Test
    void buscarUnaMascota_existente() {
        Mascota mascota = new Mascota();
        mascota.setChip("999XYZ");
        mascota.setNombre("Luna");

        when(mascotaRepository.findById("999XYZ")).thenReturn(Optional.of(mascota));

        Mascota resultado = mascotaService.buscarUnaMascota("999XYZ");

        assertThat(resultado.getChip()).isEqualTo("999XYZ");
        assertThat(resultado.getNombre()).isEqualTo("Luna");
    }

    @Test
    void buscarUnaMascota_noExistente() {
        when(mascotaRepository.findById("NOEXISTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.buscarUnaMascota("NOEXISTE"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void buscarMascotasPorRun() {
        Mascota mascota = new Mascota();
        mascota.setChip("111AAA");
        mascota.setNombre("Max");
        mascota.setRunUsuario(12345678);

        when(mascotaRepository.findByRunUsuario(12345678)).thenReturn(List.of(mascota));

        List<Mascota> resultado = mascotaService.buscarMascotasPorRun(12345678);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRunUsuario()).isEqualTo(12345678);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Max");
    }

    @Test
    void guardarMascota() {
        Mascota mascota = new Mascota();
        mascota.setChip("222BBB");
        mascota.setNombre("Michi");

        when(mascotaRepository.save(mascota)).thenReturn(mascota);

        Mascota resultado = mascotaService.guardarMascota(mascota);

        assertThat(resultado.getChip()).isEqualTo("222BBB");
        assertThat(resultado.getNombre()).isEqualTo("Michi");
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void eliminarMascota_eliminaPorChip() {
        mascotaService.eliminarMascota("333CCC");

        verify(mascotaRepository).deleteById("333CCC");
    }

    @Test
    void crearMascota_conImagen() throws IOException {
        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setChip("444DDD");
        request.setNombre("Rocky");
        request.setSexo("MACHO");
        request.setEdad(5);
        request.setRaza("Labrador");
        request.setColor("Café");
        request.setDescripcion("Perro tranquilo");
        request.setTipo("PERRO");
        request.setOtroTipo(null);

        MockMultipartFile imagen = new MockMultipartFile(
                "imagen",
                "foto.jpg",
                "image/jpeg",
                "contenido".getBytes()
        );

        when(oracleStorageService.subirImagen(any(MultipartFile.class)))
                .thenReturn("http://storage/mascota.jpg");

        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota mascota = invocation.getArgument(0);
            return mascota;
        });

        Mascota resultado = mascotaService.crearMascota(request, imagen, 12345678);

        assertThat(resultado.getChip()).isEqualTo("444DDD");
        assertThat(resultado.getNombre()).isEqualTo("Rocky");
        assertThat(resultado.getSexo()).isEqualTo("MACHO");
        assertThat(resultado.getEdad()).isEqualTo(5);
        assertThat(resultado.getRaza()).isEqualTo("Labrador");
        assertThat(resultado.getColor()).isEqualTo("Café");
        assertThat(resultado.getDescripcion()).isEqualTo("Perro tranquilo");
        assertThat(resultado.getTipo()).isEqualTo(TipoMascota.PERRO);
        assertThat(resultado.getRunUsuario()).isEqualTo(12345678);
        assertThat(resultado.getImagenUrl()).isEqualTo("http://storage/mascota.jpg");

        verify(oracleStorageService).subirImagen(any(MultipartFile.class));
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    void crearMascota_sinImagen() throws IOException {
        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setChip("555EEE");
        request.setNombre("Nala");
        request.setSexo("HEMBRA");
        request.setEdad(2);
        request.setRaza("Mestiza");
        request.setColor("Negro");
        request.setDescripcion("Gatita pequeña");
        request.setTipo("GATO");
        request.setOtroTipo(null);

        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota mascota = invocation.getArgument(0);
            return mascota;
        });

        Mascota resultado = mascotaService.crearMascota(request, null, 87654321);

        assertThat(resultado.getChip()).isEqualTo("555EEE");
        assertThat(resultado.getNombre()).isEqualTo("Nala");
        assertThat(resultado.getTipo()).isEqualTo(TipoMascota.GATO);
        assertThat(resultado.getRunUsuario()).isEqualTo(87654321);
        assertThat(resultado.getImagenUrl()).isNull();

        verify(oracleStorageService, never()).subirImagen(any(MultipartFile.class));
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    void crearMascota_errorSubiendoImagen() throws IOException {
        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setChip("666FFF");
        request.setNombre("Toby");
        request.setSexo("MACHO");
        request.setEdad(3);
        request.setRaza("Poodle");
        request.setColor("Blanco");
        request.setDescripcion("Perro pequeño");
        request.setTipo("PERRO");

        MockMultipartFile imagen = new MockMultipartFile(
                "imagen",
                "foto.jpg",
                "image/jpeg",
                "contenido".getBytes()
        );

        when(oracleStorageService.subirImagen(any(MultipartFile.class)))
                .thenThrow(new IOException("Error OCI"));

        assertThatThrownBy(() -> mascotaService.crearMascota(request, imagen, 11111111))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error subiendo imagen");

        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void crearMascota_tipoInvalido() {
        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setChip("777GGG");
        request.setNombre("Animal");
        request.setSexo("MACHO");
        request.setEdad(1);
        request.setRaza("Desconocida");
        request.setColor("Gris");
        request.setDescripcion("Tipo incorrecto");
        request.setTipo("CABALLO");

        assertThatThrownBy(() -> mascotaService.crearMascota(request, null, 22222222))
                .isInstanceOf(IllegalArgumentException.class);

        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void actualizarMascota_sinImagen() throws IOException {
        Mascota mascotaExistente = new Mascota();
        mascotaExistente.setChip("888HHH");
        mascotaExistente.setNombre("Nombre antiguo");
        mascotaExistente.setImagenUrl("http://storage/antigua.jpg");

        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setNombre("Nombre nuevo");
        request.setSexo("HEMBRA");
        request.setEdad(4);
        request.setRaza("Siames");
        request.setColor("Blanco");
        request.setDescripcion("Descripción actualizada");

        when(mascotaRepository.findById("888HHH")).thenReturn(Optional.of(mascotaExistente));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota mascota = invocation.getArgument(0);
            return mascota;
        });

        Mascota resultado = mascotaService.actualizarMascota("888HHH", request, null);

        assertThat(resultado.getChip()).isEqualTo("888HHH");
        assertThat(resultado.getNombre()).isEqualTo("Nombre nuevo");
        assertThat(resultado.getSexo()).isEqualTo("HEMBRA");
        assertThat(resultado.getEdad()).isEqualTo(4);
        assertThat(resultado.getRaza()).isEqualTo("Siames");
        assertThat(resultado.getColor()).isEqualTo("Blanco");
        assertThat(resultado.getDescripcion()).isEqualTo("Descripción actualizada");

        assertThat(resultado.getImagenUrl()).isEqualTo("http://storage/antigua.jpg");

        verify(oracleStorageService, never()).subirImagen(any(MultipartFile.class));
        verify(oracleStorageService, never()).eliminarImagen(any(String.class));
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    void actualizarMascota_conNuevaImagen() throws IOException {
        Mascota mascotaExistente = new Mascota();
        mascotaExistente.setChip("999III");
        mascotaExistente.setNombre("Copito");
        mascotaExistente.setImagenUrl("http://storage/imagen-antigua.jpg");

        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setNombre("Copito actualizado");
        request.setSexo("MACHO");
        request.setEdad(6);
        request.setRaza("Persa");
        request.setColor("Blanco");
        request.setDescripcion("Mascota actualizada");

        MockMultipartFile nuevaImagen = new MockMultipartFile(
                "imagen",
                "nueva.jpg",
                "image/jpeg",
                "nueva imagen".getBytes()
        );

        when(mascotaRepository.findById("999III")).thenReturn(Optional.of(mascotaExistente));
        when(oracleStorageService.subirImagen(any(MultipartFile.class)))
                .thenReturn("http://storage/imagen-nueva.jpg");
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota mascota = invocation.getArgument(0);
            return mascota;
        });

        Mascota resultado = mascotaService.actualizarMascota("999III", request, nuevaImagen);

        assertThat(resultado.getNombre()).isEqualTo("Copito actualizado");
        assertThat(resultado.getImagenUrl()).isEqualTo("http://storage/imagen-nueva.jpg");

        verify(oracleStorageService).subirImagen(any(MultipartFile.class));
        verify(oracleStorageService).eliminarImagen("http://storage/imagen-antigua.jpg");
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    void actualizarMascota_sinImagenAnterior() throws IOException {
        Mascota mascotaExistente = new Mascota();
        mascotaExistente.setChip("101JJJ");
        mascotaExistente.setNombre("Pelusa");
        mascotaExistente.setImagenUrl(null);

        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setNombre("Pelusa actualizada");
        request.setSexo("HEMBRA");
        request.setEdad(2);
        request.setRaza("Mestiza");
        request.setColor("Gris");
        request.setDescripcion("Actualizada");

        MockMultipartFile nuevaImagen = new MockMultipartFile(
                "imagen",
                "nueva.jpg",
                "image/jpeg",
                "nueva imagen".getBytes()
        );

        when(mascotaRepository.findById("101JJJ")).thenReturn(Optional.of(mascotaExistente));
        when(oracleStorageService.subirImagen(any(MultipartFile.class)))
                .thenReturn("http://storage/nueva.jpg");
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota mascota = invocation.getArgument(0);
            return mascota;
        });

        Mascota resultado = mascotaService.actualizarMascota("101JJJ", request, nuevaImagen);

        assertThat(resultado.getImagenUrl()).isEqualTo("http://storage/nueva.jpg");

        verify(oracleStorageService).subirImagen(any(MultipartFile.class));
        verify(oracleStorageService, never()).eliminarImagen(any(String.class));
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    void actualizarMascota_noExistente() {
        CrearMascotaRequest request = new CrearMascotaRequest();

        when(mascotaRepository.findById("NOEXISTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.actualizarMascota("NOEXISTE", request, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Mascota no encontrada");

        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void actualizarMascota_errorSubiendoImagen() throws IOException {
        Mascota mascotaExistente = new Mascota();
        mascotaExistente.setChip("202KKK");
        mascotaExistente.setImagenUrl("http://storage/antigua.jpg");

        CrearMascotaRequest request = new CrearMascotaRequest();
        request.setNombre("Mascota");
        request.setSexo("MACHO");
        request.setEdad(3);
        request.setRaza("Mestizo");
        request.setColor("Café");
        request.setDescripcion("Descripción");

        MockMultipartFile imagen = new MockMultipartFile(
                "imagen",
                "foto.jpg",
                "image/jpeg",
                "contenido".getBytes()
        );

        when(mascotaRepository.findById("202KKK")).thenReturn(Optional.of(mascotaExistente));
        when(oracleStorageService.subirImagen(any(MultipartFile.class)))
                .thenThrow(new IOException("Error OCI"));

        assertThatThrownBy(() -> mascotaService.actualizarMascota("202KKK", request, imagen))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error subiendo imagen");

        verify(mascotaRepository, never()).save(any(Mascota.class));
        verify(oracleStorageService, never()).eliminarImagen(any(String.class));
    }

    @Test
    void buscarPorRunUsuario() {
        Mascota mascota = new Mascota();
        mascota.setChip("303LLL");
        mascota.setNombre("Athenea");
        mascota.setRunUsuario(12345678);

        when(mascotaRepository.findByRunUsuario(12345678)).thenReturn(List.of(mascota));

        List<Mascota> resultado = mascotaService.buscarPorRunUsuario(12345678);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Athenea");
        assertThat(resultado.get(0).getRunUsuario()).isEqualTo(12345678);
    }
}



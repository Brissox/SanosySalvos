package petly.sanosysalvos.cl.reportes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import petly.sanosysalvos.cl.reportes.Client.GeoClient;
import petly.sanosysalvos.cl.reportes.DTO.GeoDTO;
import petly.sanosysalvos.cl.reportes.DTO.GeoRequest;
import petly.sanosysalvos.cl.reportes.DTO.GeoResponse;
import petly.sanosysalvos.cl.reportes.DTO.ReporteRequest;
import petly.sanosysalvos.cl.reportes.DTO.ReporteGeoDTO;
import petly.sanosysalvos.cl.reportes.Messaging.ReporteEventoPublisher;
import petly.sanosysalvos.cl.reportes.Model.EstadoReporte;
import petly.sanosysalvos.cl.reportes.Model.Especie;
import petly.sanosysalvos.cl.reportes.Model.Reporte;
import petly.sanosysalvos.cl.reportes.Model.Sexo;
import petly.sanosysalvos.cl.reportes.Model.Tamanio;
import petly.sanosysalvos.cl.reportes.Model.TipoReporte;
import petly.sanosysalvos.cl.reportes.Repository.ReporteRepository;
import petly.sanosysalvos.cl.reportes.Services.OracleStorageService;
import petly.sanosysalvos.cl.reportes.Services.ReporteServices;

@ExtendWith(MockitoExtension.class)
class ReporteServicesTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private GeoClient geoClient;

    @Mock
    private OracleStorageService oracleStorageService;

    @Mock
    private ReporteEventoPublisher reporteEventoPublisher;

    @InjectMocks
    private ReporteServices reporteServices;

    @Test
    void buscarPorId_reporteExistente() {
        Reporte reporte = new Reporte();
        reporte.setIdreporte(10L);
        when(reporteRepository.findById(10L)).thenReturn(Optional.of(reporte));

        Reporte resultado = reporteServices.buscarPorId(10L);

        assertThat(resultado.getIdreporte()).isEqualTo(10L);
    }

    @Test
    void buscarPorId_noExistente() {
        when(reporteRepository.findById(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reporteServices.buscarPorId(20L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Reporte no encontrado");
    }

    @Test
    void eliminar_reporteExistente() {
        when(reporteRepository.existsById(5L)).thenReturn(true);

        reporteServices.eliminar(5L);

        verify(reporteRepository).deleteById(5L);
    }

    @Test
    void eliminar_reporteNoExiste() {
        when(reporteRepository.existsById(6L)).thenReturn(false);

        assertThatThrownBy(() -> reporteServices.eliminar(6L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe el reporte");
    }

    @Test
    void eliminarGeo_conLocalizacionEImagen() {
        Reporte reporte = new Reporte();
        reporte.setIdreporte(1L);
        reporte.setLocalizacionId(99L);
        reporte.setImagenUrl("http://storage/image.jpg");
        reporte.setEstadoReporte(EstadoReporte.ACTIVO);

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        reporteServices.eliminarGeo(1L);

        assertThat(reporte.getEstadoReporte()).isEqualTo(EstadoReporte.CERRADO);
        verify(geoClient).eliminarDTO(99L);
        verify(oracleStorageService).eliminarImagen("http://storage/image.jpg");
        verify(reporteEventoPublisher).publicarReporteCerrado(1L);
    }

    @Test
    void crear_datosValidos() throws IOException {
        ReporteRequest dto = new ReporteRequest();
        dto.setLatitud(-33.45);
        dto.setLongitud(-70.66);
        dto.setTipoReporte("PERDIDA");
        dto.setDescripcion("Perro perdido");
        dto.setContacto("99999999");
        dto.setEspecie("PERRO");
        dto.setRaza("Labrador");
        dto.setColorPrincipal("Marrón");
        dto.setTamanio("GRANDE");
        dto.setSexo("MACHO");
        dto.setEdadAproximada(4);

        when(geoClient.crear(any(GeoRequest.class))).thenReturn(new GeoResponse(555L));
        when(oracleStorageService.subirImagen(any(MultipartFile.class))).thenReturn("http://storage/img.jpg");
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(invocation -> {
            Reporte input = invocation.getArgument(0);
            input.setIdreporte(42L);
            return input;
        });

        MockMultipartFile imagen = new MockMultipartFile(
                "imagen",
                "foto.jpg",
                "image/jpeg",
                "contenido".getBytes());

        Reporte resultado = reporteServices.crear(dto, imagen, 12345678);

        assertThat(resultado.getIdreporte()).isEqualTo(42L);
        assertThat(resultado.getLocalizacionId()).isEqualTo(555L);
        assertThat(resultado.getImagenUrl()).isEqualTo("http://storage/img.jpg");
        assertThat(resultado.getTipoReporte()).isEqualTo(TipoReporte.PERDIDA);
        assertThat(resultado.getEspecie()).isEqualTo(Especie.PERRO);
        assertThat(resultado.getEstadoReporte()).isEqualTo(EstadoReporte.ACTIVO);
        verify(reporteEventoPublisher).publicarReporteNuevo(any());
    }

    @Test
    void crear_especieOTRO_sinOtraEspecie() {
        ReporteRequest dto = new ReporteRequest();
        dto.setLatitud(-33.45);
        dto.setLongitud(-70.66);
        dto.setTipoReporte("AVISTAMIENTO");
        dto.setDescripcion("Animal desconocido");
        dto.setContacto("99999999");
        dto.setEspecie("OTRO");
        dto.setTamanio("PEQUENO");
        dto.setSexo("HEMBRA");
        dto.setEdadAproximada(2);
        dto.setOtraEspecie("");

        when(geoClient.crear(any(GeoRequest.class))).thenReturn(new GeoResponse(222L));

        assertThatThrownBy(() -> reporteServices.crear(dto, null, 11111111))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Debe especificar la otra especie");
    }

    @Test
    void listar_reporteConGeo() {
        Reporte reporte = new Reporte();
        reporte.setIdreporte(3L);
        reporte.setLocalizacionId(10L);
        reporte.setTipoReporte(TipoReporte.AVISTAMIENTO);
        reporte.setEstadoReporte(EstadoReporte.ACTIVO);
        reporte.setDescripcion("Prueba");
        reporte.setContacto("90000000");
        reporte.setImagenUrl("http://img.jpg");
        reporte.setEspecie(Especie.GATO);
        reporte.setRaza("Siames");
        reporte.setColorPrincipal("Blanco");
        reporte.setTamanio(Tamanio.PEQUENO);
        reporte.setSexo(Sexo.HEMBRA);
        reporte.setEdadAproximada(1);

        when(reporteRepository.findAll()).thenReturn(List.of(reporte));
        when(geoClient.obtener(10L)).thenReturn(new GeoDTO(10L, -33.5, -70.6));

        List<ReporteGeoDTO> result = reporteServices.listar();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLatitud()).isEqualTo(-33.5);
        assertThat(result.get(0).getLongitud()).isEqualTo(-70.6);
    }

    @Test
    void filtrarPorTipo_reporte() {
        Reporte reporte = new Reporte();
        reporte.setIdreporte(4L);
        reporte.setLocalizacionId(20L);
        reporte.setTipoReporte(TipoReporte.ENCONTRADA);
        reporte.setEstadoReporte(EstadoReporte.ACTIVO);
        reporte.setDescripcion("Ejemplo");
        reporte.setContacto("91111111");
        reporte.setImagenUrl(null);
        reporte.setEspecie(Especie.GATO);
        reporte.setRaza("Mestizo");
        reporte.setColorPrincipal("Negro");
        reporte.setTamanio(Tamanio.MEDIANO);
        reporte.setSexo(Sexo.MACHO);
        reporte.setEdadAproximada(5);

        when(reporteRepository.findByTipoReporte(TipoReporte.ENCONTRADA)).thenReturn(List.of(reporte));
        when(geoClient.obtener(20L)).thenReturn(new GeoDTO(20L, -33.0, -70.0));

        List<ReporteGeoDTO> result = reporteServices.filtrarPorTipo(TipoReporte.ENCONTRADA);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipoReporte()).isEqualTo("ENCONTRADA");
    }

    @Test
    void renovarReporte_reporteCerrado() {
        Reporte reporte = new Reporte();
        reporte.setIdreporte(7L);
        reporte.setEstadoReporte(EstadoReporte.CERRADO);
        reporte.setTipoReporte(TipoReporte.PERDIDA);

        when(reporteRepository.findById(7L)).thenReturn(Optional.of(reporte));

        assertThatThrownBy(() -> reporteServices.renovarReporte(7L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se puede renovar un reporte cerrado");
    }

    @Test
    void actualizarEstado_reporteExistente() {
        Reporte reporte = new Reporte();
        reporte.setIdreporte(8L);
        reporte.setEstadoReporte(EstadoReporte.ACTIVO);

        when(reporteRepository.findById(8L)).thenReturn(Optional.of(reporte));
        when(reporteRepository.save(any())).thenReturn(reporte);

        reporteServices.actualizarEstado(8L, EstadoReporte.RESUELTO);

        assertThat(reporte.getEstadoReporte()).isEqualTo(EstadoReporte.RESUELTO);
        verify(reporteRepository).save(reporte);
    }

    @Test
    void actualizarEstado_reporteNoExistente() {
        when(reporteRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reporteServices.actualizarEstado(9L, EstadoReporte.ACTIVO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Reporte no encontrado");
    }
}
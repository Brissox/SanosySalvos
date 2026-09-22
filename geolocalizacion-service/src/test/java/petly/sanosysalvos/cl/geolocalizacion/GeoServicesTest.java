package petly.sanosysalvos.cl.geolocalizacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoDTO;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoRequest;
import petly.sanosysalvos.cl.geolocalizacion.DTO.GeoResponse;
import petly.sanosysalvos.cl.geolocalizacion.Model.Localizacion;
import petly.sanosysalvos.cl.geolocalizacion.Repository.GeoRepository;
import petly.sanosysalvos.cl.geolocalizacion.Services.GeoServices;

@ExtendWith(MockitoExtension.class)
class GeoServicesTest {

    @Mock
    private GeoRepository repo;

    @InjectMocks
    private GeoServices service;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    void crear_localizacion() {
        GeoRequest request = new GeoRequest(-33.45, -70.66);

        when(repo.save(any(Localizacion.class))).thenAnswer(invocation -> {
            Localizacion localizacion = invocation.getArgument(0);
            localizacion.setId(1L);
            return localizacion;
        });

        GeoResponse response = service.crear(request);

        assertThat(response.getId()).isEqualTo(1L);
        verify(repo).save(any(Localizacion.class));
    }

    @Test
    void obtener_geoDTO() {
        Localizacion localizacion = new Localizacion();
        localizacion.setId(1L);
        localizacion.setUbicacion(geometryFactory.createPoint(new Coordinate(-70.66, -33.45)));

        when(repo.findById(1L)).thenReturn(Optional.of(localizacion));

        GeoDTO resultado = service.obtener(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getLatitud()).isEqualTo(-33.45);
        assertThat(resultado.getLongitud()).isEqualTo(-70.66);
    }

    @Test
    void obtener_noExistente() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Geo no encontrada");
    }

    @Test
    void buscarTodos_geoDTO() {
        Localizacion localizacion = new Localizacion();
        localizacion.setId(1L);
        localizacion.setUbicacion(geometryFactory.createPoint(new Coordinate(-70.66, -33.45)));

        when(repo.findAll()).thenReturn(List.of(localizacion));

        List<GeoDTO> resultado = service.buscarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getLatitud()).isEqualTo(-33.45);
        assertThat(resultado.get(0).getLongitud()).isEqualTo(-70.66);
    }

    @Test
    void eliminarGeo_existente() {
        when(repo.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void eliminarGeo_noExistente() {
        when(repo.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.eliminar(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe la geolocalización");
    }

    @Test
    void eliminarDTO_existente() {
        when(repo.existsById(1L)).thenReturn(true);

        service.eliminarDTO(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void eliminarDTO_noExistenten() {
        when(repo.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.eliminarDTO(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Localización no encontrada");
    }
}
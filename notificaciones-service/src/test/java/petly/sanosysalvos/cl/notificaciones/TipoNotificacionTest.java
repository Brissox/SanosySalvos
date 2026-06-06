package petly.sanosysalvos.cl.notificaciones;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import petly.sanosysalvos.cl.notificaciones.Model.TipoNotificacion;

class TipoNotificacionTest {

    @Test
    void tipoNotificacion_tieneValoresEsperados() {
        assertThat(TipoNotificacion.values()).contains(
                TipoNotificacion.REPORTE_CREADO,
                TipoNotificacion.REPORTE_ACTUALIZADO,
                TipoNotificacion.MASCOTA_ENCONTRADA,
                TipoNotificacion.COINCIDENCIA_POTENCIAL,
                TipoNotificacion.COMENTARIO,
                TipoNotificacion.SISTEMA,
                TipoNotificacion.REPORTE_PROXIMO_VENCER);
    }

    @Test
    void tipoNotificacion_valueOfFunciona() {
        assertThat(TipoNotificacion.valueOf("COINCIDENCIA_POTENCIAL"))
                .isEqualTo(TipoNotificacion.COINCIDENCIA_POTENCIAL);
    }
}
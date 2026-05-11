package petly.sanosysalvos.cl.reportes.Config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.reporte-nuevo}")
    private String queueReporteNuevo;

    @Value("${rabbitmq.queue.reporte-estado-actualizar}")
    private String queueReporteEstadoActualizar;

    @Value("${rabbitmq.queue.reporte-cerrado}")
    private String queueReporteCerrado;

    @Value("${rabbitmq.queue.reporte-proximo-vencer}")
    private String queueReporteProximoVencer;

    @Bean
    public Queue queueReporteNuevo() {
        return new Queue(queueReporteNuevo, true);
    }

    @Bean
    public Queue queueReporteEstadoActualizar() {
        return new Queue(queueReporteEstadoActualizar, true);
    }

    @Bean
    public Queue queueReporteCerrado() {
        return new Queue(queueReporteCerrado, true);
    }

    @Bean
    public Queue queueReporteProximoVencer() {
        return new Queue(queueReporteProximoVencer, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}

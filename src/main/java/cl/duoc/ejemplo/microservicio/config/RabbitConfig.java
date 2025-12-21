package cl.duoc.ejemplo.microservicio.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String COLA_TICKETS_OK = "cola-tickets-ok";
    public static final String COLA_TICKETS_ERROR = "cola-tickets-error";

    @Bean
    public Queue colaTicketsOk() {
        return new Queue(COLA_TICKETS_OK, true);
    }

    @Bean
    public Queue colaTicketsError() {
        return new Queue(COLA_TICKETS_ERROR, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
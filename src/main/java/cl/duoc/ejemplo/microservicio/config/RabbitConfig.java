package cl.duoc.ejemplo.microservicio.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // === Exchanges ===
    public static final String EXCHANGE_TICKETS = "tickets.exchange";
    public static final String DLX_TICKETS = "tickets.dlx";

    // === Queues ===
    public static final String COLA_TICKETS_OK = "cola-tickets-ok";
    public static final String COLA_TICKETS_DLQ = "cola-tickets-dlq";

    // === Routing Keys ===
    public static final String RK_OK = "tickets.ok";
    public static final String RK_ERROR = "tickets.error";

    // Exchange principal
    @Bean
    public DirectExchange ticketsExchange() {
        return new DirectExchange(EXCHANGE_TICKETS);
    }

    // Exchange DLQ
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_TICKETS);
    }

    // Cola OK con DLQ configurada
    @Bean
    public Queue colaTicketsOk() {
        return QueueBuilder.durable(COLA_TICKETS_OK)
                .withArgument("x-dead-letter-exchange", DLX_TICKETS)
                .withArgument("x-dead-letter-routing-key", RK_ERROR)
                .build();
    }

    // Cola DLQ
    @Bean
    public Queue colaTicketsDlq() {
        return QueueBuilder.durable(COLA_TICKETS_DLQ).build();
    }

    // Binding OK
    @Bean
    public Binding okBinding() {
        return BindingBuilder
                .bind(colaTicketsOk())
                .to(ticketsExchange())
                .with(RK_OK);
    }

    // Binding DLQ
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder
                .bind(colaTicketsDlq())
                .to(deadLetterExchange())
                .with(RK_ERROR);
    }
}


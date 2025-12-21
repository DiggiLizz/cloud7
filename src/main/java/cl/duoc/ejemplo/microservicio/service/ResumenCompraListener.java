package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
import cl.duoc.ejemplo.microservicio.dto.ResumenCompraMessage;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class ResumenCompraListener {

    private final ResumenCompraMqService resumenCompraMqService;

    public ResumenCompraListener(ResumenCompraMqService resumenCompraMqService) {
        this.resumenCompraMqService = resumenCompraMqService;
    }

    @RabbitListener(queues = RabbitConfig.COLA_TICKETS_OK)
    public void consumirMensaje(
            ResumenCompraMessage msg,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag
    ) throws Exception {

        try {
            // Simulación de error para probar DLQ
            if (msg.getNumeroResumen() < 0) {
                throw new RuntimeException("Error de validación");
            }

            resumenCompraMqService.procesarMensaje(msg);

            // ACK → mensaje OK
            channel.basicAck(tag, false);

        } catch (Exception e) {

            // NACK sin requeue → va AUTOMÁTICAMENTE a la DLQ
            channel.basicNack(tag, false, false);
        }
    }
}
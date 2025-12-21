package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
import cl.duoc.ejemplo.microservicio.dto.BffResumenRequest;
import cl.duoc.ejemplo.microservicio.dto.ResumenCompraMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BffColasService {

    private final RabbitTemplate rabbitTemplate;

    public BffColasService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // PRODUCTOR OK
    public void producirResumen(BffResumenRequest req) {

        ResumenCompraMessage msg = construirMensaje(req);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_TICKETS,
                RabbitConfig.RK_OK,
                msg
        );
    }

    private ResumenCompraMessage construirMensaje(BffResumenRequest req) {

        ResumenCompraMessage msg = new ResumenCompraMessage();
        msg.setNumeroResumen(req.getNumeroResumen());
        msg.setNombreArchivo(req.getNombreArchivo());
        msg.setCarpetaResumen(req.getCarpetaResumen());
        msg.setS3Key(req.getS3Key());
        msg.setFechaRegistro(
                req.getFechaRegistro() != null
                        ? req.getFechaRegistro()
                        : LocalDateTime.now()
        );

        return msg;
    }
}
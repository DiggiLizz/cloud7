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
    private final ResumenCompraMqService resumenCompraMqService;

    public BffColasService(RabbitTemplate rabbitTemplate,
                            ResumenCompraMqService resumenCompraMqService) {
        this.rabbitTemplate = rabbitTemplate;
        this.resumenCompraMqService = resumenCompraMqService;
    }

    // PRODUCTOR: envía un mensaje a la cola OK
    public void producirResumen(BffResumenRequest req) {

        ResumenCompraMessage msg = new ResumenCompraMessage();
        msg.setNumeroResumen(req.getNumeroResumen());
        msg.setNombreArchivo(req.getNombreArchivo());
        msg.setCarpetaResumen(req.getCarpetaResumen());
        msg.setS3Key(req.getS3Key());
        msg.setFechaRegistro(req.getFechaRegistro() != null ? req.getFechaRegistro() : LocalDateTime.now());

        rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_OK, msg);
    }

    // CONSUMIDOR (HTTP): toma 1 mensaje desde la cola OK y lo procesa
    public String consumirYProcesarUno() {

        Object obj = rabbitTemplate.receiveAndConvert(RabbitConfig.COLA_TICKETS_OK);

        if (obj == null) {
            return "No hay mensajes en la cola OK.";
        }

        if (!(obj instanceof ResumenCompraMessage)) {
            return "Mensaje recibido pero tipo inesperado: " + obj.getClass().getName();
        }

        ResumenCompraMessage msg = (ResumenCompraMessage) obj;
        resumenCompraMqService.procesarMensaje(msg);

        return "Mensaje consumido y procesado. numeroResumen=" + msg.getNumeroResumen();
    }
}

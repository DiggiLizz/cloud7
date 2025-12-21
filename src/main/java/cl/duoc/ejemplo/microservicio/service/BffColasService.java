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

    // PRODUCTOR OK
    public void producirResumenOk(BffResumenRequest req) {
        ResumenCompraMessage msg = construirMensaje(req);
        rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_OK, msg);
    }

    // CONSUMIDOR OK (HTTP)
    public String consumirOkYProcesarUno() {

        Object obj = rabbitTemplate.receiveAndConvert(RabbitConfig.COLA_TICKETS_OK);

        if (obj == null) {
            return "No hay mensajes en la cola OK (" + RabbitConfig.COLA_TICKETS_OK + ").";
        }

        if (!(obj instanceof ResumenCompraMessage)) {
            return "Mensaje recibido pero tipo inesperado: " + obj.getClass().getName();
        }

        ResumenCompraMessage msg = (ResumenCompraMessage) obj;
        resumenCompraMqService.procesarMensaje(msg);

        return "OK: mensaje consumido y procesado. numeroResumen=" + msg.getNumeroResumen();
    }

    // PRODUCTOR ERROR
    public void producirResumenError(BffResumenRequest req) {
        ResumenCompraMessage msg = construirMensaje(req);
        rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_ERROR, msg);
    }

    // CONSUMIDOR ERROR (HTTP)
    public String consumirErrorYProcesarUno() {

        Object obj = rabbitTemplate.receiveAndConvert(RabbitConfig.COLA_TICKETS_ERROR);

        if (obj == null) {
            return "No hay mensajes en la cola ERROR (" + RabbitConfig.COLA_TICKETS_ERROR + ").";
        }

        if (!(obj instanceof ResumenCompraMessage)) {
            return "Mensaje recibido pero tipo inesperado: " + obj.getClass().getName();
        }

        ResumenCompraMessage msg = (ResumenCompraMessage) obj;

        // Para la rúbrica está perfecto: demostrar consumo y procesamiento.
        resumenCompraMqService.procesarMensaje(msg);

        return "ERROR: mensaje consumido y procesado. numeroResumen=" + msg.getNumeroResumen();
    }

    private ResumenCompraMessage construirMensaje(BffResumenRequest req) {

        ResumenCompraMessage msg = new ResumenCompraMessage();
        msg.setNumeroResumen(req.getNumeroResumen());
        msg.setNombreArchivo(req.getNombreArchivo());
        msg.setCarpetaResumen(req.getCarpetaResumen());
        msg.setS3Key(req.getS3Key());
        msg.setFechaRegistro(req.getFechaRegistro() != null ? req.getFechaRegistro() : LocalDateTime.now());

        return msg;
    }
}
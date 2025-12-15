package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
import cl.duoc.ejemplo.microservicio.dto.ResumenCompraMessage;
import cl.duoc.ejemplo.microservicio.service.ResumenCompraMqService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mq")
public class MqController {

    private final RabbitTemplate rabbitTemplate; //para cola RabbitMQ
    private final ResumenCompraMqService resumenCompraMqService;

    public MqController(RabbitTemplate rabbitTemplate,
                        ResumenCompraMqService resumenCompraMqService) {
        this.rabbitTemplate = rabbitTemplate;
        this.resumenCompraMqService = resumenCompraMqService;
    }

    /**
     * Endpoint que consume todos los mensajes pendientes en la cola "cola-tickets-ok"
     * y los guarda en la base de datos usando ResumenCompraMqService.
     */
    @PostMapping("/procesar-resumenes")
    public ResponseEntity<String> procesarResumenesPendientes() {

        int procesados = 0;

        while (true) {
            Object message = rabbitTemplate.receiveAndConvert(RabbitConfig.COLA_TICKETS_OK);
            if (message == null) {
                break; // no quedan mensajes en la cola
            }

            if (message instanceof ResumenCompraMessage resumenMessage) {
                resumenCompraMqService.procesarMensaje(resumenMessage);
                procesados++;
            }
        }

        String respuesta = "Se procesaron " + procesados + " mensajes desde la cola '"
                + RabbitConfig.COLA_TICKETS_OK + "'.";
        return ResponseEntity.ok(respuesta);
    }
}

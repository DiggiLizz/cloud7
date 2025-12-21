package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
import cl.duoc.ejemplo.microservicio.dto.ResumenCompraMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumenCompraListener {

    private final ResumenCompraMqService resumenCompraMqService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.COLA_TICKETS_OK)
    public void consumirMensaje(ResumenCompraMessage msg) {

        try {
            // Simulación de error para probar derivación a cola de error
            if (msg.getNumeroResumen() < 0) {
                throw new RuntimeException("Error de validación: numeroResumen < 0");
            }

            resumenCompraMqService.procesarMensaje(msg);

            // ✅ NO hay ACK manual: Spring hace ACK automático (AUTO)

            log.info("[OK] Mensaje procesado correctamente: {}", msg);

        } catch (Exception e) {

            log.error("[ERROR] Falló procesamiento OK, enviando a cola ERROR. msg={}", msg, e);

            // ✅ Derivar a cola ERROR de forma explícita
            rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_ERROR, msg);

            // ✅ Lanzar excepción para que quede registro del fallo en el listener
            throw new RuntimeException("Error en consumidor OK; mensaje reenviado a cola ERROR", e);
        }
    }
}
package cl.duoc.ejemplo.microservicio.service;

import cl.duoc.ejemplo.microservicio.config.RabbitConfig;
import cl.duoc.ejemplo.microservicio.dto.ResumenCompraMessage;
import cl.duoc.ejemplo.microservicio.model.ResumenCompra;
import cl.duoc.ejemplo.microservicio.repo.ResumenCompraRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResumenCompraMqService {

    private final ResumenCompraRepository resumenRepo;
    private final RabbitTemplate rabbitTemplate;

    public ResumenCompraMqService(ResumenCompraRepository resumenRepo,
                                RabbitTemplate rabbitTemplate) {
        this.resumenRepo = resumenRepo;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Procesa un mensaje de la cola "OK":
     *  - Guarda/actualiza el resumen en la tabla RESUMEN_COMPRA.
     *  - Si hay error, reenvía el mensaje a la cola de errores.
     */
    public void procesarMensaje(ResumenCompraMessage message) {
        try {
            ResumenCompra resumen = resumenRepo.findByNumeroResumen(message.getNumeroResumen())
                    .orElseGet(ResumenCompra::new);

            resumen.setNumeroResumen(message.getNumeroResumen());
            resumen.setNombreArchivo("resumen_compra_" + message.getNumeroResumen() + ".txt");
            resumen.setCarpetaResumen(String.valueOf(message.getNumeroResumen()));
            resumen.setS3Key(message.getS3Key());
            resumen.setFechaRegistro(LocalDateTime.now());

            resumenRepo.save(resumen);

        } catch (Exception ex) {
            // En caso de error, reenviamos el mensaje a la cola de errores
            rabbitTemplate.convertAndSend(RabbitConfig.COLA_TICKETS_ERROR, message);
        }
    }
}
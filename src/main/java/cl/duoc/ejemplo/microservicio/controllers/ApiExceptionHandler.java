package cl.duoc.ejemplo.microservicio.controllers;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // 400 - Errores de validación del @Valid (DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Validación fallida");
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }

        body.put("errors", errors);

        // Log de validación (útil para auditoría)
        log.warn("Validación fallida: {}", errors);

        return ResponseEntity.badRequest().body(body);
    }

    // 400 - Validaciones por ConstraintViolation (por ejemplo en @RequestParam / @PathVariable)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Validación fallida");
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getConstraintViolations().forEach(v -> errors.put(
                v.getPropertyPath().toString(),
                v.getMessage()
        ));

        body.put("errors", errors);

        log.warn("ConstraintViolation: {}", errors);

        return ResponseEntity.badRequest().body(body);
    }

    // 404 - Cuando el recurso no existe (su caso del eventoId)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {

        Map<String, Object> body = base(HttpStatus.NOT_FOUND, ex.getMessage());

        log.warn("Recurso no encontrado / solicitud inválida: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 500 - Fallback (evita respuestas “ciegas”) + LOG COMPLETO del error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {

        // CLAVE: imprime stacktrace completo en docker logs
        log.error("Error interno no controlado", ex);

        Map<String, Object> body = base(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno no controlado");

        // (Opcional en DEV) si quiere ver el tipo de excepción sin exponer detalles sensibles:
        // body.put("exception", ex.getClass().getName());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> base(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
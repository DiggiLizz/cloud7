package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.CompraRequest;
import cl.duoc.ejemplo.microservicio.dto.CompraResponse;
import cl.duoc.ejemplo.microservicio.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final CompraService compraService;

    public TicketController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping("/compra")
    public CompraResponse comprar(@Valid @RequestBody CompraRequest request,
                                    @AuthenticationPrincipal Jwt jwt) {

        String usuario = "anon";

        if (jwt != null) {
            usuario = jwt.getClaimAsString("preferred_username");
            if (usuario == null || usuario.isBlank()) {
                usuario = jwt.getSubject();
            }
        }

        return compraService.comprar(request, usuario);
    }
}

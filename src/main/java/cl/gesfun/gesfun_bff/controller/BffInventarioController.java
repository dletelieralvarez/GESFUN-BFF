package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.AnulacionMovimientoInventario;
import cl.gesfun.gesfun_bff.model.EntradaInventario;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.SalidaInventario;
import cl.gesfun.gesfun_bff.model.SalidaInventarioFacturacion;
import cl.gesfun.gesfun_bff.service.InventarioBffService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventario")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffInventarioController extends BffResponseSupport {

    private final InventarioBffService inventarioBffService;

    public BffInventarioController(InventarioBffService inventarioBffService) {
        this.inventarioBffService = inventarioBffService;
    }

    @PostMapping("/entradas")
    public ResponseEntity<FrontendResponse<Object>> registrarEntrada(
            @Valid @RequestBody EntradaInventario request,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(inventarioBffService.registrarEntrada(request, jwt));
    }

    @PostMapping("/salidas")
    public ResponseEntity<FrontendResponse<Object>> registrarSalida(
            @Valid @RequestBody SalidaInventario request,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(inventarioBffService.registrarSalida(request, jwt));
    }

    @PostMapping("/salidas/facturacion")
    public ResponseEntity<FrontendResponse<Object>> registrarSalidaPorFacturacion(
            @Valid @RequestBody SalidaInventarioFacturacion request,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(inventarioBffService.registrarSalidaPorFacturacion(request, jwt));
    }

    @PatchMapping("/movimientos/{movimientoUuid}/anular")
    public ResponseEntity<FrontendResponse<Object>> anularMovimiento(
            @PathVariable String movimientoUuid,
            @Valid @RequestBody AnulacionMovimientoInventario request,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(inventarioBffService.anularMovimiento(movimientoUuid, request, jwt));
    }

    @GetMapping("/stock")
    public ResponseEntity<FrontendResponse<Object>> consultarStockSucursal(
            @RequestParam String sucursalUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(inventarioBffService.consultarStockSucursal(sucursalUuid, jwt));
    }

    @GetMapping("/stock/productos/{productoUuid}")
    public ResponseEntity<FrontendResponse<Object>> consultarStockProducto(
            @PathVariable String productoUuid,
            @RequestParam String sucursalUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(inventarioBffService.consultarStockProducto(productoUuid, sucursalUuid, jwt));
    }

    @GetMapping("/reportes/kardex")
    public ResponseEntity<FrontendResponse<Object>> consultarKardex(
            @RequestParam String productoUuid,
            @RequestParam String sucursalUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(inventarioBffService.consultarKardex(productoUuid, sucursalUuid, jwt));
    }
}

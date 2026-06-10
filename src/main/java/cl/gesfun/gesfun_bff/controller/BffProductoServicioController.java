package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.ProductoServicio;
import cl.gesfun.gesfun_bff.service.ProductoServicioBffService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos-servicios")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffProductoServicioController extends BffResponseSupport {

    private final ProductoServicioBffService productoServicioBffService;

    public BffProductoServicioController(ProductoServicioBffService productoServicioBffService) {
        this.productoServicioBffService = productoServicioBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) { return responder(productoServicioBffService.listar(jwt)); }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(productoServicioBffService.buscarPorUuid(uuid, jwt)); }

    @GetMapping("/empresa/{empresaUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorEmpresa(@PathVariable String empresaUuid, @AuthenticationPrincipal Jwt jwt) { return responder(productoServicioBffService.listarPorEmpresa(empresaUuid, jwt)); }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody ProductoServicio productoServicio, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(productoServicioBffService.crear(productoServicio, jwt)); }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody ProductoServicio productoServicio, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(productoServicioBffService.actualizar(uuid, productoServicio, jwt)); }

    @PatchMapping("/{uuid}/desactivar")
    public ResponseEntity<FrontendResponse<Object>> desactivar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(productoServicioBffService.desactivar(uuid, jwt)); }
}

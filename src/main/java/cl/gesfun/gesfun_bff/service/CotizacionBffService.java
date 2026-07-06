package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.Cotizacion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class CotizacionBffService extends CrudBffService {

    private static final String ROL_CLIENTE = "CLIENTE";
    private static final String ROL_FALLECIDO = "FALLECIDO";

    public CotizacionBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/cotizaciones", proxyService, objectMapper);
    }

    @Override
    public ResponseEntity<Object> crear(Object request, Jwt jwt) throws JsonProcessingException {
        if (request instanceof Cotizacion cotizacion) {
            return forward("", HttpMethod.POST, toJson(cotizacionConRolesFuncionales(cotizacion)), jwt);
        }

        return super.crear(request, jwt);
    }

    public ResponseEntity<Object> listarPorSucursal(String sucursalUuid, Jwt jwt) {
        return buscarPorRuta("/sucursal/" + sucursalUuid, jwt);
    }

    public ResponseEntity<Object> actualizarEstado(String uuid, Object request, Jwt jwt)
            throws com.fasterxml.jackson.core.JsonProcessingException {
        return forward("/" + uuid + "/estado", org.springframework.http.HttpMethod.PATCH, toJson(request), jwt);
    }

    private Cotizacion cotizacionConRolesFuncionales(Cotizacion cotizacion) {
        return new Cotizacion(
                cotizacion.sucursalUuid(),
                cotizacion.planUuid(),
                cotizacion.formaPagoUuid(),
                cotizacion.motivoFallecimientoUuid(),
                cotizacion.fecha(),
                cotizacion.fechaValidez(),
                cotizacion.observacion(),
                cotizacion.fechaFallecimiento(),
                cotizacion.horaFallecimiento(),
                cotizacion.lugarFallecimiento(),
                cotizacion.pagador() != null ? cotizacion.pagador().conRol(ROL_CLIENTE) : null,
                cotizacion.fallecido() != null ? cotizacion.fallecido().conRol(ROL_FALLECIDO) : null,
                cotizacion.detalles()
        );
    }
}

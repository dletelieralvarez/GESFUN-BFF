package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class CotizacionBffService extends CrudBffService {

    public CotizacionBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/cotizaciones", proxyService, objectMapper);
    }

    public ResponseEntity<Object> listarPorSucursal(String sucursalUuid, Jwt jwt) {
        return buscarPorRuta("/sucursal/" + sucursalUuid, jwt);
    }

    public ResponseEntity<Object> actualizarEstado(String uuid, Object request, Jwt jwt)
            throws com.fasterxml.jackson.core.JsonProcessingException {
        return forward("/" + uuid + "/estado", org.springframework.http.HttpMethod.PATCH, toJson(request), jwt);
    }
}

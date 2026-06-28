package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class PagoBffService extends CrudBffService {

    public PagoBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/pagos", proxyService, objectMapper);
    }

    public ResponseEntity<Object> listarPorCotizacion(String cotizacionUuid, Jwt jwt) {
        return buscarPorRuta("/cotizacion/" + cotizacionUuid, jwt);
    }

    public ResponseEntity<Object> anular(String uuid, Jwt jwt) {
        return forward("/" + uuid + "/anular", HttpMethod.PATCH, null, jwt);
    }
}

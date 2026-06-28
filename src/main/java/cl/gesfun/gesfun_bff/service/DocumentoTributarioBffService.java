package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class DocumentoTributarioBffService extends CrudBffService {

    public DocumentoTributarioBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/documentos-tributarios", proxyService, objectMapper);
    }

    public ResponseEntity<Object> listarPorCotizacion(String cotizacionUuid, Jwt jwt) {
        return buscarPorRuta("/cotizacion/" + cotizacionUuid, jwt);
    }

    public ResponseEntity<Object> buscarPorPago(String pagoUuid, Jwt jwt) {
        return buscarPorRuta("/pago/" + pagoUuid, jwt);
    }

    public ResponseEntity<Object> emitir(Object request, Jwt jwt) throws JsonProcessingException {
        return forward("/emitir", HttpMethod.POST, toJson(request), jwt);
    }

    public ResponseEntity<Object> reenviar(String uuid, Jwt jwt) {
        return forward("/" + uuid + "/reenviar", HttpMethod.POST, null, jwt);
    }

    public ResponseEntity<Object> anular(String uuid, Jwt jwt) {
        return forward("/" + uuid + "/anular", HttpMethod.PATCH, null, jwt);
    }
}

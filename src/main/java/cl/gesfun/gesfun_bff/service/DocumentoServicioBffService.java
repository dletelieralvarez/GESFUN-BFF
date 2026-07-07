package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class DocumentoServicioBffService extends CrudBffService {

    public DocumentoServicioBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/documentos-servicio", proxyService, objectMapper);
    }

    public ResponseEntity<Object> listarPorCotizacion(String cotizacionUuid, Jwt jwt) {
        return buscarPorRuta("/cotizacion/" + cotizacionUuid, jwt);
    }
}

package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ServicioFunerarioBffService extends CrudBffService {

    public ServicioFunerarioBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/servicios", proxyService, objectMapper);
    }

    public ResponseEntity<Object> listarPorSucursal(String sucursalUuid, Jwt jwt) {
        return buscarPorRuta("/sucursal/" + sucursalUuid, jwt);
    }

    public ResponseEntity<Object> listarPorEstado(String estado, Jwt jwt) {
        return buscarPorRuta("/estado/" + estado, jwt);
    }

    public ResponseEntity<Object> buscarPorCotizacion(String cotizacionUuid, Jwt jwt) {
        return buscarPorRuta("/cotizacion/" + cotizacionUuid, jwt);
    }

    public ResponseEntity<Object> listarPorCliente(String terceroUuid, Jwt jwt) {
        return buscarPorRuta("/cliente/" + terceroUuid, jwt);
    }
}

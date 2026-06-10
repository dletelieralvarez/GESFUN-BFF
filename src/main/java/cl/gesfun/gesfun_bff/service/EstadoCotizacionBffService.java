package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class EstadoCotizacionBffService extends CrudBffService {

    public EstadoCotizacionBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/estados-cotizacion", proxyService, objectMapper);
    }
}

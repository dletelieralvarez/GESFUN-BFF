package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class TipoMovimientoBffService extends CrudBffService {

    public TipoMovimientoBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/tipos-movimiento", proxyService, objectMapper);
    }
}

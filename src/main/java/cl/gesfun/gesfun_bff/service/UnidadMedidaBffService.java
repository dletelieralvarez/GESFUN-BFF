package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class UnidadMedidaBffService extends CrudBffService {

    public UnidadMedidaBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/unidades-medida", proxyService, objectMapper);
    }
}

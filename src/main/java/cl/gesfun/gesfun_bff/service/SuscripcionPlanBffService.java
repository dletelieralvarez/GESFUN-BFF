package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class SuscripcionPlanBffService extends CrudBffService {

    public SuscripcionPlanBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/suscripcion-planes", proxyService, objectMapper);
    }
}

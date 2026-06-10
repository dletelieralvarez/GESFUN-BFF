package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ComunaBffService extends CrudBffService {

    public ComunaBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/comunas", proxyService, objectMapper);
    }
}

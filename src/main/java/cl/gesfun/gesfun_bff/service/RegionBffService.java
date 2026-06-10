package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class RegionBffService extends CrudBffService {

    public RegionBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/regiones", proxyService, objectMapper);
    }
}

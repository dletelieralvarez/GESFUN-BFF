package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class FormaPagoBffService extends CrudBffService {

    public FormaPagoBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/formas-pago", proxyService, objectMapper);
    }
}

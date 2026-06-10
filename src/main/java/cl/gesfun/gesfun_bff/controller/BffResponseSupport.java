package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import org.springframework.http.ResponseEntity;

abstract class BffResponseSupport {

    static final String ACCESS_AS_USER =
            "hasAuthority('SCOPE_access_as_user') or hasAuthority('SCOPE_https://duocactividadazure.onmicrosoft.com/daead1c3-a4cc-4647-9423-e1fc626d8003/access_as_user')";

    protected ResponseEntity<FrontendResponse<Object>> responder(ResponseEntity<Object> backendResponse) {
        return ResponseEntity.status(backendResponse.getStatusCode())
                .body(FrontendResponse.success(backendResponse.getBody()));
    }
}

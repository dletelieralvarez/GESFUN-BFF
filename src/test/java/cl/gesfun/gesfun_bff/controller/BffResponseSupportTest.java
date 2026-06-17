package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BffResponseSupportTest {

    private final TestResponseSupport support = new TestResponseSupport();

    @Test
    void responderEnvuelveBodyEnFrontendResponseManteniendoStatus() {
        ResponseEntity<Object> backendResponse = ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", 1));

        ResponseEntity<FrontendResponse<Object>> response = support.responderPublico(backendResponse);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getPayload()).isEqualTo(Map.of("id", 1));
        assertThat(response.getBody().getMessage()).isNull();
    }

    private static final class TestResponseSupport extends BffResponseSupport {

        private ResponseEntity<FrontendResponse<Object>> responderPublico(ResponseEntity<Object> backendResponse) {
            return responder(backendResponse);
        }
    }
}

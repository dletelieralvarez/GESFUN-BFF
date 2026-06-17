package cl.gesfun.gesfun_bff.error;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalErrorHandlerTest {

    private final GlobalErrorHandler handler = new GlobalErrorHandler();

    @Test
    void handleBackendErrorsMantieneStatusYMensajeBackend() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                "{\"error\":\"dato invalido\"}".getBytes(),
                null
        );

        ResponseEntity<FrontendResponse<Object>> response = handler.handleBackendErrors(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getPayload()).isNull();
        assertThat(response.getBody().getMessage()).contains("Error al procesar");
        assertThat(response.getBody().getMessage()).contains("dato invalido");
    }

    @Test
    void handleGenericExceptionRetornaInternalServerError() {
        ResponseEntity<FrontendResponse<Object>> response = handler.handleGenericException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Error interno en el BFF: boom");
    }
}

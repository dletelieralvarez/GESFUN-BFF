package cl.gesfun.gesfun_bff.error;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    /*
        Maneja excepciones globales en el BFF.
        Captura errores de backend (RestClientResponseException) y devuelve un FrontendResponse.failure(...).
        Captura excepciones genéricas y devuelve un error interno con mensaje claro.
        Evita respuestas HTML de error y mantiene el formato JSON para el frontend.
    */
   
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<FrontendResponse<Object>> handleBackendErrors(RestClientResponseException ex) {
        String message = "Error al procesar la petición en el servicio de backend.";
        log.warn(
                "BFF capturo error del backend. status={} responseBody={}",
                ex.getRawStatusCode(),
                ex.getResponseBodyAsString()
        );
        return ResponseEntity.status(ex.getRawStatusCode())
                .body(FrontendResponse.failure(message + " " + ex.getResponseBodyAsString()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<FrontendResponse<Object>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(FrontendResponse.failure(ex.getReason()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FrontendResponse<Object>> handleGenericException(Exception ex) {
        String message = "Error interno en el BFF: " + ex.getMessage();
        log.error("BFF capturo error interno no controlado.", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FrontendResponse.failure(message));
    }
}

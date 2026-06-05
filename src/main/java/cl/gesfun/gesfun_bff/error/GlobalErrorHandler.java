package cl.gesfun.gesfun_bff.error;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;

@ControllerAdvice
public class GlobalErrorHandler {

    /*
        Maneja excepciones globales en el BFF.
        Captura errores de backend (RestClientResponseException) y devuelve un FrontendResponse.failure(...).
        Captura excepciones genéricas y devuelve un error interno con mensaje claro.
        Evita respuestas HTML de error y mantiene el formato JSON para el frontend.
    */
   
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<FrontendResponse<Object>> handleBackendErrors(RestClientResponseException ex) {
        String message = "Error al procesar la petición en el servicio de backend.";
        return ResponseEntity.status(ex.getRawStatusCode())
                .body(FrontendResponse.failure(message + " " + ex.getResponseBodyAsString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FrontendResponse<Object>> handleGenericException(Exception ex) {
        String message = "Error interno en el BFF: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FrontendResponse.failure(message));
    }
}

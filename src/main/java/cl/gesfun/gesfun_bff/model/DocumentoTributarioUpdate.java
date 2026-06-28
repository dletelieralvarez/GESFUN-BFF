package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentoTributarioUpdate(
        @Size(max = 25, message = "El estado no puede superar los 25 caracteres")
        String estado,

        @Size(max = 30, message = "El folio no puede superar los 30 caracteres")
        String folio,

        @Size(max = 80, message = "El track id no puede superar los 80 caracteres")
        String trackId,

        @Size(max = 1000, message = "El mensaje de error no puede superar los 1000 caracteres")
        String errorMensaje,

        @Size(max = 500, message = "La url del PDF no puede superar los 500 caracteres")
        String pdfUrl,

        @Size(max = 500, message = "La url del XML no puede superar los 500 caracteres")
        String xmlUrl
) {
}

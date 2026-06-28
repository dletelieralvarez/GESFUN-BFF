package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentoTributarioEmitir(
        @NotBlank(message = "El uuid del pago es obligatorio")
        @Size(max = 36, message = "El uuid del pago no puede superar los 36 caracteres")
        String pagoUuid,

        @NotBlank(message = "El codigo del tipo de documento es obligatorio")
        @Size(max = 15, message = "El codigo del tipo de documento no puede superar los 15 caracteres")
        String tipoDocumentoCodigo,

        @Size(max = 1000, message = "La observacion no puede superar los 1000 caracteres")
        String observacion
) {
}

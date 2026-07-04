package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalidaInventarioFacturacion(
        @NotBlank(message = "El uuid del documento tributario es obligatorio")
        @Size(max = 36, message = "El uuid del documento tributario no puede superar los 36 caracteres")
        String documentoTributarioUuid,

        @NotBlank(message = "El uuid de la cotizacion es obligatorio")
        @Size(max = 36, message = "El uuid de la cotizacion no puede superar los 36 caracteres")
        String cotizacionUuid,

        @NotBlank(message = "El uuid del usuario es obligatorio")
        @Size(max = 36, message = "El uuid del usuario no puede superar los 36 caracteres")
        String usuarioUuid,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaDocumento,

        @Size(max = 30, message = "El numero de factura no puede superar los 30 caracteres")
        String numeroFactura,

        @Size(max = 500, message = "La observacion no puede superar los 500 caracteres")
        String observacion
) {
}

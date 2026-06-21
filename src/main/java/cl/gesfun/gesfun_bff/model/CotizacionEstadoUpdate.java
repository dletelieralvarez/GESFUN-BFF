package cl.gesfun.gesfun_bff.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CotizacionEstadoUpdate(
        @NotBlank(message = "El uuid del estado es obligatorio")
        @Size(max = 36, message = "El uuid del estado no puede superar los 36 caracteres")
        String estadoUuid
) {
}

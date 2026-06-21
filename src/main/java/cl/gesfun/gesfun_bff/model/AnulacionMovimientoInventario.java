package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnulacionMovimientoInventario(
        @NotBlank(message = "El motivo de anulacion es obligatorio")
        @Size(max = 500, message = "El motivo de anulacion no puede superar los 500 caracteres")
        String motivo,
        @NotBlank(message = "El uuid del usuario que anula es obligatorio")
        @Size(max = 36, message = "El uuid del usuario no puede superar los 36 caracteres")
        String usuarioUuid
) {
}

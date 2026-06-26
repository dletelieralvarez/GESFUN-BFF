package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Agenda(
        String uuid,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        String estado,

        @Size(max = 500, message = "La observacion no puede superar los 500 caracteres")
        String observacion,

        String tipoRecursoUuid,
        String tipoRecursoNombre,
        String sucursalUuid,
        String sucursalNombre,
        String cotizacionUuid,
        Integer cotizacionNumero
) {
}

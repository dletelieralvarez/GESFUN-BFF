package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record ServicioFunerarioCreate(
        @Size(max = 30, message = "El folio no puede superar los 30 caracteres")
        String folio,

        @Size(max = 25, message = "El estado no puede superar los 25 caracteres")
        String estado,

        @NotBlank(message = "El uuid de la cotizacion es obligatorio")
        @Size(max = 36, message = "El uuid de la cotizacion no puede superar los 36 caracteres")
        String cotizacionUuid,

        @Size(max = 36, message = "El uuid de la agenda no puede superar los 36 caracteres")
        String agendaUuid,

        LocalDateTime fechaIngreso,
        LocalDateTime fechaVelatorio,
        LocalDateTime fechaCeremonia,
        LocalDateTime fechaTermino,

        @Size(max = 250, message = "El destino no puede superar los 250 caracteres")
        String destino,

        @Size(max = 1000, message = "La observacion no puede superar los 1000 caracteres")
        String observacion
) {
}

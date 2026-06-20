package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Cotizacion(
        @NotBlank(message = "El uuid de la sucursal es obligatorio")
        @Size(max = 36, message = "El uuid de la sucursal no puede superar los 36 caracteres")
        String sucursalUuid,

        @NotBlank(message = "El uuid del plan es obligatorio")
        @Size(max = 36, message = "El uuid del plan no puede superar los 36 caracteres")
        String planUuid,

        @NotBlank(message = "El uuid de la forma de pago es obligatorio")
        @Size(max = 36, message = "El uuid de la forma de pago no puede superar los 36 caracteres")
        String formaPagoUuid,

        @NotBlank(message = "El uuid del motivo de fallecimiento es obligatorio")
        @Size(max = 36, message = "El uuid del motivo de fallecimiento no puede superar los 36 caracteres")
        String motivoFallecimientoUuid,

        LocalDate fecha,
        LocalDate fechaValidez,

        @Size(max = 1000, message = "La observacion no puede superar los 1000 caracteres")
        String observacion,

        LocalDate fechaFallecimiento,
        LocalTime horaFallecimiento,

        @Size(max = 550, message = "El lugar de fallecimiento no puede superar los 550 caracteres")
        String lugarFallecimiento,

        @NotNull(message = "Los datos del pagador son obligatorios")
        @Valid
        CotizacionPersona pagador,

        @NotNull(message = "Los datos del fallecido son obligatorios")
        @Valid
        CotizacionPersona fallecido,

        @NotEmpty(message = "La cotizacion debe incluir al menos un producto o servicio")
        List<@Valid CotizacionDetalle> detalles
) {
}

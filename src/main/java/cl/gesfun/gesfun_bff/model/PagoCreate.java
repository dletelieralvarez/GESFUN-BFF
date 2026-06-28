package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PagoCreate(
        @NotBlank(message = "El uuid de la cotizacion es obligatorio")
        @Size(max = 36, message = "El uuid de la cotizacion no puede superar los 36 caracteres")
        String cotizacionUuid,

        @NotBlank(message = "El uuid de la forma de pago es obligatorio")
        @Size(max = 36, message = "El uuid de la forma de pago no puede superar los 36 caracteres")
        String formaPagoUuid,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        BigDecimal monto,

        LocalDateTime fechaPago,

        @Size(max = 500, message = "La observacion no puede superar los 500 caracteres")
        String observacion
) {
}

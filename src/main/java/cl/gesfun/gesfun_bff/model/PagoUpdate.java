package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PagoUpdate(
        @Size(max = 36, message = "El uuid de la forma de pago no puede superar los 36 caracteres")
        String formaPagoUuid,

        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        BigDecimal monto,

        LocalDateTime fechaPago,

        @Size(max = 25, message = "El estado no puede superar los 25 caracteres")
        String estado,

        @Size(max = 500, message = "La observacion no puede superar los 500 caracteres")
        String observacion
) {
}

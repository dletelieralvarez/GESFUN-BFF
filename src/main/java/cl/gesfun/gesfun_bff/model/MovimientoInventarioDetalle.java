package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MovimientoInventarioDetalle(
        @NotBlank(message = "El uuid del producto es obligatorio")
        @Size(max = 36, message = "El uuid del producto no puede superar los 36 caracteres")
        String productoUuid,
        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor que cero")
        BigDecimal cantidad,
        @NotNull(message = "El costo unitario es obligatorio")
        @DecimalMin(value = "0.00", message = "El costo unitario no puede ser negativo")
        BigDecimal costoUnitario,
        @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
        BigDecimal descuento,
        @Size(max = 500, message = "La observacion no puede superar los 500 caracteres")
        String observacion
) {
}

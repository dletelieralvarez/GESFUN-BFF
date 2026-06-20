package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CotizacionDetalle(
        @NotBlank(message = "El uuid del producto o servicio es obligatorio")
        @Size(max = 36, message = "El uuid del producto o servicio no puede superar los 36 caracteres")
        String productoServicioUuid,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer cantidad,

        @Min(value = 0, message = "El descuento no puede ser negativo")
        Integer descuento,

        @Size(max = 500, message = "La observacion no puede superar los 500 caracteres")
        String observacion
) {
}

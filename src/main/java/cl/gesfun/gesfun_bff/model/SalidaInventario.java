package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalidaInventario(
        @NotBlank(message = "El uuid de la sucursal es obligatorio")
        @Size(max = 36, message = "El uuid de la sucursal no puede superar los 36 caracteres")
        String sucursalUuid,
        @NotBlank(message = "El uuid del tipo de movimiento es obligatorio")
        @Size(max = 36, message = "El uuid del tipo de movimiento no puede superar los 36 caracteres")
        String tipoMovimientoUuid,
        String formaPagoUuid,
        String terceroUuid,
        String retiradoPorUuid,
        @NotBlank(message = "El uuid del usuario es obligatorio")
        @Size(max = 36, message = "El uuid del usuario no puede superar los 36 caracteres")
        String usuarioUuid,
        LocalDate fechaDocumento,
        @Size(max = 30, message = "El numero de guia no puede superar los 30 caracteres")
        String numeroGuia,
        @Size(max = 30, message = "El numero de factura no puede superar los 30 caracteres")
        String numeroFactura,
        @Size(max = 500, message = "La observacion no puede superar los 500 caracteres")
        String observacion,
        @NotEmpty(message = "La salida debe incluir al menos un producto")
        List<@Valid MovimientoInventarioDetalle> detalles
) {
}

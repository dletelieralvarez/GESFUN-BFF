package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServicioFunerario(
        @Size(max = 30, message = "El folio no puede superar los 30 caracteres")
        String folio,

        @Size(max = 250, message = "El nombre del fallecido no puede superar los 250 caracteres")
        String fallecidoNombre,

        @Size(max = 20, message = "El rut del fallecido no puede superar los 20 caracteres")
        String fallecidoRut,

        @Size(max = 25, message = "El estado no puede superar los 25 caracteres")
        String estado,

        LocalDateTime fechaIngreso,
        LocalDateTime fechaVelatorio,
        LocalDateTime fechaCeremonia,
        LocalDateTime fechaTermino,

        @Size(max = 250, message = "El destino no puede superar los 250 caracteres")
        String destino,

        @DecimalMin(value = "0.00", message = "El monto total no puede ser negativo")
        BigDecimal montoTotal,

        @DecimalMin(value = "0.00", message = "El monto pagado no puede ser negativo")
        BigDecimal montoPagado,

        @Size(max = 1000, message = "La observacion no puede superar los 1000 caracteres")
        String observacion,

        @Size(max = 36, message = "El uuid de la cotizacion no puede superar los 36 caracteres")
        String cotizacionUuid,

        @Size(max = 36, message = "El uuid del cliente responsable no puede superar los 36 caracteres")
        String terceroUuid,

        @Size(max = 36, message = "El uuid de la suscripcion del plan no puede superar los 36 caracteres")
        String suscripcionPlanUuid,

        @Size(max = 36, message = "El uuid de la sucursal no puede superar los 36 caracteres")
        String sucursalUuid,

        @Size(max = 36, message = "El uuid de la agenda no puede superar los 36 caracteres")
        String agendaUuid,

        @Size(max = 36, message = "El uuid del motivo de fallecimiento no puede superar los 36 caracteres")
        String motivoFallecimientoUuid,

        @Size(max = 36, message = "El uuid del usuario responsable no puede superar los 36 caracteres")
        String responsableUsuarioUuid
) {
}

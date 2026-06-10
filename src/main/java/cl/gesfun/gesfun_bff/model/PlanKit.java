package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlanKit(
        Integer cantidad,
        Integer unitario,
        String observacion,
        Integer activo,
        String productoServicioUuid,
        String planUuid
) {
}

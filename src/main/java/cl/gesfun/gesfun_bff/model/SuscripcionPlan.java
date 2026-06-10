package cl.gesfun.gesfun_bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SuscripcionPlan(
        String nombre,
        String descripcion,
        Integer valor,
        Integer activo
) {
}
